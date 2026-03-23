package org.slimecraft.kits

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.slimecraft.bedrock.event.EventNode
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.util.item.ItemBuilder
import java.util.UUID

val mapResetWandKey = key("map_reset_wand")

enum class KitsItem(val supplier: () -> ItemStack, val init: () -> Unit) {
    MAP_RESET_WAND({
        ItemBuilder
            .create()
            .material(Material.GOLDEN_AXE)
            .name("<yellow>Map Reset Wand")
            .pdc(mapResetWandKey, PersistentDataType.BOOLEAN, true)
            .build()
    }, {
        EventNode.global().addListener(PlayerInteractEvent::class.java, {
            val item = it.item ?: return@addListener
            if (!item.persistentDataContainer.has(mapResetWandKey)) return@addListener
            val block = it.clickedBlock ?: return@addListener
            val p = it.player
            if (it.hand != EquipmentSlot.HAND) return@addListener
            if (it.action == Action.LEFT_CLICK_BLOCK) {
                it.isCancelled = true
                setLeftClickPos(p, block)
                p.sendMessage("<green>Set pos 1".component())
            } else if (it.action == Action.RIGHT_CLICK_BLOCK) {
                it.isCancelled = true
                setRightClickPos(p, block)
                p.sendMessage("<green>Set pos 2".component())
            }
        })
    })
}