package org.slimecraft.kits

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.watch.ReloadableConfig
import com.sksamuel.hoplite.watch.watchers.FileWatcher
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.UseCooldown
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.slimecraft.bedrock.annotation.plugin.Dependency
import org.slimecraft.bedrock.annotation.plugin.Plugin
import org.slimecraft.bedrock.event.EventNode
import org.slimecraft.bedrock.kt.extensions.component
import org.slimecraft.bedrock.util.Ticks
import org.slimecraft.funmands.paper.PaperFunmandsManager
import org.slimecraft.kits.command.KitsCommand
import org.slimecraft.kits.command.MapResetCommand
import org.slimecraft.kits.command.ToggleKitsCommand
import org.slimecraft.kits.KitManager
import org.slimecraft.kits.data.MapReset
import org.slimecraft.kits.data.config.Config
import org.slimecraft.kits.data.config.deserializer.ComponentDecoder

@Plugin("kits", dependencies = [
    Dependency("FastAsyncWorldEdit", required = true),
    Dependency("Vault", required = true)
])
class KitsPlugin : JavaPlugin() {
    lateinit var kitMgr: KitManager
    lateinit var reloader: ReloadableConfig<Config>
    lateinit var economy: Economy

    override fun onEnable() {
        saveDefaultConfig()
        val connectionSource = JdbcConnectionSource("jdbc:sqlite:plugins/kits/database.db")
        val mapResetDao: Dao<MapReset, Int> = DaoManager.createDao(connectionSource, MapReset::class.java)
        val cmdManager = PaperFunmandsManager(lifecycleManager)
        cmdManager.registerCommand(MapResetCommand(mapResetDao, this))
        cmdManager.registerCommand(ToggleKitsCommand())
        cmdManager.registerCommand(KitsCommand(this))
        TableUtils.createTableIfNotExists(connectionSource, MapReset::class.java)
        for (item in KitsItem.entries) {
            item.init()
        }

        val loader = ConfigLoaderBuilder.empty()
            .withClassLoader(KitsPlugin::class.java.classLoader)
            .addDefaults()
            .addDecoder(ComponentDecoder())
            .addSource(PropertySource.file(dataPath.resolve("config.yml").toFile()))
            .build()
        println(dataPath.toString())
        val watcher = FileWatcher(dataPath.toString())
        reloader = ReloadableConfig(loader, Config::class).addWatcher(watcher)

        refreshMapResetTask(mapResetDao, reloader.getLatest())
        kitMgr = KitManager(reloader.getLatest().kitSettings)
        val dynamiteKey = key("dynamite")
        economy = server.servicesManager.getRegistration(Economy::class.java)!!.provider

        EventNode.global().addListener(PlayerPostRespawnEvent::class.java) {
            val p = it.player
            if (!shouldGiveKits(p)) return@addListener
            kitMgr.give(it.player)
        }

        EventNode.global().addListener(PlayerInteractEvent::class.java) {
            val item = it.item
            if (item?.persistentDataContainer?.has(dynamiteKey) != true) return@addListener
            val action = it.action
            if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return@addListener
            val p = it.player
            if (p.getCooldown(dynamiteKey) > 0) return@addListener
            val tnt = p.world.spawn(p.location, TNTPrimed::class.java)
            tnt.velocity = p.location.direction.normalize().multiply(3)
            tnt.fuseTicks = reloader.getLatest().dynamiteExplodeTicks
            tnt.persistentDataContainer.set(dynamiteKey, PersistentDataType.BOOLEAN, true)
            item.amount -= 1
            p.setCooldown(dynamiteKey, Ticks.seconds(7).toInt())
            p.setCooldown(item, Ticks.seconds(7).toInt())
        }

        EventNode.global().addListener(BlockPlaceEvent::class.java) {
            if (!it.itemInHand.persistentDataContainer.has(dynamiteKey)) return@addListener
            it.isCancelled = true
        }

        EventNode.global().addListener(ExplosionPrimeEvent::class.java) {
            if (!it.entity.persistentDataContainer.has(dynamiteKey)) return@addListener
            it.fire = true
            it.radius = 5f
        }

        EventNode.global().addListener(BlockBreakEvent::class.java) {
            if (it.block.type != Material.valueOf(reloader.getLatest().coinBlock.uppercase())) return@addListener // get block type from config
            it.isDropItems = false
            val p = it.player
            val amt = reloader.getLatest().coinsPerCoinBlockMined
            economy.depositPlayer(p, amt)
            p.sendMessage(reloader.getLatest().coinsEarnedMessage.component("amount" to Component.text(amt)))
        }

        EventNode.global().addListener(PlayerDeathEvent::class.java) {
            val p = it.damageSource.directEntity
            if (p !is Player) return@addListener
            val amt = reloader.getLatest().coinsPerPlayerKilled
            economy.depositPlayer(p, amt)
            p.sendMessage(reloader.getLatest().coinsEarnedMessage.component("amount" to Component.text(amt)))
        }
    }
}