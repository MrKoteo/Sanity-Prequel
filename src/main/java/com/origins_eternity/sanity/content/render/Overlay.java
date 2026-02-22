package com.origins_eternity.sanity.content.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.origins_eternity.sanity.config.Configuration.Overlay;
import static com.origins_eternity.sanity.event.ClientEvent.*;
import static com.origins_eternity.sanity.utils.proxy.ClientProxy.*;

@SideOnly(Side.CLIENT)
public class Overlay extends Gui {
    @SubscribeEvent()
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            EntityPlayerSP player = mc().player;
            if (!player.isCreative() && !player.isSpectator()) {
                int posX = event.getResolution().getScaledWidth();
                int posY = event.getResolution().getScaledHeight();
                GlStateManager.enableBlend();
                GlStateManager.pushMatrix();
                drawBlood(posX, posY);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                int offX = ((player.getPrimaryHand() == EnumHandSide.RIGHT && !player.getHeldItemOffhand().isEmpty())
                        || (player.getPrimaryHand() == EnumHandSide.LEFT && player.getHeldItemOffhand().isEmpty())) && Overlay.check ? 97 - Overlay.offX : -130 + Overlay.offX;
                posX = posX / 2 + offX;
                posY = posY - 29 - Overlay.offY;
                drawBrain(player, posX, posY);
                GlStateManager.popMatrix();
                mc().getTextureManager().bindTexture(Gui.ICONS);
                GlStateManager.disableBlend();
            }
        }
    }

    private void drawBlood(int posX, int posY) {
        mc().getTextureManager().bindTexture(blood);
        if (value < Overlay.blood && down > 0) {
            float alpha = down % 60 > 30 ? (60 - down) * 0.03f : down * 0.03f;
            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
            drawScaledCustomSizeModalRect(0, 0, 0, 0, 100, 58, posX, posY, 100, 58);
        }
    }

    private void drawBrain(EntityPlayerSP player, int posX, int posY) {
        if (flash > 0 || Overlay.flash == -1) {
            mc().getTextureManager().bindTexture(hud);
            if (value < Overlay.shake) {
                posY += player.ticksExisted % 2;
            }
            double percent = value / 100;
            int consume = 22 - (int) (percent * 22 + 0.5);
            drawTexturedModalRect(posX, posY, 0, 0, 33, 24);
            drawTexturedModalRect(posX, posY + consume + 1, 33, consume + 1, 33, 22 - consume);
            if (glow > 0 && glow % 10 < 5) {
                drawTexturedModalRect(posX, posY, 0, 24, 33, 24);
            }
            if (down > 0) {
                posY += down % 20 < 10 ? 1 : 0;
                drawTexturedModalRect(posX + 13, posY, 66, 0, 6, 24);
            } else if (up > 0) {
                posY -= up % 20 < 10 ? 1 : 0;;
                drawTexturedModalRect(posX + 13, posY, 72, 0, 6, 24);
            }
        }
    }
}