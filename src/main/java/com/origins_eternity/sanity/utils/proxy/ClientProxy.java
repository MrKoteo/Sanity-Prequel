package com.origins_eternity.sanity.utils.proxy;

import com.origins_eternity.sanity.content.entity.FakeEntity;
import com.origins_eternity.sanity.content.render.FakeEntityRender;
import com.origins_eternity.sanity.content.render.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(FakeEntity.class, FakeEntityRender::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.register(new Overlay());
    }

    @Override
    public void serverStart(FMLServerStartingEvent event) {
        super.serverStart(event);
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }
}