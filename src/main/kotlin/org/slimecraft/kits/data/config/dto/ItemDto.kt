package org.slimecraft.kits.data.config.dto

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.util.item.ItemBuilder
import org.slimecraft.kits.key
import kotlin.random.Random
import kotlin.random.nextInt

data class ItemDto(val material: String, val name: String = "", val amount: List<Int> = listOf(1), val pdc: String = "", val enchantments: Map<String, List<Int>> = emptyMap(), val items: List<ItemDto> = listOf()) { // TODO: later make it a List<EnchantmentDto> and make it work with a custom deserializer
    fun itemStack(): ItemStack {
        val b = ItemBuilder.create()
            .material(Material.valueOf(material.uppercase()))
            .amount(amount[Random.nextInt(0, amount.size)])
            .component(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(enchantments.map { EnchantmentDto(it.key, it.value) }.associate {it.enchantment() to it.levels[Random.nextInt(0, it.levels.size)]}))

        if (!name.isEmpty()) {
            b.name(name.component())
        }

        if (!pdc.isEmpty()) {
            b.pdc(key(pdc), PersistentDataType.BOOLEAN, true)
        }

        return b.build()
    }
}

