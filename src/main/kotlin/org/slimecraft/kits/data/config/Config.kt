package org.slimecraft.kits.data.config

import com.sksamuel.hoplite.decoder.Minutes
import org.slimecraft.kits.data.config.dto.KitSettings

data class Config(val mapResetFloor: String, val dynamiteExplodeTicks: Int, val mapResetCooldown: Minutes, val kitSettings: KitSettings, val coinsEarnedMessage: String, val coinsPerCoinBlockMined: Double, val coinsPerPlayerKilled: Double, val coinBlock: String, val opCoinBlock: String, val coinsPerOpCoinBlockMined: Double, val notEnoughCoinsForWithdrawal: String, val withdrawalSuccess: String, val withdrawalUsed: String, val cannotWithdrawInvalidAmount: String)
