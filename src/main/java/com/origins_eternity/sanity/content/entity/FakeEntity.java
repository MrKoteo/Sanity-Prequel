package com.origins_eternity.sanity.content.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
public class FakeEntity extends EntityLiving {
    private final EntityLivingBase living;
    private final  int existTime;
    private static int nextClientId = Integer.MIN_VALUE / 2;
    private final float offsetYaw;

    public FakeEntity(World world, Entity entity, int existTime) {
        super(world);
        this.living = (EntityLivingBase) entity;
        this.noClip = true;
        this.existTime = existTime;
        this.setEntityInvulnerable(true);
        this.setEntityId(nextClientId++);
        this.offsetYaw = world.rand.nextFloat() * 360;
    }

    @Override
    protected void initEntityAI() {
    }

    @Override
    public boolean canBePushed() { return false; }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) { return null; }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() { return null; }

    @Override
    public void onUpdate() {
        ticksExisted++;
        if (mc().player != null) {
            double dx = mc().player.posX - posX;
            double dz = mc().player.posZ - posZ;
            float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx))) - 90f;
            living.posX = posX;
            living.posY = posY;
            living.posZ = posZ;
            living.prevPosX = prevPosX;
            living.prevPosY = prevPosY;
            living.prevPosZ = prevPosZ;
            living.rotationYaw = offsetYaw;
            living.rotationPitch = rotationPitch;
            living.rotationYawHead = targetYaw;
            living.renderYawOffset = offsetYaw;
            living.prevRotationYaw = rotationYaw;
            living.prevRotationPitch = rotationPitch;
            living.prevRotationYawHead = targetYaw;
            living.prevRenderYawOffset = offsetYaw;
            if (ticksExisted > existTime * 20) this.setDead();
        }
    }

    public Entity getFakeEntity() {
        return living;
    }
}