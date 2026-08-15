package com.origins_eternity.sanity.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.origins_eternity.sanity.Sanity.MOD_ID;

@Config(modid = MOD_ID)
public class Configuration {
    @Config.Name("Compat")
    @Config.Comment("Mod Compat Options")
    @Config.LangKey("config.sanity.compat")
    public static ConfigCompat Compat = new ConfigCompat();

    @Config.Name("Mechanics")
    @Config.Comment("Mechanics Options")
    @Config.LangKey("config.sanity.mechanics")
    public static ConfigMechanics Mechanics = new ConfigMechanics();

    @Config.Name("Overlay")
    @Config.Comment("Overlay Options")
    @Config.LangKey("config.sanity.overlay")
    public static ConfigOverlay Overlay = new ConfigOverlay();

    @Config.Name("Effects")
    @Config.Comment("Effects Options")
    @Config.LangKey("config.sanity.effects")
    public static ConfigEffect Effect = new ConfigEffect();

    public static class ConfigCompat {
        @Config.Name("Food Spoiling")
        @Config.LangKey("config.sanity.foodSpoiling")
        @Config.Comment("The sanity to decrease when food spoiled in player's inventory. (only works with Food Spoiling mod.)")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double foodSpoiling = 1.0;

        @Config.Name("Thirst")
        @Config.LangKey("config.sanity.thirst")
        @Config.Comment("The sanity to decrease when player's thirstLevel < 6. (per 0.5s, support ToughAsNails and SimpleDifficulty)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double thirst = 0.1;

        @Config.Name("Campfire")
        @Config.LangKey("config.sanity.campfire")
        @Config.Comment("The sanity to increase when players are around campfire and gain comfort effect from Pyrotech. (per 0.5s)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double campfire = 0.2;

        @Config.Name("Quest")
        @Config.LangKey("config.sanity.quest")
        @Config.Comment("The sanity to increase when players complete a quest. (only support FTB Quests)")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double quest = 10.0;

        @Config.Name("Baubles")
        @Config.LangKey("config.sanity.baubles")
        @Config.Comment("The list of baubles which will decrease or increase sanity when players wear it. ('bauble;value', per 0.5s)")
        public String[] baubles = new String[]{"sanity:garland;0.2"};

        @Config.Name("Nutrition")
        @Config.LangKey("config.sanity.nutrition")
        @Config.Comment("The normal nutrition range and factors affecting the increase or decrease of sanity. ('decel_factor;accel_factor;min_nutrition;max_nutrition')")
        public String nutrition = "0.8;1.25;20;80";
    }

    public static class ConfigMechanics {
        @Config.Name("Reset Sanity")
        @Config.LangKey("config.sanity.reset")
        @Config.Comment("Whether to reset sanity after respawning.")
        public boolean reset = true;

        @Config.Name("Attack Animal")
        @Config.LangKey("config.sanity.animal")
        @Config.Comment("The sanity to decrease when players attack an animal.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double attackAnimal = 0.5;

        @Config.Name("Attack Villager")
        @Config.LangKey("config.sanity.villager")
        @Config.Comment("The sanity to decrease when players attack a villager.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double attackVillager = 1.5;

        @Config.Name("Attack Player")
        @Config.LangKey("config.sanity.player")
        @Config.Comment("The sanity to decrease when players attack another player.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double attackPlayer = 2.0;

        @Config.Name("Hurt")
        @Config.LangKey("config.sanity.hurt")
        @Config.Comment("The sanity to decrease based on the damage with the ratio of 1 to this number.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double hurt = 1.0;

        @Config.Name("Trip")
        @Config.LangKey("config.sanity.trip")
        @Config.Comment("The sanity to decrease when players change dimension.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double trip = 10.0;

        @Config.Name("Lost")
        @Config.LangKey("config.sanity.lost")
        @Config.Comment("The sanity to decrease when players lose pets.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double lost = 30.0;

        @Config.Name("Lightning")
        @Config.LangKey("config.sanity.lightning")
        @Config.Comment("The sanity to decrease when players are struck by lightning.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double lightning = 30.0;

        @Config.Name("Rain")
        @Config.LangKey("config.sanity.rain")
        @Config.Comment("The sanity to decrease when players get wet by rain. (per 0.5s)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double rain = 0.1;

        @Config.Name("Hunger")
        @Config.LangKey("config.sanity.hunger")
        @Config.Comment("The sanity to decrease when player's foodLevel < 6. (per 0.5s)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double hunger = 0.1;

        @Config.Name("Choking")
        @Config.LangKey("config.sanity.choking")
        @Config.Comment("The sanity to decrease when player's air < 90. (per 0.5s)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double choking = 0.1;

        @Config.Name("Dark")
        @Config.LangKey("config.sanity.dark")
        @Config.Comment("The sanity to decrease when players are in dark. (per 0.5s)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double dark = 0.1;

        @Config.Name("Mob")
        @Config.LangKey("config.sanity.mob")
        @Config.Comment("The sanity to decrease when mobs are around player. (per 0.5s, within 8 blocks)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double mob = 0.2;

        @Config.Name("Abnormal Player")
        @Config.LangKey("config.sanity.abnormal")
        @Config.Comment("The sanity to decrease when players are around a player with sanity < 50. (per 0.5s, within 5 blocks)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double abnormal = 0.2;

        @Config.Name("Normal Player")
        @Config.LangKey("config.sanity.normal")
        @Config.Comment("The sanity to increase when players are around a player with sanity ≥ 50. (per 0.5s, within 5 blocks)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double normal = 0.1;

        @Config.Name("Pet")
        @Config.LangKey("config.sanity.pet")
        @Config.Comment("The sanity to increase when players stay with pets. (per 0.5s, within 5 blocks)")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double pet = 0.2;

        @Config.Name("Food")
        @Config.LangKey("config.sanity.food")
        @Config.Comment("The sanity to increase when players eat foods that are not in the item list.")
        @Config.RangeDouble(min = 0.0, max = 0.5)
        public double food = 1.0;

        @Config.Name("Composure")
        @Config.LangKey("config.sanity.composure")
        @Config.Comment("The sanity to increase by the composure effect at each interval.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double composure = 1.0;

        @Config.Name("Kill Mob")
        @Config.LangKey("config.sanity.kill")
        @Config.Comment("The sanity to increase when players kill a mob.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double killMob = 2.5;

        @Config.Name("Bred")
        @Config.LangKey("config.sanity.bred")
        @Config.Comment("The sanity to increase when players bred animals.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double bred = 10.0;

        @Config.Name("Advancement")
        @Config.LangKey("config.sanity.advancement")
        @Config.Comment("The sanity to increase when players gain an advancement.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double advancement = 15.0;

        @Config.Name("Sleep")
        @Config.LangKey("config.sanity.sleep")
        @Config.Comment("The sanity to increase when players wake up.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double sleep = 50.0;

        @Config.Name("JukeBox Range")
        @Config.LangKey("config.sanity.jukebox")
        @Config.Comment("The effective radius within which jukebox can affect player's sanity.")
        @Config.RangeInt(min = 0, max = 24)
        public int jukebox = 24;

        @Config.Name("Items")
        @Config.LangKey("config.sanity.items")
        @Config.Comment("The list of items which will decrease or increase sanity when players use it. ('item;value')")
        public String[] items = new String[]{"minecraft:rotten_flesh;-3.0", "minecraft:spider_eye;-3.0", "minecraft:chicken;-2.0", "minecraft:porkchop;-2.0", "minecraft:mutton;-2.0", "minecraft:beef;-2.0", "minecraft:rabbit;-2.0", "minecraft:poisonous_potato;-2.0", "minecraft:fish:*;-2.0"};

        @Config.Name("Records")
        @Config.LangKey("config.sanity.records")
        @Config.Comment("The list of records which will decrease or increase sanity per 0.5s when jukebox plays them around players. ('record;value;duration')")
        public String[] records = new String[]{"minecraft:record_cat;0.2;185"};

        @Config.Name("Environment")
        @Config.LangKey("config.sanity.environment")
        @Config.Comment("The list of blocks and liquids which will decrease or increase sanity per 0.5s when players stay in it. ('block;value')")
        public String[] blocks = new String[]{"minecraft:web;-0.1"};

        @Config.Name("Entities")
        @Config.LangKey("config.sanity.entities")
        @Config.Comment("The list of entities which will decrease or increase sanity when players stay with it, attack it and kill it. ('entity;stay_value;attack_value;kill_value', use '*' to indicate the default value.)")
        public String[] entities = new String[]{"minecraft:wither;*;*;10.0", "sanity:shadow_monster;0.3;*;15.0"};
        
        @Config.Name("Equipments")
        @Config.LangKey("config.sanity.equipments")
        @Config.Comment("The list of equipments which will decrease or increase sanity per 0.5s when players wear it. ('equipment;value')")
        public String[] equipments = new String[]{"sanity:garland;0.2"};

        @Config.Name("Dimensions")
        @Config.LangKey("config.sanity.dimensions")
        @Config.Comment("The list of dimensions which will enable sanity.")
        public int[] dimensions = new int[]{1, 0, -1};

        @Config.Name("Blacklist")
        @Config.LangKey("config.sanity.blacklist")
        @Config.Comment("Whether to make the list of dimensions blacklist or not.")
        public boolean blacklist = false;
    }

    public static class ConfigOverlay {
        @Config.Name("Brain Overlay OffX")
        @Config.LangKey("config.sanity.offx")
        @Config.Comment("Offset on x of the brain overlay. A positive number means a shift to the right.")
        public int offX = 0;

        @Config.Name("Brain Overlay OffY")
        @Config.LangKey("config.sanity.offy")
        @Config.Comment("Offset on y of the brain overlay. A positive number means a shift to the top.")
        public int offY = 0;

        @Config.Name("Hand Check")
        @Config.LangKey("config.sanity.check")
        @Config.Comment("Whether to mirror the position of brain overlay to the other side automatically based on the status of the hands.")
        public boolean check = true;

        @Config.Name("Highlight")
        @Config.LangKey("config.sanity.highlight")
        @Config.Comment("Highlight the border of brain overlay when change in sanity is greater than or equal to this value.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double highlight = 1.0;

        @Config.Name("Brain Overlay Flash Time")
        @Config.LangKey("config.sanity.flash")
        @Config.Comment("How many seconds the brain overlay should be visible when sanity changed. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 30)
        public int flash = -1;

        @Config.Name("Brain Overlay Shake")
        @Config.LangKey("config.sanity.shake")
        @Config.Comment("Make brain overlay shake on screen when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int shake = 30;

        @Config.Name("Blood Overlay")
        @Config.LangKey("config.sanity.blood")
        @Config.Comment("Enable blood overlay when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int blood = 45;
    }

    public static class ConfigEffect {
        @Config.Name("Shader Effects")
        @Config.LangKey("config.sanity.shader")
        @Config.Comment("Enable shader effects when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int shader = 60;

        @Config.Name("Sound")
        @Config.LangKey("config.sanity.sound")
        @Config.Comment("Play random sounds when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int sound = 75;

        @Config.Name("Random Sounds List")
        @Config.LangKey("config.sanity.sounds")
        @Config.Comment("The list of sounds which will play randomly when sanity is low, including their volume and pitch values. ('sound;volume;pitch') | ('sound;min_volume;max_volume;min_pitch;max_pitch')")
        public String[] sounds = new String[]{"entity.creeper.primed;1.0;0.5", "entity.tnt.primed;1.0;0.5", "entity.skeleton.ambient;0.5;1.5;0.5;1.5", "entity.skeleton.step;1.0;0.5", "entity.zombie.ambient;0.5;1.5;0.5;1.5", "entity.zombie.step;1.0;0.5", "entity.enderman.ambient;0.5;1.5;0.5;1.5", "entity.hostile.big_fall;1.0;0.5", "block.chest.open;1.0;0.5", "block.chest.close;1.0;0.5", "block.wooden_door.open;1.0;0.5", "block.wooden_trapdoor.open;1.0;0.5", "entity.wolf.growl;0.5;1.5;0.5;1.5"};

        @Config.Name("Ghost")
        @Config.LangKey("config.sanity.ghost")
        @Config.Comment("Spawn fake mobs around player when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int ghost = 30;

        @Config.Name("Ghost Opacity")
        @Config.LangKey("config.sanity.opacity")
        @Config.Comment("Set minimum opacity of the ghost.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double opacity = 0.5;

        @Config.Name("Random Ghosts List")
        @Config.LangKey("config.sanity.ghosts")
        @Config.Comment("The list of ghosts which will randomly appear in the player's view, including their spawn radius and duration. ('ghost;min_radius;max_radius;min_ticks;max_ticks')")
        public String[] ghosts = new String[]{"minecraft:creeper;6;14;30;120", "minecraft:skeleton;6;14;30;120", "minecraft:creeper;6;14;30;120", "minecraft:zombie;6;14;30;120", "minecraft:enderman;6;14;30;120"};

        @Config.Name("Whisper")
        @Config.LangKey("config.sanity.whisper")
        @Config.Comment("Play whispering when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int whisper = 45;

        @Config.Name("Whisper Interval")
        @Config.LangKey("config.sanity.interval")
        @Config.Comment("Set maximum number of seconds between two whispers.")
        @Config.RangeInt(min = 0, max = 132)
        public int interval = 132;

        @Config.Name("Shadow Monster")
        @Config.LangKey("config.sanity.shadow")
        @Config.Comment("Spawn shadow monster near the player when sanity is lower than the value. (set this to -1 to disable)")
        @Config.RangeInt(min = -1, max = 100)
        public int shadow = 15;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(MOD_ID)) {
                ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}