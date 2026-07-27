package com.origins_eternity.sanity.content.render;

import com.origins_eternity.sanity.content.entity.ShadowMonster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import static com.origins_eternity.sanity.Sanity.MOD_ID;

public class ShadowRender extends GeoEntityRenderer<ShadowMonster> {

    public ShadowRender(RenderManager renderManager) {
        super(renderManager, new ModelShadow());
        addLayer(new ShadowGlowLayer(this));
    }

    private static class ModelShadow extends AnimatedGeoModel<ShadowMonster> {
        @Override
        public ResourceLocation getModelLocation(ShadowMonster entity) {
            return new ResourceLocation(MOD_ID, "geo/shadow_monster.geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(ShadowMonster entity) {
            return new ResourceLocation(MOD_ID, "textures/entity/shadow_monster.png");
        }

        @Override
        public ResourceLocation getAnimationFileLocation(ShadowMonster entity) {
            return new ResourceLocation(MOD_ID, "animations/shadow_monster.animation.json");
        }
    }

    private static class ShadowGlowLayer extends GeoLayerRenderer<ShadowMonster> {
        private static final ResourceLocation GLOWMASK = new ResourceLocation(MOD_ID, "textures/entity/shadow_monster_glowmask.png");
        private final ShadowRender renderer;

        public ShadowGlowLayer(ShadowRender rendererIn) {
            super(rendererIn);
            this.renderer = rendererIn;
        }

        @Override
        public void render(ShadowMonster entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
            if (entity.isInvisible()) return;
            GeoModel model = renderer.getGeoModelProvider().getModel(renderer.getGeoModelProvider().getModelLocation(entity));
            float prevX = OpenGlHelper.lastBrightnessX;
            float prevY = OpenGlHelper.lastBrightnessY;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().getTextureManager().bindTexture(GLOWMASK);
            renderer.render(model, entity, partialTicks, 1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);
        }

        @Override
        public void doRenderLayer(ShadowMonster entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {}

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}