package org.slimecraft.kits.command

import com.sksamuel.hoplite.watch.ReloadableConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.util.item.ItemBuilder
import org.slimecraft.funmands.paper.command.PaperCommand
import org.slimecraft.kits.data.config.Config
import org.slimecraft.kits.withdrawalKey

class WithdrawCommand(val economy: Economy, val reloadable: ReloadableConfig<Config>) : PaperCommand("withdraw") {
    init {
        addFormat("amount:double", {
            val p = it.source.sender
            if (p !is Player) return@addFormat
            val amt: Double = it.get("amount")
            val amtC = Component.text(amt)
            if (!economy.has(p, amt)) {
                p.sendMessage(reloadable.getLatest().notEnoughCoinsForWithdrawal.component("amount" to Component.text(amt), "balance" to Component.text(economy.getBalance(p))))
                return@addFormat
            }
            val item = ItemBuilder.create()
                .material(Material.PAPER)
                .name("<aqua>Coin Withdrawal")
                .lore(listOf("<white>A voucher for <yellow><amount></yellow> coins".component("amount" to Component.text(amt)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)))
                .pdc(withdrawalKey, PersistentDataType.DOUBLE, amt)
                .build()
            economy.withdrawPlayer(p, amt)
            p.sendMessage(reloadable.getLatest().withdrawalSuccess.component("amount" to amtC, "balance" to Component.text(economy.getBalance(p))))
            p.inventory.addItem(item)
        })
    }
}