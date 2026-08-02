package com.origins_eternity.sanity.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import com.origins_eternity.sanity.capability.sanity.Sanity;
import com.origins_eternity.sanity.compat.FoodSpoiling;
import com.origins_eternity.sanity.content.armor.Garland;
import com.origins_eternity.sanity.content.entity.ShadowMonster;
import mod.acgaming.foodspoiling.logic.FSData;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

import static com.origins_eternity.sanity.Sanity.MOD_ID;
import static com.origins_eternity.sanity.capability.Capabilities.SANITY;
import static com.origins_eternity.sanity.config.Configuration.Effect;
import static com.origins_eternity.sanity.config.Configuration.Mechanics;
import static com.origins_eternity.sanity.content.entity.ShadowMonster.spawnShadow;
import static com.origins_eternity.sanity.utils.Utils.*;
import static mod.acgaming.foodspoiling.logic.FSLogic.canRot;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEvent {
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(MOD_ID, "sanity"), new Sanity.SanityProvider(SANITY));
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        EntityPlayer old = event.getOriginal();
        EntityPlayer clone = event.getEntityPlayer();
        if (!clone.world.isRemote) {
            Capability<ISanity> capability = SANITY;
            ISanity origin = old.getCapability(capability, null);
            ISanity present = clone.getCapability(capability, null);
            if (!event.isWasDeath() || !Mechanics.reset) {
                capability.getStorage().readNBT(capability, present, null, capability.getStorage().writeNBT(capability, origin, null));
            }
        }
    }

    @SubscribeEvent
    public static void LivingEntityUseItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            if (!player.isCreative()) {
                double value = 0;
                ItemStack stack = event.getItem();
                int num = stackMatched(stack, Mechanics.items);
                if (num != -1) {
                    value = Double.parseDouble(Mechanics.items[num].split(";")[1]);
                } else if (stack.getItem() instanceof ItemFood) {
                    value = Mechanics.food;
                }
                ISanity sanity = player.getCapability(SANITY, null);
                if (value > 0) {
                    if (Loader.isModLoaded("foodspoiling")) {
                        if (canRot(stack) == EnumActionResult.SUCCESS && FSData.hasCreationTime(stack)) {
                            double spoilage = FoodSpoiling.getPercentage(event.getItem(), player) / 100.0;
                            value *= spoilage;
                        }
                    }
                    sanity.recoverSanity(value);
                } else {
                    sanity.consumeSanity(-value);
                }
            }
        }
    }

    static long time;

    @SubscribeEvent
    public static void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        if (!event.getEntityPlayer().world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (!player.isCreative()) {
                time = player.world.getWorldTime();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!event.getEntityPlayer().world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (!player.isCreative()) {
                double sleep = (player.world.getWorldTime() - time) / 12000.0;
                ISanity sanity = player.getCapability(SANITY, null);
                sanity.recoverSanity(Mechanics.sleep * sleep);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!player.world.isRemote) {
                ISanity sanity = player.getCapability(SANITY, null);
                sanity.consumeSanity(Mechanics.lightning);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (!player.isCreative() && !player.world.isRemote) {
                ISanity sanity = player.getCapability(SANITY, null);
                int num = entityMatched(event.getEntity(), 2);
                if (num != -1) {
                    double value = Double.parseDouble(Mechanics.entities[num].split(";")[2]);
                    if (value > 0) {
                        sanity.recoverSanity(value);
                    } else {
                        sanity.consumeSanity(-value);
                    }
                } else if (event.getEntity() instanceof EntityAnimal) {
                    sanity.consumeSanity(Mechanics.attackAnimal);
                } else if (event.getEntity() instanceof EntityVillager) {
                    sanity.consumeSanity(Mechanics.attackVillager);
                } else if (event.getEntity() instanceof EntityPlayer) {
                    sanity.consumeSanity(Mechanics.attackPlayer);
                }
            }
        }
    }

    private static final Map<UUID, Integer> PLAYER_COOLDOWN = new HashMap<>();
    private static final Map<UUID, Integer> PLAYER_SHADOW = new HashMap<>();

    public static void onShadowDeath(ShadowMonster shadow) {
        UUID uuid = shadow.getPlayerId();
        if (uuid == null) return;
        EntityPlayer player = shadow.world.getPlayerEntityByUUID(uuid);
        if (player != null) {
            PLAYER_COOLDOWN.put(uuid, player.ticksExisted + player.world.rand.nextInt(320) + 640);
        } else {
            PLAYER_COOLDOWN.remove(uuid);
        }
        PLAYER_SHADOW.remove(uuid);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        ISanity sanity = player.getCapability(SANITY, null);
        boolean invalidPlayer = player.isCreative() || player.isSpectator();
        boolean shouldActive = !invalidPlayer && validDimension(player.dimension);
        if (sanity.getEnable() != shouldActive) {
            sanity.setEnable(shouldActive);
            return;
        }
        if (shouldActive && player.ticksExisted % 10 == 0 && !player.world.isRemote) {
            double value = tickPlayer(player);
            if (value > 0) {
                sanity.recoverSanity(value);
            } else if (value < 0) {
                sanity.consumeSanity(-value);
            }
            sanity.removeCoolDown();
            syncSanity(player);
            if (sanity.getSanity() < Effect.shadow) {
                UUID uuid = player.getUniqueID();
                if (!PLAYER_SHADOW.containsKey(uuid)) {
                    int spawnTick = PLAYER_COOLDOWN.getOrDefault(uuid, 0);
                    if (player.ticksExisted < spawnTick) {
                        return;
                    }
                    int entityID = spawnShadow(player);
                    if (entityID != -1) {
                        PLAYER_SHADOW.put(uuid, entityID);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() != null) {
            EntityPlayer player = event.getCausedByPlayer();
            if (!player.world.isRemote) {
                ISanity sanity = player.getCapability(SANITY, null);
                if (sanity.getCoolDown() == 0) {
                    sanity.recoverSanity(Mechanics.bred);
                    sanity.setCoolDown(20);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        if (event.getAdvancement().getDisplay() == null || !event.getAdvancement().getDisplay().shouldAnnounceToChat()) return;
        EntityPlayer player = event.getEntityPlayer();
        if (!player.isCreative() && !player.world.isRemote) {
            ISanity sanity = player.getCapability(SANITY, null);
            sanity.recoverSanity(Mechanics.advancement);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        if (!player.isCreative() && !player.world.isRemote) {
            ISanity sanity = player.getCapability(SANITY, null);
            sanity.setEnable(validDimension(player.dimension));
            sanity.consumeSanity(Mechanics.trip);
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().world.isRemote) {
            if (event.getEntity() instanceof EntityTameable) {
                EntityTameable entityTameable = (EntityTameable) event.getEntity();
                if (entityTameable.isTamed() && entityTameable.getOwner() != null && entityTameable.getOwner() instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entityTameable.getOwner();
                    ISanity sanity = player.getCapability(SANITY, null);
                    sanity.consumeSanity(Mechanics.lost);
                }
            } else if (event.getEntity() instanceof EntityLiving && event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
                ISanity sanity = player.getCapability(SANITY, null);
                int num = entityMatched(event.getEntity(), 3);
                double value = num == -1 ? Mechanics.killMob : Double.parseDouble(Mechanics.entities[num].split(";")[3]);
                if (sanity.getCoolDown() == 0) {
                    if (value > 0) {
                        sanity.recoverSanity(value);
                    } else {
                        sanity.consumeSanity(-value);
                    }
                    sanity.setCoolDown(20);
                }
            }
            if (event.getEntity() instanceof ShadowMonster) {
                onShadowDeath((ShadowMonster) event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!player.world.isRemote) {
                ISanity sanity = player.getCapability(SANITY, null);
                sanity.consumeSanity(Mechanics.hurt * event.getAmount());
                int damage = 0;
                if (event.getSource() == DamageSource.LIGHTNING_BOLT) {
                    damage = 30;
                } else if (event.getSource().isFireDamage()) {
                    damage = 20;
                } else if (event.getSource().isExplosion()) {
                    damage = 10;
                }
                if (damage != 0) {
                    ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                    ItemStack bauble = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.HEAD.getValidSlots()[0]);
                    if (armor.getItem().equals(Garland.GARLAND)) {
                        armor.damageItem(damage, player);
                    }
                    if (bauble.getItem().equals(Garland.GARLAND)) {
                        bauble.damageItem(damage, player);
                    }
                }
            }
        }
    }

    private static final Map<BlockPos, JukeboxData> PLAYING_JUKEBOXES = new HashMap<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            BlockPos pos = event.getPos();
            if (PLAYING_JUKEBOXES.containsKey(pos)) return;
            if (world.getBlockState(pos).getBlock().equals(Blocks.JUKEBOX)
                    && event.getItemStack().getItem() instanceof ItemRecord) {
                int num = stackMatched(event.getItemStack(), Mechanics.records);
                if (num != -1 && !PLAYING_JUKEBOXES.containsKey(pos)) {
                    String[] args = Mechanics.records[num].split(";");
                    double value = Double.parseDouble(args[1]);
                    int duration = Integer.parseInt(args[2]);
                    AxisAlignedBB box = new AxisAlignedBB(
                            pos.getX() + 0.5 - Mechanics.jukebox, pos.getY() + 0.5 - Mechanics.jukebox, pos.getZ() + 0.5 - Mechanics.jukebox,
                            pos.getX() + 0.5 + Mechanics.jukebox, pos.getY() + 0.5 + Mechanics.jukebox, pos.getZ() + 0.5 + Mechanics.jukebox
                    );
                    List<EntityPlayer> players = new ArrayList<>(world.getEntitiesWithinAABB(EntityPlayer.class, box));
                    PLAYING_JUKEBOXES.put(pos, new JukeboxData(players, value, duration, world.getTotalWorldTime()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        World world = event.world;
        if (!world.isRemote && world.getTotalWorldTime() % 10 == 0) {
            PLAYING_JUKEBOXES.entrySet().removeIf(entry -> {
                BlockPos pos = entry.getKey();
                JukeboxData data = entry.getValue();
                if (!world.isBlockLoaded(pos)) return true;
                IBlockState state = world.getBlockState(pos);
                if (!state.getBlock().equals(Blocks.JUKEBOX) || !state.getValue(BlockJukebox.HAS_RECORD)) {
                    return true;
                }
                return world.getTotalWorldTime() - data.startTime > data.duration * 20L;
            });

            for (Map.Entry<BlockPos, JukeboxData> entry : PLAYING_JUKEBOXES.entrySet()) {
                BlockPos pos = entry.getKey();
                JukeboxData data = entry.getValue();
                for (EntityPlayer player : data.players) {
                    if (player.getDistanceSq(pos) <= Mechanics.jukebox * Mechanics.jukebox) {
                        ISanity sanity = player.getCapability(SANITY, null);
                        if (data.value > 0) {
                            sanity.recoverSanity(data.value);
                        } else if (data.value < 0) {
                            sanity.consumeSanity(-data.value);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        PLAYING_JUKEBOXES.values().forEach(data -> data.players.remove(player));
    }

    private static class JukeboxData {
        public List<EntityPlayer> players;
        public double value;
        public int duration;
        public long startTime;

        JukeboxData(List<EntityPlayer> players, double value, int duration, long startTime) {
            this.players = players;
            this.value = value;
            this.duration = duration;
            this.startTime = startTime;
        }
    }
}