package com.origins_eternity.sanity.event;

import com.origins_eternity.sanity.capability.sanity.ISanity;
import com.origins_eternity.sanity.content.shader.SanityShader;
import com.origins_eternity.sanity.content.sound.InSanity;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.origins_eternity.sanity.Sanity.MOD_ID;
import static com.origins_eternity.sanity.capability.Capabilities.SANITY;
import static com.origins_eternity.sanity.config.Configuration.Effect;
import static com.origins_eternity.sanity.config.Configuration.Overlay;
import static com.origins_eternity.sanity.content.entity.FakeEntity.spawnFakeEntity;
import static com.origins_eternity.sanity.utils.Utils.isAwake;
import static com.origins_eternity.sanity.utils.Utils.playRandomSound;
import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public class ClientEvent {
    private static int sound;
    private static int ghost;
    private static int whisper;
    public static int up = -1;
    public static int down = -1;
    public static int glow = -1;
    public static int flash = -1;
    private static float value = -1;
    private static InSanity insanity;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if (player == mc().player) {
                ISanity sanity = player.getCapability(SANITY, null);
                if (!sanity.getEnable()) return;
                if (up > -1) up--;
                if (down > -1) down--;
                if (glow > -1) glow--;
                if (flash > 0) flash--;
                if (player.ticksExisted % 20 == 0) {
                    update(sanity);
                    if (isAwake(player)) return;
                    Random rand = player.world.rand;
                    if (value < Effect.sound) {
                        if (sound > 0) {
                            sound--;
                        } else if (playRandomSound(player)) {
                            sound = (rand.nextInt((int) value + 1) + 64);
                        }
                    }
                    if (value < Effect.ghost) {
                        if (ghost > 0) {
                            ghost--;
                        } else if (spawnFakeEntity(player, 1 - (value / Effect.ghost) * 0.5f)) {
                            ghost = (rand.nextInt((int) value + 1) + 64);
                        }
                    }
                    if (value < Effect.whisper) {
                        SoundHandler soundHandler = mc().getSoundHandler();
                        if (!soundHandler.isSoundPlaying(insanity)) {
                            if (whisper > 0) {
                                whisper--;
                            } else {
                                insanity = new InSanity(player, 1 - (value / Effect.whisper) * 0.8f);
                                soundHandler.playSound(insanity);
                                whisper = Math.min(Effect.interval, rand.nextInt((int) value + 1) + 32);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
            if (insanity == null) {
                SoundHandler soundHandler = mc().getSoundHandler();
                for (int i = 0; i < 6; i++) {
                    insanity = new InSanity(mc().player, 0.001f);
                    soundHandler.playSound(insanity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            SanityShader shader = SanityShader.getInstance();
            if (mc().player == null || mc().world == null) {
                shader.reset();
                return;
            }
            ISanity sanity = mc().player.getCapability(SANITY, null);
            if (!sanity.getEnable()) {
                shader.unload();
                return;
            }
            float value = sanity.getSanity();
            if (value < Effect.shader) {
                float strength = 1f - (value / Effect.shader);
                if (strength > 0f) {
                    if (!shader.isActive()) {
                        shader.load();
                    }
                    shader.update(strength);
                }
            } else if (shader.isActive()) {
                shader.unload();
            }
        }
    }

    private static void update(ISanity sanity) {
        float current = sanity.getSanity();
        if (value == -1) {
            value = current;
            return;
        }
        if (current != value) {
            if (current < value && down <= 1) {
                down = 59;
            } else if (current > value && up <= 1) {
                up = 59;
            }
            if (Math.abs(current - value) >= Overlay.highlight && glow <= 1) {
                glow = 29;
            }
            if (Overlay.flash != -1) {
                flash = Overlay.flash * 20;
            }
            value = current;
        }
    }
}