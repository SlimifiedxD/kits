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
import org.slimecraft.funmands.api.argument.type.DoubleArgument
import org.slimecraft.funmands.api.argument.type.IntegerArgument
import org.slimecraft.funmands.paper.command.PaperCommand
import org.slimecraft.kits.data.config.Config
import org.slimecraft.kits.withdrawalKey

class WithdrawCommand(val economy: Economy, val reloadable: ReloadableConfig<Config>) : PaperCommand("withdraw") {
    init {
        addFormat("amount:double", {
            val p = it.source.sender as? Player ?: return@addFormat
            withdraw(p, it.get("amount"))
        })

        addFormat("amount:double vouchers:int", {
            val p = it.source.sender as? Player ?: return@addFormat
            withdraw(p, it.get("amount"), it.get("vouchers"))
        })
    }

    private fun withdraw(p: Player, amtPerWithdrawal: Double, howManyVouchers: Int = 1) {
        val amt = amtPerWithdrawal * howManyVouchers
        val amtC = Component.text(amt)
        if (amt <= 0) {
            p.sendMessage(reloadable.getLatest().cannotWithdrawInvalidAmount.component("amount" to amtC))
            return
        }
        if (howManyVouchers <= 0) {
            // TODO: add config option saying invalid voucher amount
            return
        }
        if (!economy.has(p, amt)) {
            p.sendMessage(reloadable.getLatest().notEnoughCoinsForWithdrawal.component("amount" to Component.text(amt), "balance" to Component.text(economy.getBalance(p))))
            return
        }
        val item = ItemBuilder.create()
            .material(Material.PAPER)
            .name("<aqua>Coin Withdrawal")
            .lore(listOf("<white>A voucher for <yellow><amount></yellow> coins".component("amount" to Component.text(amtPerWithdrawal)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)))
            .pdc(withdrawalKey, PersistentDataType.DOUBLE, amtPerWithdrawal)
            .amount(howManyVouchers)
            .build()
        economy.withdrawPlayer(p, amt)
        p.sendMessage(reloadable.getLatest().withdrawalSuccess.component("amount" to amtC, "balance" to Component.text(economy.getBalance(p))))
        val items = p.inventory.addItem(item)

        items.values.forEach { p.world.dropItem(p.location, it) }
    }
}