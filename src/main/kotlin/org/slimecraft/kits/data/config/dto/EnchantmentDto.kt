package org.slimecraft.kits.data.config.dto

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

data class EnchantmentDto(val name: String, val levels: List<Int>) {
    fun enchantment(): Enchantment {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(NamespacedKey("minecraft", name))
    }
}
