package com.origins_eternity.sanity.event;

import com.origins_eternity.sanity.content.capability.sanity.ISanity;
import com.origins_eternity.sanity.content.sound.InSanity;
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
    private static int sound;
    private static InSanity insanity;
    public static int up = -1;
    public static int down = -1;
    public static int glow = -1;
    public static double value = -1;
    public static int flash = -1;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if (!player.isCreative() && !player.isSpectator() && player == mc().player) {
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
                    if (insanity == null || insanity.isDonePlaying()) {
                        insanity = new InSanity(INSANITY, player);
                        mc().getSoundHandler().playSound(insanity);
                    }
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
        if (up > -1) up--;
        if (down > -1) down--;
        if (glow > -1) glow--;
        if (flash > 0) flash--;
        if (sanity.getSanity() != value) {
            if (sanity.getSanity() < value && down <= 1) {
                down = 59;
            } else if (sanity.getSanity() > value && up <= 1) {
                up = 59;
            }
            if (Math.abs(sanity.getSanity() - value) >= 1.0 && glow <= 1) {
                glow = 29;
            }
            if (Overlay.flash != -1) {
                flash = Overlay.flash * 20;
            }
            value = sanity.getSanity();
        }
    }
}