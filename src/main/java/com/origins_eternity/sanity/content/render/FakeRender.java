package com.origins_eternity.sanity.content.render;

import com.origins_eternity.sanity.content.entity.FakeEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FakeRender extends Render<FakeEntity> {
    public FakeRender(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(FakeEntity fakeEntity, double x, double y, double z, float entityYaw, float partialTicks) {
        Entity entity = fakeEntity.getFakeEntity();
        Render<Entity> render = renderManager.getEntityRenderObject(entity);
        if (render != null) {
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            GlStateManager.color(1, 1, 1, fakeEntity.getAlpha());
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
            GlStateManager.popAttrib();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(FakeEntity entity) {
        return null;
    }
}