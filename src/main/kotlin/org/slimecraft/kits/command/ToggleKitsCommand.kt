package org.slimecraft.kits.command

import org.bukkit.entity.Player
import org.slimecraft.funmands.paper.command.PaperCommand
import org.slimecraft.kits.toggleGiveKits

class ToggleKitsCommand : PaperCommand("togglekits") {
    init {
        addFormat("", {
            val p = it.source.sender
            if (p !is Player) return@addFormat
            toggleGiveKits(p)
        })
    }
}