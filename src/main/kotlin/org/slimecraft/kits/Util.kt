package org.slimecraft.kits

import com.j256.ormlite.dao.Dao
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sksamuel.hoplite.decoder.Minutes
import com.sksamuel.hoplite.decoder.duration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.slimecraft.bedrock.event.EventNode
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.kt.extensions.minutes
import org.slimecraft.bedrock.kt.extensions.seconds
import org.slimecraft.bedrock.task.Task
import org.slimecraft.bedrock.task.Tasks
import org.slimecraft.bedrock.util.Ticks
import org.slimecraft.bedrock.util.location.LocationDto
import org.slimecraft.kits.data.MapReset
import org.slimecraft.kits.data.config.Config
import java.util.UUID
import kotlin.random.Random

private val leftClickPositions: MutableMap<UUID, LocationDto> = mutableMapOf()
private val rightClickPositions: MutableMap<UUID, LocationDto> = mutableMapOf()
private var mapResetTask: Task? = null
private val doNotGiveKitsTo: MutableSet<UUID> = mutableSetOf()
val withdrawalKey = key("withdrawal")

fun key(value: String): NamespacedKey {
    return NamespacedKey("kits", value)
}

fun getLeftClickPos(p: Player): LocationDto? {
    return leftClickPositions[p.uniqueId]
}

fun getRightClickPos(p: Player): LocationDto? {
    return rightClickPositions[p.uniqueId]
}

fun setLeftClickPos(p: Player, block: Block) {
    leftClickPositions[p.uniqueId] = LocationDto(block.location)
}

fun setRightClickPos(p: Player, block: Block) {
    rightClickPositions[p.uniqueId] = LocationDto(block.location)
}

fun LocationDto.toBlockVector3(): BlockVector3 {
    return BlockVector3.at(this.blockX, this.blockY, this.blockZ)
}

fun toggleGiveKits(p: Player) {
    val id = p.uniqueId
    if (id in doNotGiveKitsTo) {
        doNotGiveKitsTo.remove(id)
        p.sendMessage("<green>You will now receive kits".component())
    } else {
        doNotGiveKitsTo.add(id)
        p.sendMessage("<red>You will no longer receive kits".component())
    }
}

fun shouldGiveKits(p: Player): Boolean {
    return p.uniqueId !in doNotGiveKitsTo
}

fun refreshMapResetTask(dao: Dao<MapReset, Int>, config: Config) {
    mapResetTask?.cancel()
    mapResetTask = Task
        .builder()
        .whenRan { t ->
            for (reset in dao.queryForAll()) { // should only ever be 1 (hopefully)...
                val one = reset.one
                val two = reset.two
                if (one == null || two == null) return@whenRan
                Tasks.run { t ->
                    val bWorld = Bukkit.getWorld(one.world)!!
                    val region = CuboidRegion(one.toBlockVector3(), two.toBlockVector3())
                    val clipboard = BlockArrayClipboard(region)
                    WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(bWorld)).use {
                        val copy = ForwardExtentCopy(it, region, clipboard, region.minimumPoint)
                        Operations.complete(copy)
                    }
                    val blocks = region.map { bWorld.getBlockAt(it.x(), it.y(), it.z()) }.toList()
                    for (block in blocks) {
                        if (block.y == region.maximumY) {
                            block.type = Material.valueOf(config.mapResetFloor.uppercase())
                        } else {
                            if (Random.nextInt(0, 10) == 0) { // get from config
                                block.type = Material.GOLD_ORE // get from config l8r
                            } else {
                                block.type = Material.STONE // get from config l8r
                            }
                        }
                    }
                }
            }
        }
        .async()
        .repeat(config.mapResetCooldown.value.minutes)
        .run()
}