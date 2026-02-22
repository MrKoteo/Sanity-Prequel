package com.origins_eternity.sanity.event;

import com.origins_eternity.sanity.content.capability.sanity.ISanity;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.util.Random;

import static com.origins_eternity.sanity.Sanity.MOD_ID;
import static com.origins_eternity.sanity.config.Configuration.Effect;
import static com.origins_eternity.sanity.config.Configuration.Overlay;
import static com.origins_eternity.sanity.content.capability.Capabilities.SANITY;
import static com.origins_eternity.sanity.content.sound.Sounds.INSANITY;
import static com.origins_eternity.sanity.utils.Utils.isAwake;
import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public class ClientEvent {
    private static final Random rand = new Random();

    public static int up = 0;
    public static int down = 0;
    public static int glow = 0;
    public static double value = -1;
    public static int flash = Overlay.flash;
    private static int sound;
    private static int whisper;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!player.isCreative() && !player.isSpectator() && player == mc().player) {
            if (event.phase != TickEvent.Phase.END) return;
            ISanity sanity = player.getCapability(SANITY, null);
            if (!sanity.getEnable() || isAwake(player)) return;
            update(sanity);
            if (value < Effect.sound) {
                sound--;
                if (sound <= 0) {
                    int number = rand.nextInt(Effect.sounds.length);
                    SoundEvent sounds = SoundEvent.REGISTRY.getObject(new ResourceLocation(Effect.sounds[number]));
                    if (sounds != null) {
                        player.playSound(sounds, 1f, 0.5f);
                    }
                    sound = rand.nextInt(600) + 800;
                }
            }
            if (value < Effect.whisper) {
                whisper--;
                ISound insanity = PositionedSoundRecord.getMasterRecord(INSANITY, 1f);
                if (whisper <= 0) {
                    mc().getSoundHandler().playSound(insanity);
                    whisper = 680;
                }
            }
            if (Overlay.flash != -1) {
                if (up > 0 || down > 0) {
                    flash = Overlay.flash * 20;
                } else if (flash > 0) {
                    flash--;
                }
            }
        }
    }

    private static final String LEVEL1 = "shaders/post/" + Effect.level1.split(";")[0];
    private static final String LEVEL2 = "shaders/post/" + Effect.level2.split(";")[0];
    private static final String LEVEL3 = "shaders/post/" + Effect.level3.split(";")[0];

    private static final int num1 = Integer.parseInt(Effect.level1.split(";")[1]);
    private static final int num2 = Integer.parseInt(Effect.level2.split(";")[1]);
    private static final int num3 = Integer.parseInt(Effect.level3.split(";")[1]);

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        EntityPlayer player = mc().player;
        if (Effect.shader && event.phase == TickEvent.Phase.END && player != null && OpenGlHelper.shadersSupported) {
            ISanity sanity = player.getCapability(SANITY, null);
            EntityRenderer renderer = mc().entityRenderer;
            if (!sanity.getEnable() || isAwake(player)) {
                clearShader(renderer);
            } else if (sanity.getSanity() <= num3) {
                useEffect(renderer, LEVEL3);
            } else if (sanity.getSanity() <= num2) {
                useEffect(renderer, LEVEL2);
            } else if (sanity.getSanity() <= num1) {
                useEffect(renderer, LEVEL1);
            } else {
                clearShader(renderer);
            }
        }
    }

    private static String current = "default";

    private static void clearShader(EntityRenderer renderer) {
        if (!current.equals("default")) {
            renderer.stopUseShader();
            current = "default";
        }
    }

    private static void useEffect(EntityRenderer renderer, String name) {
        if (!current.equals(name)) {
            renderer.loadShader(new ResourceLocation(name));
            current = name;
        }
    }

    private static void update(ISanity sanity) {
        if (value == -1) {
            value = sanity.getSanity();
            return;
        }
        if (up > 0) up--;
        if (down > 0) down--;
        if (glow > 0) glow--;
        if (sanity.getSanity() < value && down == 0) {
            down = 59;
        } else if (sanity.getSanity() > value && up == 0) {
            up = 59;
        } else {
            return;
        }
        if (Math.abs(sanity.getSanity() - value) >= 1.0) {
            glow = 29;
        }
        value = sanity.getSanity();
    }
}