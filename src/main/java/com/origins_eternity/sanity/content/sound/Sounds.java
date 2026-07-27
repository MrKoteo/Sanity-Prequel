package com.origins_eternity.sanity.content.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static com.origins_eternity.sanity.Sanity.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class Sounds {
    private static final List<SoundEvent> SOUNDEVENTS = new ArrayList<>();
    public static SoundEvent INSANITY = createSound("insanity");
    public static SoundEvent LEAVES_RUSTLE = createSound("leaves_rustle");
    public static SoundEvent SHADOW_MONSTER_HURT = createSound("shadow_monster_hurt");
    public static SoundEvent SHADOW_MONSTER_AMBIENT = createSound("shadow_monster_ambient");

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent soundEvent : SOUNDEVENTS) {
            event.getRegistry().register(soundEvent);
        }
    }

    private static SoundEvent createSound(String name) {
        ResourceLocation location = new ResourceLocation(MOD_ID, name);
        SoundEvent soundEvent = new SoundEvent(location).setRegistryName(location);
        SOUNDEVENTS.add(soundEvent);
        return soundEvent;
    }
}