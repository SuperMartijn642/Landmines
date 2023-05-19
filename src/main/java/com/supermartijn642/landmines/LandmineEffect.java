package com.supermartijn642.landmines;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public interface LandmineEffect {

    void applyEffect(Level level, BlockPos pos, ItemStack stack);

    LandmineEffect NOTHING = (level, pos, stack) -> {
    };

    LandmineEffect EXPLOSION = (level, pos, stack) -> {
        if(!level.isClientSide)
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, LandminesConfig.explosionCausesFire.get(), LandminesConfig.explosionBreakBlocks.get() ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE);
    };

    LandmineEffect POTION = (level, pos, stack) -> {
        if(!level.isClientSide){
            Potion potion = PotionUtils.getPotion(stack);
            List<MobEffectInstance> mobEffects = PotionUtils.getMobEffects(stack);
            boolean isWater = potion == Potions.WATER && mobEffects.isEmpty();
            if(isWater){ // water potion
                AABB area = new AABB(pos).inflate(4, 2, 4);
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isSensitiveToWater);
                for(LivingEntity entity : entities){
                    double distance = area.getCenter().distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
                    if(distance < 16 && entity.isSensitiveToWater())
                        entity.hurt(DamageSource.indirectMagic(entity, null), 1);
                }
            }else if(!mobEffects.isEmpty()){
                if(stack.getItem() == Items.LINGERING_POTION){ // lingering potion
                    AreaEffectCloud effectCloud = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

                    effectCloud.setRadius(3);
                    effectCloud.setRadiusOnUse(-0.5f);
                    effectCloud.setWaitTime(10);
                    effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());
                    effectCloud.setPotion(potion);

                    for(MobEffectInstance effectInstance : PotionUtils.getCustomEffects(stack))
                        effectCloud.addEffect(new MobEffectInstance(effectInstance));

                    CompoundTag tag = stack.getTag();
                    if(tag != null && tag.contains("CustomPotionColor", 99))
                        effectCloud.setFixedColor(tag.getInt("CustomPotionColor"));

                    level.addFreshEntity(effectCloud);
                }else{ // splash potion
                    AABB area = new AABB(pos).inflate(4, 2, 4);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
                    for(LivingEntity entity : entities){
                        if(entity.isAffectedByPotions()){
                            double distance = area.getCenter().distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
                            if(distance < 16){
                                double closenessFactor = 1 - Math.sqrt(distance) / 4;

                                for(MobEffectInstance effectInstance : mobEffects){
                                    MobEffect effect = effectInstance.getEffect();
                                    if(effect.isInstantenous())
                                        effect.applyInstantenousEffect(null, null, entity, effectInstance.getAmplifier(), closenessFactor);
                                    else{
                                        int duration = (int)(closenessFactor * effectInstance.getDuration() + 0.5);
                                        if(duration > 20)
                                            entity.addEffect(new MobEffectInstance(effect, duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int i = potion.hasInstantEffects() ? 2007 : 2002;
            level.levelEvent(i, pos, PotionUtils.getColor(stack));
        }
    };

    LandmineEffect LAUNCH = (level, pos, stack) -> {
        level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(0.3))
            .forEach(entity -> entity.push(0, LandminesConfig.launchForce.get(), 0));
        level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1, 0.8f);
    };

    LandmineEffect TELEPORT = (level, pos, stack) -> {
        if(!level.isClientSide){
            double range = LandminesConfig.teleportRange.get();
            level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(0.7))
                .forEach(entity -> {
                    double entityX = entity.getX();
                    double entityY = entity.getY();
                    double entityZ = entity.getZ();

                    for(int i = 0; i < 16; ++i){
                        double teleportX = entity.getX() + (level.getRandom().nextDouble() - 0.5) * 2 * range;
                        double teleportY = Mth.clamp(entity.getY() + (level.getRandom().nextDouble() - 0.5) * 2 * range, 0, level.getHeight() - 1);
                        double teleportZ = entity.getZ() + (level.getRandom().nextDouble() - 0.5) * 2 * range;
                        if(entity.isPassenger())
                            entity.stopRiding();

                        if(entity.randomTeleport(teleportX, teleportY, teleportZ, true)){
                            SoundEvent soundEvent = entity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                            level.playSound(null, entityX, entityY, entityZ, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                            entity.playSound(soundEvent, 1.0F, 1.0F);
                            break;
                        }
                    }
                });
        }
    };

    LandmineEffect FIRE = (level, pos, stack) -> {
        level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(0.7))
            .forEach(entity -> {
                if(!(entity instanceof Player && ((Player)entity).isCreative()))
                    entity.setSecondsOnFire(5);
            });
    };

    LandmineEffect SNOW = (level, pos, stack) -> {
        if(!level.isClientSide){
            int maxRange = LandminesConfig.snowRange.get();
            for(int x = -maxRange; x <= maxRange; x++){
                for(int y = 1; y >= -1; y--){
                    for(int z = -maxRange; z <= maxRange; z++){
                        int distance = x * x + z * z;
                        if(distance > maxRange * maxRange || !level.isEmptyBlock(pos.offset(x, y, z)))
                            continue;

                        int layers = level.getRandom().nextInt(Math.min(7, Math.max(1, (int)Math.ceil((maxRange - Math.sqrt(distance)) / maxRange * 7))) + 1) + 1;

                        BlockState state = Blocks.SNOW.defaultBlockState();
                        state = state.setValue(SnowLayerBlock.LAYERS, level.getRandom().nextInt(layers) + 1);
                        if(!state.canSurvive(level, pos.offset(x, y, z)))
                            continue;

                        level.setBlock(pos.offset(x, y, z), state, 1 | 2);
                    }
                }
            }
        }
    };

    LandmineEffect ZOMBIE = (level, pos, stack) -> {
        if(!level.isClientSide){
            int spawnRange = LandminesConfig.zombieRange.get();
            int spawns = 0, targetSpawns = LandminesConfig.zombieCount.get();
            for(int attempts = 0; attempts < Math.max(20, targetSpawns * 3); attempts++){
                int x = (int)((level.getRandom().nextDouble() - 0.5) * 2 * spawnRange);
                int y = level.getRandom().nextInt(5) - 2;
                int z = (int)((level.getRandom().nextDouble() - 0.5) * 2 * spawnRange);
                BlockPos spawnPos = pos.offset(x, y, z);
                if(!level.getBlockState(spawnPos.below()).getCollisionShape(level, spawnPos.below()).isEmpty() &&
                    level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).isEmpty() &&
                    level.getBlockState(spawnPos.above()).getCollisionShape(level, spawnPos.above()).isEmpty()){
                    Entity zombie = EntityType.ZOMBIE.spawn((ServerLevel)level, (ItemStack)null, null, spawnPos, MobSpawnType.TRIGGERED, true, false);
                    if(zombie != null){
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
        level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(0.7))
            .forEach(
                entity -> entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, duration, 1, true, false))
            );
    };

    LandmineEffect LIGHTNING = (level, pos, stack) -> {
        if(!level.isClientSide)
            EntityType.LIGHTNING_BOLT.spawn((ServerLevel)level, (ItemStack)null, null, pos, MobSpawnType.TRIGGERED, true, false);
    };

    LandmineEffect ARROWS = (level, pos, stack) -> {
        if(!level.isClientSide){
            int arrows = LandminesConfig.arrowsCount.get();
            for(int i = 0; i < arrows; i++){
                double angle = Math.PI * 2 / arrows * i;
                Arrow entity = new Arrow(level, pos.getX() + 0.5 + Math.cos(angle), pos.getY() + 0.2, pos.getZ() + 0.5 + Math.sin(angle));
                entity.setDeltaMovement(0.2 * Math.cos(angle) + level.getRandom().nextDouble() * 0.2 - 0.1, 0.2 + level.getRandom().nextDouble() * 0.2 - 0.1, 0.2 * Math.sin(angle) + level.getRandom().nextDouble() * 0.2 - 0.1);
                entity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                level.addFreshEntity(entity);
            }
        }
    };

}
