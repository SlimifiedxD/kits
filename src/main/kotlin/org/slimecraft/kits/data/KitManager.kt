package org.slimecraft.kits.data

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.slimecraft.bedrock.util.item.ItemBuilder
import org.slimecraft.kits.data.config.dto.ItemsDto
import org.slimecraft.kits.data.config.dto.KitSettings
import kotlin.random.Random
import kotlin.random.nextInt

class KitManager(val kitSettings: KitSettings) {
    fun give(p: Player) {
        p.inventory.addItem(*selectRandom(kitSettings.weapons))
        p.inventory.addItem(*selectRandom(kitSettings.rangedWeapons))
        p.inventory.addItem(*selectRandom(kitSettings.misc))
        p.inventory.addItem(*selectRandom(kitSettings.tools))
        p.inventory.addItem(*selectRandom(kitSettings.food))
        p.inventory.setItem(EquipmentSlot.OFF_HAND, selectRandom(kitSettings.offhand)[0])
        p.inventory.setItem(EquipmentSlot.HEAD, selectRandom(kitSettings.helmets)[0])
        p.inventory.setItem(EquipmentSlot.CHEST, selectRandom(kitSettings.chestplates)[0])
        p.inventory.setItem(EquipmentSlot.LEGS, selectRandom(kitSettings.leggings)[0])
        p.inventory.setItem(EquipmentSlot.FEET, selectRandom(kitSettings.boots)[0])
    }

    private fun selectRandom(dto: ItemsDto): Array<ItemStack> {
        val items: MutableList<ItemStack> = mutableListOf()
        val numberOfItems = (dto.minItems..dto.maxItems).random()
        repeat(numberOfItems) {
            items += getRandomItems(dto)
        }
        return items.toTypedArray()
    }

    private fun getRandomItems(dto: ItemsDto): List<ItemStack> {
        val item = dto.items[Random.nextInt(0, dto.items.size)]
        val items = mutableListOf(item.itemStack())
        for (item in item.items) {
            items.add(item.itemStack())
        }
        return items
    }
}