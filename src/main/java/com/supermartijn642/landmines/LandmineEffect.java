package com.supermartijn642.landmines;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public interface LandmineEffect {

    void applyEffect(World world, BlockPos pos, ItemStack stack);

    LandmineEffect NOTHING = (world, pos, stack) -> {
    };

    LandmineEffect EXPLOSION = (world, pos, stack) -> {
        if(!world.isClientSide)
            world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, LandminesConfig.explosionCausesFire.get(), LandminesConfig.explosionBreakBlocks.get() ? Explosion.Mode.BREAK : Explosion.Mode.NONE);
    };

    LandmineEffect POTION = (world, pos, stack) -> {
        if(!world.isClientSide){
            Potion potion = PotionUtils.getPotion(stack);
            List<EffectInstance> mobEffects = PotionUtils.getMobEffects(stack);
            boolean isWater = potion == Potions.WATER && mobEffects.isEmpty();
            if(isWater){ // water potion
                AxisAlignedBB area = new AxisAlignedBB(pos).inflate(4, 2, 4);
                List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isSensitiveToWater);
                for(LivingEntity entity : entities){
                    double distance = area.getCenter().distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
                    if(distance < 16 && entity.isSensitiveToWater())
                        entity.hurt(DamageSource.indirectMagic(entity, null), 1);
                }
            }else if(!mobEffects.isEmpty()){
                if(stack.getItem() == Items.LINGERING_POTION){ // lingering potion
                    AreaEffectCloudEntity effectCloud = new AreaEffectCloudEntity(world, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

                    effectCloud.setRadius(3);
                    effectCloud.setRadiusOnUse(-0.5f);
                    effectCloud.setWaitTime(10);
                    effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());
                    effectCloud.setPotion(potion);

                    for(EffectInstance effectinstance : PotionUtils.getCustomEffects(stack))
                        effectCloud.addEffect(new EffectInstance(effectinstance));

                    CompoundNBT compoundnbt = stack.getTag();
                    if(compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99))
                        effectCloud.setFixedColor(compoundnbt.getInt("CustomPotionColor"));

                    world.addFreshEntity(effectCloud);
                }else{ // splash potion
                    AxisAlignedBB area = new AxisAlignedBB(pos).inflate(4, 2, 4);
                    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area);
                    for(LivingEntity entity : entities){
                        if(entity.isAffectedByPotions()){
                            double distance = area.getCenter().distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
                            if(distance < 16){
                                double closenessFactor = 1 - Math.sqrt(distance) / 4;

                                for(EffectInstance effectInstance : mobEffects){
                                    Effect effect = effectInstance.getEffect();
                                    if(effect.isInstantenous())
                                        effect.applyInstantenousEffect(null, null, entity, effectInstance.getAmplifier(), closenessFactor);
                                    else{
                                        int duration = (int)(closenessFactor * effectInstance.getDuration() + 0.5);
                                        if(duration > 20)
                                            entity.addEffect(new EffectInstance(effect, duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int i = potion.hasInstantEffects() ? 2007 : 2002;
            world.levelEvent(i, pos, PotionUtils.getColor(stack));
        }
    };

    LandmineEffect LAUNCH = (world, pos, stack) -> {
        world.getEntitiesOfClass(Entity.class, new AxisAlignedBB(pos).inflate(0.3))
            .forEach(entity -> entity.push(0, LandminesConfig.launchForce.get(), 0));
        world.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 1, 0.8f);
    };

    LandmineEffect TELEPORT = (world, pos, stack) -> {
        if(!world.isClientSide){
            double range = LandminesConfig.teleportRange.get();
            world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(0.7))
                .forEach(entity -> {
                    double entityX = entity.getX();
                    double entityY = entity.getY();
                    double entityZ = entity.getZ();

                    for(int i = 0; i < 16; ++i){
                        double teleportX = entity.getX() + (world.getRandom().nextDouble() - 0.5) * 2 * range;
                        double teleportY = MathHelper.clamp(entity.getY() + (world.getRandom().nextDouble() - 0.5) * 2 * range, 0, world.getHeight() - 1);
                        double teleportZ = entity.getZ() + (world.getRandom().nextDouble() - 0.5) * 2 * range;
                        if(entity.isPassenger()){
                            entity.stopRiding();
                        }

                        if(entity.randomTeleport(teleportX, teleportY, teleportZ, true)){
                            SoundEvent soundevent = entity instanceof FoxEntity ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                            world.playSound(null, entityX, entityY, entityZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            entity.playSound(soundevent, 1.0F, 1.0F);
                            break;
                        }
                    }
                });
        }
    };

    LandmineEffect FIRE = (world, pos, stack) -> {
        world.getEntitiesOfClass(Entity.class, new AxisAlignedBB(pos).inflate(0.7))
            .forEach(entity -> {
                if(!(entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative()))
                    entity.setSecondsOnFire(5);
            });
    };

    LandmineEffect SNOW = (world, pos, stack) -> {
        if(!world.isClientSide){
            int maxRange = LandminesConfig.snowRange.get();
            for(int x = -maxRange; x <= maxRange; x++){
                for(int y = 1; y >= -1; y--){
                    for(int z = -maxRange; z <= maxRange; z++){
                        int distance = x * x + z * z;
                        if(distance > maxRange * maxRange || !world.isEmptyBlock(pos.offset(x, y, z)))
                            continue;

                        int layers = world.getRandom().nextInt(Math.min(7, Math.max(1, (int)Math.ceil((maxRange - Math.sqrt(distance)) / maxRange * 7))) + 1) + 1;

                        BlockState state = Blocks.SNOW.defaultBlockState();
                        state = state.setValue(SnowBlock.LAYERS, world.getRandom().nextInt(layers) + 1);
                        if(!state.canSurvive(world, pos.offset(x, y, z)))
                            continue;

                        world.setBlock(pos.offset(x, y, z), state, 1 | 2);
                    }
                }
            }
        }
    };

    LandmineEffect ZOMBIE = (world, pos, stack) -> {
        if(!world.isClientSide){
            int spawnRange = LandminesConfig.zombieRange.get();
            int spawns = 0, targetSpawns = LandminesConfig.zombieCount.get();
            for(int attempts = 0; attempts < Math.max(20, targetSpawns * 3); attempts++){
                int x = (int)((world.getRandom().nextDouble() - 0.5) * 2 * spawnRange);
                int y = world.getRandom().nextInt(5) - 2;
                int z = (int)((world.getRandom().nextDouble() - 0.5) * 2 * spawnRange);
                BlockPos spawnPos = pos.offset(x, y, z);
                if(!world.getBlockState(spawnPos.below()).getCollisionShape(world, spawnPos.below()).isEmpty() &&
                    world.getBlockState(spawnPos).getCollisionShape(world, spawnPos).isEmpty() &&
                    world.getBlockState(spawnPos.above()).getCollisionShape(world, spawnPos.above()).isEmpty()){
                    Entity zombie = EntityType.ZOMBIE.spawn((ServerWorld)world, null, null, spawnPos, SpawnReason.TRIGGERED, true, false);
                    if(zombie != null){
                        spawns++;
                        if(spawns == targetSpawns)
                            return;
                    }
                }
            }
        }
    };

    LandmineEffect LEVITATION = (world, pos, stack) -> {
        int duration = LandminesConfig.levitationDuration.get();
        world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(0.7))
            .forEach(
                entity -> entity.addEffect(new EffectInstance(Effects.LEVITATION, duration, 1, true, false))
            );
    };

    LandmineEffect LIGHTNING = (world, pos, stack) -> {
        if(!world.isClientSide)
            EntityType.LIGHTNING_BOLT.spawn((ServerWorld)world, null, null, pos, SpawnReason.TRIGGERED, true, false);
    };

    LandmineEffect ARROWS = (world, pos, stack) -> {
        if(!world.isClientSide){
            int arrows = LandminesConfig.arrowsCount.get();
            for(int i = 0; i < arrows; i++){
                double angle = Math.PI * 2 / arrows * i;
                ArrowEntity entity = new ArrowEntity(world, pos.getX() + 0.5 + Math.cos(angle), pos.getY() + 0.2, pos.getZ() + 0.5 + Math.sin(angle));
                entity.setDeltaMovement(0.2 * Math.cos(angle) + world.getRandom().nextDouble() * 0.2 - 0.1, 0.2 + world.getRandom().nextDouble() * 0.2 - 0.1, 0.2 * Math.sin(angle) + world.getRandom().nextDouble() * 0.2 - 0.1);
                entity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                world.addFreshEntity(entity);
            }
        }
    };

}
