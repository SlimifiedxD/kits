package org.slimecraft.kits.command

import com.j256.ormlite.dao.Dao
import org.bukkit.Material
import org.bukkit.entity.Player
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.task.Tasks
import org.slimecraft.funmands.paper.command.PaperCommand
import org.slimecraft.kits.KitsItem
import org.slimecraft.kits.KitsPlugin
import org.slimecraft.kits.data.MapReset
import org.slimecraft.kits.getLeftClickPos
import org.slimecraft.kits.getRightClickPos
import org.slimecraft.kits.refreshMapResetTask

class MapResetCommand(dao: Dao<MapReset, Int>, val plugin: KitsPlugin) : PaperCommand("mapreset") {
    init {
        addFormat("", {
        }, {
            it.setPredicate { it.sender.isOp }
        })

        addFormat("wand", {
            val p = it.source.sender
            if (p !is Player) return@addFormat
            p.inventory.addItem(KitsItem.MAP_RESET_WAND.supplier())
            p.sendMessage("<green>Gave you the map reset wand!".component())
        }, {
            it.setPredicate { it.sender.isOp }
        })

        addFormat("set", {
            val p = it.source.sender
            if (p !is Player) return@addFormat
            var shouldNotContinue = false
            val one = getLeftClickPos(p)
            val two = getRightClickPos(p)

            if (one == null) {
                p.sendMessage("<red>Pos 1 not set".component())
                shouldNotContinue = true
            }

            if (two == null) {
                p.sendMessage("<red>Pos 2 not set".component())
                shouldNotContinue = true
            }

            if (one?.world != two?.world) {
                p.sendMessage("<red>The two positions are not within the same world".component())
                shouldNotContinue = true
            }

            if (shouldNotContinue) return@addFormat

            Tasks.run({ t ->
                for (reset in dao.queryForAll()) {
                    dao.delete(reset)
                }
                dao.create(MapReset(one!!, two!!))
                Tasks.run { t ->
                    p.sendMessage("<green>Set the map reset region!".component())
                    refreshMapResetTask(dao, Material.valueOf(plugin.reloader.getLatest().mapResetFloor.uppercase()), plugin.reloader.getLatest().mapResetCooldown)
                }
            }, true)
        }, {
            it.setPredicate { it.sender.isOp }
        })
    }
}