package com.origins_eternity.sanity.utils.registry;

import com.origins_eternity.sanity.content.entity.ShadowMonster;
import com.origins_eternity.sanity.content.render.ShadowRender;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.origins_eternity.sanity.Sanity.MOD_ID;
import static com.origins_eternity.sanity.content.armor.Garland.GARLAND;
import static com.origins_eternity.sanity.content.potion.Potions.*;
import static com.origins_eternity.sanity.content.tool.Umbrella.UMBRELLA;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class ContentRegister {
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        for (Potion potion : POTIONS) {
            event.getRegistry().register(potion);
        }
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        event.getRegistry().registerAll(Composure_Potion, Long_Composure_Potion, Strong_Composure_Potion);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(UMBRELLA);
        event.getRegistry().register(GARLAND);
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(ShadowMonster.class)
                        .id(new ResourceLocation(MOD_ID, "shadow_monster"), 0)
                        .name(MOD_ID + ".shadow_monster")
                        .tracker(64, 3, true)
                        .build()
        );
    }


    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        ModelLoader.setCustomModelResourceLocation(UMBRELLA, 0, new ModelResourceLocation(UMBRELLA.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(GARLAND, 0, new ModelResourceLocation(GARLAND.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerModels();
        RenderingRegistry.registerEntityRenderingHandler(
                ShadowMonster.class,
                ShadowRender::new
        );
    }
}