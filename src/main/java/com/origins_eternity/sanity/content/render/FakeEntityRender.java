package com.origins_eternity.sanity.content.render;

import com.origins_eternity.sanity.content.entity.FakeEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FakeEntityRender extends Render<FakeEntity> {

    public FakeEntityRender(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(FakeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Entity fakeEntity = entity.getFakeEntity();
        Render<Entity> render = renderManager.getEntityRenderObject(fakeEntity);
        if (render != null) {
            render.doRender(fakeEntity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(FakeEntity entity) {
        return null;
    }
}