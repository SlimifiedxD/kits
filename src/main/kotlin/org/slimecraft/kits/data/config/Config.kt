package org.slimecraft.kits.data.config

import com.sksamuel.hoplite.decoder.Minutes
import org.slimecraft.kits.data.config.dto.KitSettings

data class Config(val mapResetFloor: String, val dynamiteExplodeTicks: Int, val mapResetCooldown: Minutes, val kitSettings: KitSettings)