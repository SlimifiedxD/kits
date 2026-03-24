package org.slimecraft.kits.data.config.dto

import org.bukkit.Registry
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.slimecraft.bedrock.kt.extensions.seconds

data class PotionEffectDto(val id: String, val amplifier: Byte, val duration: Int, val ambient: Boolean = false, val showParticles: Boolean = true, val showIcon: Boolean = true) {
    fun potionEffect(): PotionEffect {
        return PotionEffect(Registry.POTION_EFFECT_TYPE.stream().filter { it.key.value().equals(id) }.toList()[0], duration.seconds.toInt(), amplifier.toInt(), ambient, showParticles, showIcon)
    }
}
