package org.slimecraft.kits.data.config.dto

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemEnchantments
import io.papermc.paper.datacomponent.item.PotionContents
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.task.Task
import org.slimecraft.bedrock.util.item.ItemBuilder
import org.slimecraft.kits.key
import kotlin.random.Random
import kotlin.random.nextInt
import org.slimecraft.kits.fromHex

data class ItemDto(
    val material: String,
    val name: String = "",
    val amount: List<Int> = listOf(1),
    val pdc: String = "",
    val enchantments: Map<String, List<Int>> = emptyMap(),
    val items: List<ItemDto> = listOf(),
    val potionContents: PotionContentsDto? = null,
    val offhandEffects: List<PotionEffectDto> = listOf()
) { // TODO: later make it a List<EnchantmentDto> and make it work with a custom deserializer
    fun itemStack(): ItemStack {
        val b = ItemBuilder.create()
            .material(Material.valueOf(material.uppercase()))
            .amount(amount[Random.nextInt(0, amount.size)])
            .component(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.map { EnchantmentDto(it.key, it.value) }
                    .associate { it.enchantment() to it.levels[Random.nextInt(0, it.levels.size)] })
            )

        if (!name.isEmpty()) {
            b.name(name.component())
        }

        val key = key(pdc)

        if (!pdc.isEmpty()) {
            b.pdc(key, PersistentDataType.BOOLEAN, true)
        }

        if (potionContents != null) {
            val contents = PotionContents.potionContents()
            if (potionContents.color != null) {
                contents.customColor(fromHex(potionContents.color))
            }
            potionContents.effects.forEach {
                contents.addCustomEffect(it.potionEffect())
            }
            b.component(DataComponentTypes.POTION_CONTENTS, contents)
        }

        for (effect in offhandEffects) {
            Task.builder()
                .repeat(effect.duration.toLong())
                .whenRan {
                    for (player in Bukkit.getOnlinePlayers()) {
                        val offhand = player.inventory.itemInOffHand
                        if (!offhand.persistentDataContainer.has(key)) continue
                        player.addPotionEffect(effect.potionEffect())
                    }
                }
        }

        return b.build()
    }
}

