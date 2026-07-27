package com.origins_eternity.sanity.content.entity;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import com.origins_eternity.sanity.content.sound.Sounds;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.UUID;

import static com.origins_eternity.sanity.event.CommonEvent.onShadowDeath;
import static com.origins_eternity.sanity.utils.Utils.findSurface;

public class ShadowMonster extends EntityMob implements IAnimatable {

    private UUID playerId;
    private final AnimationFactory factory = new AnimationFactory(this);

    public ShadowMonster(World worldIn) {
        super(worldIn);
        this.setSize(1f, 2.9f);
        this.stepHeight = 1.0f;
        this.experienceValue = 0;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class,
                10, false, false, this::isInSanity));
    }

    private boolean isInSanity(EntityPlayer player) {
        ISanity sanity = player.getCapability(Capabilities.SANITY, null);
        return sanity.getSanity() < 15;
    }

    private int count = 0;

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && !(this.getAttackTarget() instanceof EntityPlayer)) {
            count++;
            if (count >= 200) {
                onShadowDeath(this);
                this.setDead();
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.42D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movement", 5, this::movementPredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (this.isSwingInProgress) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("attack.swing", false));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            if (this.getAttackTarget() != null && !this.isInWater()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("move.run", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("move.walk", true));
            }
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("misc.idle", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() { return Sounds.SHADOW_MONSTER_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return Sounds.SHADOW_MONSTER_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return Sounds.SHADOW_MONSTER_HURT; }

    private void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public static int spawnShadow(EntityPlayer player) {
        World world = player.world;
        double radius = 12 + world.rand.nextDouble() * 8;
        double yawRad = Math.toRadians(player.rotationYaw + 90 + world.rand.nextDouble() * 180);
        double x = (int) (player.posX - Math.sin(yawRad) * radius) + 0.5;
        double z = (int) (player.posZ + Math.cos(yawRad) * radius) + 0.5;
        double y = findSurface(world, new BlockPos(x, (int) player.posY + 5, z));
        if (y != -1) {
            BlockPos pos = new BlockPos(x, y, z);
            ShadowMonster shadow = new ShadowMonster(world);
            shadow.setPosition(x, y, z);
            shadow.setPlayerId(player.getUniqueID());
            shadow.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            if (player.world.spawnEntity(shadow)) {
                return shadow.getEntityId();
            }
        }
        return -1;
    }
}