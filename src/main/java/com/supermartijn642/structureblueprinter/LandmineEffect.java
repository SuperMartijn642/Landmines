package com.supermartijn642.structureblueprinter;

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

    void applyEffect(World world, BlockPos pos, ItemStack stack);

    LandmineEffect NOTHING = (world, pos, stack) -> {
    };

    LandmineEffect EXPLOSION = (world, pos, stack) -> {
        if(!world.isRemote)
            world.newExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, LandminesConfig.explosionCausesFire.get(), LandminesConfig.explosionBreakBlocks.get());
    };

    LandmineEffect POTION = (world, pos, stack) -> {
        if(!world.isRemote){
            PotionType potion = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> mobEffects = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = potion == PotionTypes.WATER && mobEffects.isEmpty();
            if(isWater){ // water potion
                AxisAlignedBB area = new AxisAlignedBB(pos).grow(4, 2, 4);
                List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity instanceof EntityEnderman || entity instanceof EntityBlaze);
                for(EntityLivingBase entity : entities){
                    double distance = area.getCenter().squareDistanceTo(entity.getPositionVector());
                    if(distance < 16 && (entity instanceof EntityEnderman || entity instanceof EntityBlaze))
                        entity.attackEntityFrom(DamageSource.DROWN, 1);
                }
            }else if(!mobEffects.isEmpty()){
                if(stack.getItem() == Items.LINGERING_POTION){ // lingering potion
                    EntityAreaEffectCloud effectCloud = new EntityAreaEffectCloud(world, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

                    effectCloud.setRadius(3);
                    effectCloud.setRadiusOnUse(-0.5f);
                    effectCloud.setWaitTime(10);
                    effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());
                    effectCloud.setPotion(potion);

                    for(PotionEffect effectinstance : PotionUtils.getFullEffectsFromItem(stack))
                        effectCloud.addEffect(new PotionEffect(effectinstance));

                    NBTTagCompound compoundnbt = stack.getTagCompound();
                    if(compoundnbt != null && compoundnbt.hasKey("CustomPotionColor", 99))
                        effectCloud.setColor(compoundnbt.getInteger("CustomPotionColor"));

                    world.spawnEntity(effectCloud);
                }else{ // splash potion
                    AxisAlignedBB area = new AxisAlignedBB(pos).grow(4, 2, 4);
                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
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
            world.playEvent(i, pos, PotionUtils.getColor(stack));
        }
    };

    LandmineEffect LAUNCH = (world, pos, stack) -> {
        world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.3))
            .forEach(entity -> entity.addVelocity(0, LandminesConfig.launchForce.get(), 0));
        world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1, 0.8f);
    };

    LandmineEffect TELEPORT = (world, pos, stack) -> {
        if(!world.isRemote){
            double range = LandminesConfig.teleportRange.get();
            world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(0.7))
                .forEach(entity -> {
                    double entityX = entity.getPositionVector().x;
                    double entityY = entity.getPositionVector().y;
                    double entityZ = entity.getPositionVector().z;

                    for(int i = 0; i < 16; ++i){
                        double teleportX = entity.getPositionVector().x + (world.rand.nextDouble() - 0) * 2 * range;
                        double teleportY = MathHelper.clamp(entity.getPositionVector().y + (world.rand.nextDouble() - 0.5) * 2 * range, 0, world.getHeight() - 1);
                        double teleportZ = entity.getPositionVector().z + (world.rand.nextDouble() - 0) * 2 * range;
                        if(entity.isRiding()){
                            entity.dismountRidingEntity();
                        }

                        if(entity.attemptTeleport(teleportX, teleportY, teleportZ)){
                            SoundEvent soundevent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                            world.playSound(null, entityX, entityY, entityZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            entity.playSound(soundevent, 1.0F, 1.0F);
                            break;
                        }
                    }
                });
        }
    };

    LandmineEffect FIRE = (world, pos, stack) -> {
        world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.7))
            .forEach(entity -> {
                if(!(entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()))
                    entity.setFire(5);
            });
    };

    LandmineEffect SNOW = (world, pos, stack) -> {
        if(!world.isRemote){
            int maxRange = LandminesConfig.snowRange.get();
            for(int x = -maxRange; x <= maxRange; x++){
                for(int y = 1; y >= -1; y--){
                    for(int z = -maxRange; z <= maxRange; z++){
                        int distance = x * x + z * z;
                        if(distance > maxRange * maxRange || !world.isAirBlock(pos.add(x, y, z)))
                            continue;

                        int layers = world.rand.nextInt(Math.min(7, Math.max(1, (int)Math.ceil((maxRange - Math.sqrt(distance)) / maxRange * 7))) + 1) + 1;

                        IBlockState state = Blocks.SNOW_LAYER.getDefaultState();
                        state = state.withProperty(BlockSnow.LAYERS, world.rand.nextInt(layers) + 1);
                        if(!Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos))
                            continue;

                        world.setBlockState(pos.add(x, y, z), state, 1 | 2);
                    }
                }
            }
        }
    };

    LandmineEffect ZOMBIE = (world, pos, stack) -> {
        if(!world.isRemote){
            int spawnRange = LandminesConfig.zombieRange.get();
            int spawns = 0, targetSpawns = LandminesConfig.zombieCount.get();
            for(int attempts = 0; attempts < Math.max(20, targetSpawns * 3); attempts++){
                int x = (int)((world.rand.nextDouble() - 0.5) * 2 * spawnRange);
                int y = world.rand.nextInt(5) - 2;
                int z = (int)((world.rand.nextDouble() - 0.5) * 2 * spawnRange);
                BlockPos spawnPos = pos.add(x, y, z);
                if(world.getBlockState(spawnPos.down()).getCollisionBoundingBox(world, spawnPos.down()) != null && world.getBlockState(spawnPos.down()).getCollisionBoundingBox(world, spawnPos.down()).getAverageEdgeLength() != 0 &&
                    (world.getBlockState(spawnPos).getCollisionBoundingBox(world, spawnPos) == null || world.getBlockState(spawnPos).getCollisionBoundingBox(world, spawnPos).getAverageEdgeLength() == 0) &&
                    (world.getBlockState(spawnPos.up()).getCollisionBoundingBox(world, spawnPos.up()) == null || world.getBlockState(spawnPos.up()).getCollisionBoundingBox(world, spawnPos.up()).getAverageEdgeLength() == 0)){
                    Entity zombie = new EntityZombie(world);
                    zombie.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.55, spawnPos.getZ() + 0.5);

                    if(world.spawnEntity(zombie)){
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
        world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(0.7))
            .forEach(
                entity -> entity.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, duration, 1, true, false))
            );
    };

    LandmineEffect LIGHTNING = (world, pos, stack) -> {
        if(!world.isRemote){
            EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, true);
            world.addWeatherEffect(lightning);
        }
    };

    LandmineEffect ARROWS = (world, pos, stack) -> {
        if(!world.isRemote){
            int arrows = LandminesConfig.arrowsCount.get();
            for(int i = 0; i < arrows; i++){
                double angle = Math.PI * 2 / arrows * i;
                EntityArrow entity = new EntityTippedArrow(world, pos.getX() + 0.5 + Math.cos(angle), pos.getY() + 0.2, pos.getZ() + 0.5 + Math.sin(angle));
                entity.setVelocity(0.2 * Math.cos(angle) + world.rand.nextDouble() * 0.2 - 0.1, 0.2 + world.rand.nextDouble() * 0.2 - 0.1, 0.2 * Math.sin(angle) + world.rand.nextDouble() * 0.2 - 0.1);
                entity.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                world.spawnEntity(entity);
            }
        }
    };

}
