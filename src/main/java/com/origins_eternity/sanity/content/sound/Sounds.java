package com.origins_eternity.sanity.content.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.origins_eternity.sanity.Sanity.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class Sounds {
    public static SoundEvent INSANITY = createSound("insanity");
    public static SoundEvent LEAVES_RUSTLE = createSound("leaves_rustle");

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(LEAVES_RUSTLE);
        event.getRegistry().register(INSANITY);
    }

    private static SoundEvent createSound(String name) {
        ResourceLocation location = new ResourceLocation(MOD_ID, name);
        return new SoundEvent(location).setRegistryName(location);

    }
}