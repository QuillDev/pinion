package moe.quill.pinion.spawners.config

import moe.quill.pinion.commands.translation.CommandArgTranslator
import moe.quill.pinion.core.config.ConfigManager
import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.core.functional.Lambda
import moe.quill.pinion.core.items.builder
import moe.quill.pinion.core.items.itemBuilder
import moe.quill.pinion.core.menu.*
import moe.quill.pinion.core.menu.icons.IconTexture
import moe.quill.pinion.core.menu.icons.MobHead
import moe.quill.pinion.core.menu.icons.entityIcons
import moe.quill.pinion.glow.GlowHandler
import moe.quill.pinion.packets.gui.textInput
import moe.quill.pinion.spawners.lib.Spawner
import moe.quill.pinion.spawners.lib.EntityMeta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import java.util.*
import kotlin.reflect.full.isSubclassOf

class SpawnerManager(private val plugin: Plugin) :
    ConfigManager<MutableList<Spawner>>(plugin, { mutableListOf() }, "spawners.yml"), CommandArgTranslator<Spawner>,
    Listener {

    private val glowHandler = GlowHandler()
    private val viewing = mutableSetOf<UUID>()

    init {
        Bukkit.getServer().pluginManager.registerEvents(this, plugin)
    }

    val editorMode = mutableMapOf<UUID, Boolean>()

    //Modify Spawners
    fun addSpawner(spawner: Spawner) {
        data += spawner
        write()
    }

    fun removeSpawner(name: String) {
        val matches = data.filter { it.name == name }
        matches.forEach { data -= it; it.disable() }
        write()
    }

    //Modify Spawner Data
    fun addType(name: String, type: EntityType) {
        translateArgument(name)?.entityMeta?.plusAssign(EntityMeta(type))
        write()
    }

    fun removeType(name: String, meta: EntityMeta) {
        translateArgument(name)?.entityMeta?.remove(meta)
        write()
    }


    @EventHandler
    fun onSpawnerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val editMode = editorMode[event.player.uniqueId] ?: false
        if (!editMode) return

        val spawner = data.firstOrNull { it.block == block } ?: return

        createSpawnerGUI(spawner).show(event.player)
    }

    override fun translateArgument(arg: String): Spawner? {
        return data.firstOrNull { it.name == arg }
    }

    override fun translationNames(): Collection<String> {
        return data.map { it.name }
    }

    fun createSpawnerGUI(spawner: Spawner): Menu {
        return menuBuilder(plugin, Component.text("Spawner Menu ${spawner.name}"), 3) {
            //Add Entity icon
            set(0) {
                MenuItem(itemBuilder(Material.PLAYER_HEAD) {
                    name { Component.text("Add Entity").color(NamedTextColor.GREEN) }
                    skullTexture { IconTexture.PLUS.texture }
                }) { it.openMenu(createEntityAddGUI({ createSpawnerGUI(spawner) }, spawner)) }
            }
            // View entity data
            set(1) {
                MenuItem(itemBuilder(Material.AXOLOTL_SPAWN_EGG) {
                    name { Component.text("View Entities").color(NamedTextColor.GREEN) }
                }) { it.openMenu(showTypesGUI({ createSpawnerGUI(spawner) }, spawner)) }
            }

            set(2) { slot ->
                booleanInput(
                    slot,
                    this,
                    Component.text("Visible?"),
                    spawner.visible,
                    { spawner.visible = it },
                    itemBuilder(Material.ENDER_EYE),
                    itemBuilder(Material.ENDER_PEARL)
                )
            }

            //Whether this spawner is on or not
            set(3) { slot ->
                booleanInput(
                    slot,
                    this,
                    Component.text("Enabled?"),
                    spawner.enabled,
                    { spawner.enabled = it },
                    itemBuilder(Material.BLAZE_POWDER),
                    itemBuilder(Material.GUNPOWDER)
                )
            }

            //Radius Select
            append {
                MenuItem(itemBuilder(Material.HEART_OF_THE_SEA) { name { Component.text("Spawn Radius: ${spawner.radius}") } }) { player ->
                    numberInput(plugin, spawner.radius, { spawner.radius = it }).show(player)
                }
            }
            //Rate Select
            append {
                MenuItem(itemBuilder(Material.REDSTONE) { name { Component.text("Spawn Rate [Ticks Per Spawn]: ${spawner.rate}") } }) { player ->
                    numberInput(plugin, spawner.rate.toInt(), { spawner.rate = it.toLong() }).show(player)
                }
            }
            //Spawn Cap
            append {
                MenuItem(itemBuilder(Material.ZOMBIE_HEAD) { name { Component.text("Spawn Cap: ${spawner.spawnCap}") } }) { player ->
                    numberInput(plugin, spawner.spawnCap, { spawner.spawnCap = it }).show(player)
                }
            }
            append {
                MenuItem(itemBuilder(Material.FERMENTED_SPIDER_EYE) { name { Component.text("View Range") } }) { player ->
                    if (!viewing.contains(player.uniqueId)) {
                        spawner.cache.forEach { glowHandler.showGlow(player, it) }
                        viewing += player.uniqueId
                        return@MenuItem
                    }
                    spawner.cache.forEach { glowHandler.hideGlow(player, it) }
                    viewing -= player.uniqueId
                }
            }

            //Entity Culler
            set(18) {
                MenuItem(itemBuilder(Material.DIAMOND_SWORD) { name { Component.text("Kill Entities.") } }) {
                    spawner.entities.forEach { it.remove() }
                }
            }

            //Icon for closing the menu
            set(8) {
                MenuItem(itemBuilder(Material.PLAYER_HEAD) {
                    name { Component.text("Close Menu").color(NamedTextColor.RED).decorate(TextDecoration.BOLD) }
                    skullTexture { IconTexture.EXIT.texture }
                }) { it.closeInventory() }
            }
            //Icon for deleting the spawner
            set(26) {
                MenuItem(itemBuilder(Material.BARRIER) {
                    name { Component.text("Delete Spawner").color(NamedTextColor.RED).decorate(TextDecoration.BOLD) }
                }) { removeSpawner(spawner.name); it.closeInventory() }
            }
        }
    }

    private fun createEntityAddGUI(parent: () -> Menu, spawner: Spawner): Menu {
        return menuBuilder(plugin, Component.text("Entities"), 6) {
            set(0) { backButton(parent) }

            var idx = 1
            entityIcons.forEach { (entry, icon) ->
                set(idx) { MenuItem(icon) { addType(spawner.name, entry); it.openMenu(parent()) } }
                idx++
            }
        }
    }

    private fun showTypesGUI(parent: () -> Menu, spawner: Spawner): Menu {
        return menuBuilder(plugin, Component.text("Entity Types"), 6) {
            //Back arrow for the menu
            set(0) { backButton(parent) }

            //Iterate through entity meta
            var idx = 1
            spawner.entityMeta.forEach { meta ->
                val icon = entityIcons[meta.type] ?: return@forEach
                set(idx) {
                    MenuItem(icon.builder { name { Component.text(meta.name) } }) {
                        it.openMenu(
                            createMetaMenu(
                                { showTypesGUI(parent, spawner) },
                                spawner,
                                meta
                            )
                        )
                    }
                }
                idx++
            }
        }
    }

    private fun createEquipmentMenu(parent: () -> Menu, meta: EntityMeta): Menu {
        return menuBuilder(plugin, Component.text("Equipment"), 6) {
            set(0) { MenuItem(itemBuilder(Material.DIAMOND_HELMET)) }
            set(9) { MenuItem(itemBuilder(Material.DIAMOND_CHESTPLATE)) }
            set(18) { MenuItem(itemBuilder(Material.DIAMOND_LEGGINGS)) }
            set(27) { MenuItem(itemBuilder(Material.DIAMOND_BOOTS)) }
            set(36) { MenuItem(itemBuilder(Material.DIAMOND_SWORD)) }
            set(45) { MenuItem(itemBuilder(Material.SHIELD)) }

            meta.helmet?.let { item -> set(1) { MenuItem(item, true) } }
            meta.chest?.let { item -> set(10) { MenuItem(item, true) } }
            meta.leggings?.let { item -> set(19) { MenuItem(item, true) } }
            meta.boots?.let { item -> set(28) { MenuItem(item, true) } }
            meta.mainHand?.let { item -> set(37) { MenuItem(item, true) } }
            meta.offHand?.let { item -> set(46) { MenuItem(item, true) } }

            for (idx in 0..53) {
                if (idx % 9 == 0 || (idx - 1) % 9 == 0 || idx == 8) continue
                set(idx) { MenuItem(itemBuilder(Material.BLACK_STAINED_GLASS_PANE) { name { Component.empty() } }) }
            }

            set(8) {
                MenuItem(itemBuilder(Material.GREEN_WOOL) {
                    Component.text("Confirm?").color(NamedTextColor.GREEN)
                }) {
                    meta.helmet = inventory.getItem(1)
                    meta.chest = inventory.getItem(10)
                    meta.leggings = inventory.getItem(19)
                    meta.boots = inventory.getItem(28)
                    meta.mainHand = inventory.getItem(37)
                    meta.offHand = inventory.getItem(46)
                    it.openMenu(parent())
                }
            }
        }
    }

    private fun createMetaMenu(parent: () -> Menu, spawner: Spawner, meta: EntityMeta): Menu {

        return menuBuilder(plugin, Component.text("Edit meta for: ${meta.name}"), 6) {
            //Remove this entity
            set(size - 1) {
                MenuItem(itemBuilder(Material.BARRIER) {
                    name { Component.text("Remove").color(NamedTextColor.RED).decorate(TextDecoration.BOLD) }
                }) { player ->
                    removeType(spawner.name, meta)
                    parent().show(player)
                }
            }

            //Back Button
            append { backButton(parent) }

            //Renaming Entity Menu
            append {
                textInput(
                    plugin,
                    itemBuilder(Material.NAME_TAG) { name { Component.text("Rename Entity") } }) { player, response ->
                    val name = response.getOrNull(0) ?: return@textInput
                    if (name.isEmpty()) return@textInput
                    meta.name = name
                    Lambda { player.openMenu(createMetaMenu(parent, spawner, meta)) }.runTask(plugin)
                }
            }

            //Entity Weights
            append {
                MenuItem(itemBuilder(Material.IRON_INGOT) { name { Component.text("Entity Weight: ${meta.weight}") } }) { player ->
                    numberInput(plugin, meta.weight, { weight -> meta.weight = weight; player.openMenu(this) }).show(
                        player
                    )
                }
            }

            //Add different flag values
            val type = meta.type.entityClass?.kotlin ?: return@menuBuilder
            //Living Entity Attributes
            if (type.isSubclassOf(LivingEntity::class)) {
                append {
                    MenuItem(itemBuilder(Material.DIAMOND_CHESTPLATE) { name { Component.text("Equipment") } }) {
                        it.openMenu(createEquipmentMenu({ createMetaMenu(parent, spawner, meta) }, meta))
                    }
                }
                //Add a boolean field for assignment
                append { slot ->
                    booleanInput(
                        slot,
                        this,
                        Component.text("Invisible?"),
                        meta.invisible,
                        { meta.invisible = it },
                        itemBuilder(Material.POTION) { potionData { PotionData(PotionType.INVISIBILITY) } },
                        itemBuilder(Material.GLASS_BOTTLE)
                    )
                }
                append { slot ->
                    booleanInput(
                        slot,
                        this,
                        Component.text("Glowing?"),
                        meta.glowing,
                        { meta.glowing = it },
                        itemBuilder(Material.GLOWSTONE),
                        itemBuilder(Material.REDSTONE_LAMP)
                    )
                }
            }
            //Creeper
            if (type.isSubclassOf(Creeper::class)) {
                append { slot ->
                    booleanInput(
                        slot,
                        this,
                        Component.text("Charged?"),
                        meta.charged,
                        { meta.charged = it }
                    )
                }
            }

            //If the entities size can be modified
            if (type.isSubclassOf(Slime::class) || type.isSubclassOf(Phantom::class)) {
                append {
                    MenuItem(itemBuilder(Material.PLAYER_HEAD) {
                        name { Component.text("Size?") }
                        skullTexture { MobHead.SLIME.texture }
                    }) { player ->
                        numberInput(
                            plugin,
                            meta.size,
                            { meta.size = it },
                            { createMetaMenu(parent, spawner, meta) },
                            min = 1
                        ).show(player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onDisable(event: PluginDisableEvent) {
        log("Writing Current Spawner data to the config")
        write()
    }
}