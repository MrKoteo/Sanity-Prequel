package com.origins_eternity.sanity.content.sound;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.origins_eternity.sanity.config.Configuration.Effect;
import static com.origins_eternity.sanity.content.sound.Sounds.INSANITY;
import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
public class InSanity extends MovingSound {
    private final EntityPlayer player;
    private int count;

    public InSanity(EntityPlayer player, float volume) {
        super(INSANITY, SoundCategory.HOSTILE);
        this.player = player;
        this.volume = volume;

        if (player != null) {
            this.xPosF = (float) player.posX;
            this.yPosF = (float) player.posY;
            this.zPosF = (float) player.posZ;

            Random rand = player.world.rand;
            this.count = rand.nextInt(520) + 180;
        }
    }

    @Override
    public void update() {
        if (this.player == null || this.player.isDead || !this.player.isEntityAlive() || this.player != mc().player) {
            this.donePlaying = true;
            return;
        }
        ISanity sanity = this.player.getCapability(Capabilities.SANITY, null);
        if (sanity.getSanity() >= Effect.whisper || !sanity.getEnable() || count < 0) {
            if (this.volume > 0f) {
                volume -= 0.02f;
            } else {
                this.donePlaying = true;
                return;
            }
        }
        this.xPosF = (float) this.player.posX;
        this.yPosF = (float) this.player.posY;
        this.zPosF = (float) this.player.posZ;
        count--;
    }
}