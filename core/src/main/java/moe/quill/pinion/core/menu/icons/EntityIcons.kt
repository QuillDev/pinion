package moe.quill.pinion.core.menu.icons

import moe.quill.pinion.core.items.itemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.EntityType

//Entity Heads
val entityIcons = mutableMapOf(
    //Creeper
    EntityType.CREEPER to itemBuilder(Material.CREEPER_HEAD) {
        name { Component.text("Creeper") }
    },
    //Zombie
    EntityType.ZOMBIE to itemBuilder(Material.ZOMBIE_HEAD) {
        name { Component.text("Zombie") }
    },
    //Slime
    EntityType.SLIME to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Slime") }
        skullTexture { MobHead.SLIME.texture }
    },
    //Magma Cube
    EntityType.MAGMA_CUBE to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Magma Cube") }
        skullTexture { MobHead.MAGMA_CUBE.texture }
    },
    //Stray
    EntityType.STRAY to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Stray") }
        skullTexture { MobHead.STRAY.texture }
    },
    //Husk
    EntityType.HUSK to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Husk") }
        skullTexture { MobHead.HUSK.texture }
    },
    //Phantom
    EntityType.PHANTOM to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Phantom") }
        skullTexture { MobHead.PHANTOM.texture }
    },
    //Spider
    EntityType.SPIDER to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Spider") }
        skullTexture { MobHead.SPIDER.texture }
    },
    //Cave Spider
    EntityType.CAVE_SPIDER to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Cave Spider") }
        skullTexture { MobHead.CAVE_SPIDER.texture }
    },
    //Blaze
    EntityType.BLAZE to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Blaze") }
        skullTexture { MobHead.BLAZE.texture }
    },
    //Shulker
    EntityType.SHULKER to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Shulker") }
        skullTexture { MobHead.SHULKER.texture }
    },
    //Enderman
    EntityType.ENDERMAN to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Enderman") }
        skullTexture { MobHead.ENDERMAN.texture }
    },
    //Skeleton
    EntityType.SKELETON to itemBuilder(Material.SKELETON_SKULL) {
        name { Component.text("Skeleton") }
    },
    //Zombie Villager
    EntityType.ZOMBIE_VILLAGER to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Zombie Villager") }
        skullTexture { MobHead.ZOMBIE_VILLAGER.texture }
    },
    EntityType.DROWNED to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Drowned") }
        skullTexture { MobHead.DROWNED.texture }
    },
    EntityType.GUARDIAN to itemBuilder(Material.PLAYER_HEAD) {
        name { Component.text("Guardian") }
        skullTexture { MobHead.GUARDIAN.texture }
    },
)