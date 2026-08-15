package com.origins_eternity.sanity.content.entity;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.origins_eternity.sanity.config.Configuration.Effect;
import static com.origins_eternity.sanity.utils.Utils.findSurface;
import static com.origins_eternity.sanity.utils.Utils.isAwake;
import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
public class FakeEntity extends Entity {
    private float alpha;
    private float offsetYaw;
    private int liveTicks;
    private final EntityLivingBase living;
    private static int nextID = Integer.MIN_VALUE / 2;

    public FakeEntity(World world, Entity entity) {
        super(world);
        this.noClip = true;
        this.setEntityInvulnerable(true);
        this.setEntityId(nextID++);

        this.living = (EntityLivingBase) entity;
        this.offsetYaw = world.rand.nextFloat() * 360;

        this.living.limbSwing = 0;
        this.living.limbSwingAmount = 0;
        this.living.prevLimbSwingAmount = 0;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) return;

        EntityPlayerSP player = mc().player;
        if (player == null) return;

        ISanity sanity = player.getCapability(Capabilities.SANITY, null);
        if (!sanity.getEnable() || isAwake(player) || sanity.getSanity() >= Effect.ghost || ticksExisted > liveTicks) {
            this.setDead();
            return;
        }

        living.posX = posX;
        living.posY = posY;
        living.posZ = posZ;
        living.prevPosX = prevPosX;
        living.prevPosY = prevPosY;
        living.prevPosZ = prevPosZ;

        double dx = player.posX - posX;
        double dy = player.posY + player.getEyeHeight() - posY - living.getEyeHeight();
        double dz = player.posZ - posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        float headPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(dy, distance)), -50, 50);
        float headYaw = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90f - offsetYaw), -75, 75) + offsetYaw;

        living.rotationYaw = offsetYaw;
        living.rotationPitch = headPitch;
        living.rotationYawHead = headYaw;
        living.renderYawOffset = offsetYaw;
        living.prevRotationYaw = rotationYaw;
        living.prevRotationPitch = headPitch;
        living.prevRotationYawHead = headYaw;
        living.prevRenderYawOffset = offsetYaw;

        living.ticksExisted = this.ticksExisted;
        living.prevLimbSwingAmount = living.limbSwingAmount;

        living.limbSwing += 0.03f;
        living.limbSwingAmount = 0.15f;

        if (ticksExisted % 25 == 0) {
            offsetYaw = headYaw;
        }
    }

    public Entity getFakeEntity() {
        return living;
    }

    public float getAlpha() {
        return alpha;
    }

    private void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    private void setLiveTicks(int liveTicks) {
        this.liveTicks = liveTicks;
    }

    public static boolean spawnFakeEntity(EntityPlayer player, float alpha) {
        World world = player.world;
        String[] args = Effect.ghosts[world.rand.nextInt(Effect.ghosts.length)].split(";");
        ResourceLocation location = new ResourceLocation(args[0]);

        if (EntityList.isRegistered(location) && args.length > 4) {
            Entity entity = EntityList.createEntityByIDFromName(location, world);
            if (entity instanceof EntityLivingBase) {
                int minRadius = Integer.parseInt(args[1]);
                int maxRadius = Integer.parseInt(args[2]);
                double radius = 0.5 + minRadius + world.rand.nextDouble() * (maxRadius - minRadius);
                double yawRad = Math.toRadians(player.rotationYaw + world.rand.nextDouble() * 120 - 60);
                double x = (int) (player.posX - Math.sin(yawRad) * radius) + 0.5;
                double z = (int) (player.posZ + Math.cos(yawRad) * radius) + 0.5;
                double y = findSurface(world, new BlockPos(x, (int) player.posY + 5, z));
                if (y == -1) return false;

                int minTicks = Integer.parseInt(args[3]);
                int maxTicks = Integer.parseInt(args[4]);
                int liveTicks = minTicks + world.rand.nextInt(maxTicks - minTicks);

                FakeEntity fakeEntity = new FakeEntity(world, entity);
                fakeEntity.setAlpha((float) Math.max(alpha, Effect.opacity));
                fakeEntity.setLiveTicks(liveTicks);
                fakeEntity.setPosition(x, y, z);

                WorldClient clientWorld = (WorldClient) world;
                return clientWorld.spawnEntity(fakeEntity);
            }
        }
        return false;
    }
}