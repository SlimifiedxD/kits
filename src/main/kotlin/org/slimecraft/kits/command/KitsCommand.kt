package org.slimecraft.kits.command

import org.slimecraft.funmands.paper.command.PaperCommand
import org.slimecraft.kits.KitsPlugin
import org.slimecraft.kits.KitManager

class KitsCommand(val plugin: KitsPlugin) : PaperCommand("kits") {
    init {
        addFormat("", {}, {
            it.setPredicate { it.sender.isOp }
        })

        addFormat("reload", {
            plugin.kitMgr = KitManager(plugin.reloader.getLatest().kitSettings)
            println(plugin.reloader.getLatest().kitSettings.weapons.items)
        }, {
            it.setPredicate { it.sender.isOp }
        })
    }
}