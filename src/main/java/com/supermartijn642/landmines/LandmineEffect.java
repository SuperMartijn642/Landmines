package com.supermartijn642.landmines;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public interface LandmineEffect {

    void applyEffect(World level, BlockPos pos, ItemStack stack);

    LandmineEffect NOTHING = (level, pos, stack) -> {
    };

    LandmineEffect EXPLOSION = (level, pos, stack) -> {
        if(!level.isRemote)
            level.newExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, LandminesConfig.explosionCausesFire.get(), LandminesConfig.explosionBreakBlocks.get());
    };

    LandmineEffect POTION = (level, pos, stack) -> {
        if(!level.isRemote){
            PotionType potion = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> mobEffects = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = potion == PotionTypes.WATER && mobEffects.isEmpty();
            if(isWater){ // water potion
                AxisAlignedBB area = new AxisAlignedBB(pos).grow(4, 2, 4);
                List<EntityLivingBase> entities = level.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity instanceof EntityEnderman || entity instanceof EntityBlaze);
                for(EntityLivingBase entity : entities){
                    double distance = area.getCenter().squareDistanceTo(entity.getPositionVector());
                    if(distance < 16 && (entity instanceof EntityEnderman || entity instanceof EntityBlaze))
                        entity.attackEntityFrom(DamageSource.DROWN, 1);
                }
            }else if(!mobEffects.isEmpty()){
                if(stack.getItem() == Items.LINGERING_POTION){ // lingering potion
                    EntityAreaEffectCloud effectCloud = new EntityAreaEffectCloud(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

                    effectCloud.setRadius(3);
                    effectCloud.setRadiusOnUse(-0.5f);
                    effectCloud.setWaitTime(10);
                    effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());
                    effectCloud.setPotion(potion);

                    for(PotionEffect effectInstance : PotionUtils.getFullEffectsFromItem(stack))
                        effectCloud.addEffect(new PotionEffect(effectInstance));

                    NBTTagCompound tag = stack.getTagCompound();
                    if(tag != null && tag.hasKey("CustomPotionColor", 99))
                        effectCloud.setColor(tag.getInteger("CustomPotionColor"));

                    level.spawnEntity(effectCloud);
                }else{ // splash potion
                    AxisAlignedBB area = new AxisAlignedBB(pos).grow(4, 2, 4);
                    List<EntityLivingBase> entities = level.getEntitiesWithinAABB(EntityLivingBase.class, area);
                    for(EntityLivingBase entity : entities){
                        if(entity.canBeHitWithPotion()){
                            double distance = area.getCenter().squareDistanceTo(entity.getPositionVector());
                            if(distance < 16){
                                double closenessFactor = 1 - Math.sqrt(distance) / 4;

                                for(PotionEffect effectInstance : mobEffects){
                                    Potion effect = effectInstance.getPotion();
                                    if(effect.isInstant())
                                        effect.affectEntity(null, null, entity, effectInstance.getAmplifier(), closenessFactor);
                                    else{
                                        int duration = (int)(closenessFactor * effectInstance.getDuration() + 0.5);
                                        if(duration > 20)
                                            entity.addPotionEffect(new PotionEffect(effect, duration, effectInstance.getAmplifier(), effectInstance.getIsAmbient(), effectInstance.doesShowParticles()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int i = potion.hasInstantEffect() ? 2007 : 2002;
            level.playEvent(i, pos, PotionUtils.getColor(stack));
        }
    };

    LandmineEffect LAUNCH = (level, pos, stack) -> {
        level.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.3))
            .forEach(entity -> entity.addVelocity(0, LandminesConfig.launchForce.get(), 0));
        level.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1, 0.8f);
    };

    LandmineEffect TELEPORT = (level, pos, stack) -> {
        if(!level.isRemote){
            double range = LandminesConfig.teleportRange.get();
            level.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(0.7))
                .forEach(entity -> {
                    double entityX = entity.getPositionVector().x;
                    double entityY = entity.getPositionVector().y;
                    double entityZ = entity.getPositionVector().z;

                    for(int i = 0; i < 16; ++i){
                        double teleportX = entity.getPositionVector().x + (level.rand.nextDouble() - 0) * 2 * range;
                        double teleportY = MathHelper.clamp(entity.getPositionVector().y + (level.rand.nextDouble() - 0.5) * 2 * range, 0, level.getHeight() - 1);
                        double teleportZ = entity.getPositionVector().z + (level.rand.nextDouble() - 0) * 2 * range;
                        if(entity.isRiding()){
                            entity.dismountRidingEntity();
                        }

                        if(entity.attemptTeleport(teleportX, teleportY, teleportZ)){
                            SoundEvent soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                            level.playSound(null, entityX, entityY, entityZ, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            entity.playSound(soundEvent, 1.0F, 1.0F);
                            break;
                        }
                    }
                });
        }
    };

    LandmineEffect FIRE = (level, pos, stack) -> {
        level.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.7))
            .forEach(entity -> {
                if(!(entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()))
                    entity.setFire(LandminesConfig.fireDuration.get());
            });
    };

    LandmineEffect SNOW = (level, pos, stack) -> {
        if(!level.isRemote){
            int maxRange = LandminesConfig.snowRange.get();
            for(int x = -maxRange; x <= maxRange; x++){
                for(int y = 1; y >= -1; y--){
                    for(int z = -maxRange; z <= maxRange; z++){
                        int distance = x * x + z * z;
                        if(distance > maxRange * maxRange || !level.isAirBlock(pos.add(x, y, z)))
                            continue;

                        int layers = level.rand.nextInt(Math.min(7, Math.max(1, (int)Math.ceil((maxRange - Math.sqrt(distance)) / maxRange * 7))) + 1) + 1;

                        IBlockState state = Blocks.SNOW_LAYER.getDefaultState();
                        state = state.withProperty(BlockSnow.LAYERS, level.rand.nextInt(layers) + 1);
                        if(!Blocks.SNOW_LAYER.canPlaceBlockAt(level, pos))
                            continue;

                        level.setBlockState(pos.add(x, y, z), state, 1 | 2);
                    }
                }
            }
        }
    };

    LandmineEffect ZOMBIE = (level, pos, stack) -> {
        if(!level.isRemote){
            int spawnRange = LandminesConfig.zombieRange.get();
            int spawns = 0, targetSpawns = LandminesConfig.zombieCount.get();
            for(int attempts = 0; attempts < Math.max(20, targetSpawns * 3); attempts++){
                int x = (int)((level.rand.nextDouble() - 0.5) * 2 * spawnRange);
                int y = level.rand.nextInt(5) - 2;
                int z = (int)((level.rand.nextDouble() - 0.5) * 2 * spawnRange);
                BlockPos spawnPos = pos.add(x, y, z);
                if(level.getBlockState(spawnPos.down()).getCollisionBoundingBox(level, spawnPos.down()) != null && level.getBlockState(spawnPos.down()).getCollisionBoundingBox(level, spawnPos.down()).getAverageEdgeLength() != 0 &&
                    (level.getBlockState(spawnPos).getCollisionBoundingBox(level, spawnPos) == null || level.getBlockState(spawnPos).getCollisionBoundingBox(level, spawnPos).getAverageEdgeLength() == 0) &&
                    (level.getBlockState(spawnPos.up()).getCollisionBoundingBox(level, spawnPos.up()) == null || level.getBlockState(spawnPos.up()).getCollisionBoundingBox(level, spawnPos.up()).getAverageEdgeLength() == 0)){
                    Entity zombie = new EntityZombie(level);
                    zombie.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.55, spawnPos.getZ() + 0.5);

                    if(level.spawnEntity(zombie)){
                        spawns++;
                        if(spawns == targetSpawns)
                            return;
                    }
                }
            }
        }
    };

    LandmineEffect LEVITATION = (level, pos, stack) -> {
        int duration = LandminesConfig.levitationDuration.get();
        level.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(0.7))
            .forEach(
                entity -> entity.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, duration, 1, true, false))
            );
    };

    LandmineEffect LIGHTNING = (level, pos, stack) -> {
        if(!level.isRemote){
            EntityLightningBolt lightning = new EntityLightningBolt(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, true);
            level.addWeatherEffect(lightning);
        }
    };

    LandmineEffect ARROWS = (level, pos, stack) -> {
        if(!level.isRemote){
            int arrows = LandminesConfig.arrowsCount.get();
            for(int i = 0; i < arrows; i++){
                double angle = Math.PI * 2 / arrows * i;
                EntityArrow entity = new EntityTippedArrow(level, pos.getX() + 0.5 + Math.cos(angle), pos.getY() + 0.2, pos.getZ() + 0.5 + Math.sin(angle));
                entity.setVelocity(0.2 * Math.cos(angle) + level.rand.nextDouble() * 0.2 - 0.1, 0.2 + level.rand.nextDouble() * 0.2 - 0.1, 0.2 * Math.sin(angle) + level.rand.nextDouble() * 0.2 - 0.1);
                entity.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                level.spawnEntity(entity);
            }
        }
    };

}
