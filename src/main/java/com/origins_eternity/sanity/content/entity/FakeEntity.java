package com.origins_eternity.sanity.content.entity;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
public class FakeEntity extends EntityLiving {
    private final EntityLivingBase living;
    private static int nextClientId = Integer.MIN_VALUE / 2;
    private float offsetYaw;
    private int liveTicks;

    public FakeEntity(World world, Entity entity, int liveTicks) {
        super(world);
        this.noClip = true;
        this.liveTicks = liveTicks;
        this.setEntityInvulnerable(true);
        this.setEntityId(nextClientId++);
        this.living = (EntityLivingBase) entity;
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
        if (world.isRemote) {
            EntityPlayerSP player = mc().player;
            if (player == null) return;
            ISanity sanity = player.getCapability(Capabilities.SANITY, null);
            if (!sanity.getEnable() || isAwake(player) || sanity.getSanity() >= Effect.ghost || ticksExisted > liveTicks) {
                this.setDead();
                return;
            }
            double dx = player.posX - posX;
            double dy = player.posY + player.getEyeHeight() - posY - living.getEyeHeight();
            double dz = player.posZ - posZ;
            double distance = Math.sqrt(dx * dx + dz * dz);
            float headPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(dy, distance)), -50, 50);
            float headYaw = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90f - offsetYaw), -75, 75) + offsetYaw;
            living.posX = posX;
            living.posY = posY;
            living.posZ = posZ;
            living.prevPosX = prevPosX;
            living.prevPosY = prevPosY;
            living.prevPosZ = prevPosZ;
            living.rotationYaw = offsetYaw;
            living.rotationPitch = headPitch;
            living.rotationYawHead = headYaw;
            living.renderYawOffset = offsetYaw;
            living.prevRotationYaw = rotationYaw;
            living.prevRotationPitch = headPitch;
            living.prevRotationYawHead = headYaw;
            living.prevRenderYawOffset = offsetYaw;
            if (ticksExisted % 25 == 0) offsetYaw = headYaw;
        }
    }

    public Entity getFakeEntity() {
        return living;
    }

    public static boolean spawnFakeEntity(EntityPlayer player) {
        World world = player.world;
        String[] args = Effect.ghosts[world.rand.nextInt(Effect.ghosts.length)].split(";");
        ResourceLocation location = new ResourceLocation(args[0]);
        if (EntityList.isRegistered(location) && args.length > 4) {
            Entity entity = EntityList.createEntityByIDFromName(location, world);
            if (entity instanceof EntityLivingBase) {
                int min_radius = Integer.parseInt(args[1]);
                int max_radius = Integer.parseInt(args[2]);
                double radius = 0.5 + min_radius + world.rand.nextDouble() * (max_radius - min_radius);
                double yawRad = Math.toRadians(player.rotationYaw + world.rand.nextDouble() * 120 - 60);
                double x = (int) (player.posX - Math.sin(yawRad) * radius) + 0.5;
                double z = (int) (player.posZ + Math.cos(yawRad) * radius) + 0.5;
                double y = findSurface(world, new BlockPos(x, (int) player.posY + 5, z));
                if (y == -1) return false;
                int min_ticks = Integer.parseInt(args[3]);
                int max_ticks = Integer.parseInt(args[4]);
                int liveTicks = min_ticks + world.rand.nextInt(max_ticks - min_ticks);
                FakeEntity fakeEntity = new FakeEntity(world, entity, liveTicks);
                WorldClient clientWorld = (WorldClient) world;
                fakeEntity.setPosition(x, y, z);
                clientWorld.addEntityToWorld(fakeEntity.getEntityId(), fakeEntity);
                return true;
            }
        }
        return false;
    }
}