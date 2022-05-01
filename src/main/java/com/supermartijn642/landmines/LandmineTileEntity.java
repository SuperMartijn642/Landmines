package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseTileEntity;
import com.supermartijn642.core.block.BlockShape;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineTileEntity extends BaseTileEntity {

    public final LandmineType type;
    private LandmineState state = LandmineState.UNARMED, lastState = this.state;
    public boolean collision;
    public int cooldown;
    private ItemStack stack = ItemStack.EMPTY;
    public int renderTransitionTicks = 0;

    public LandmineTileEntity(LandmineType type, BlockPos pos, BlockState state){
        super(type.getTileEntityType(), pos, state);
        this.type = type;
    }

    public void tick(){
        if(this.state == LandmineState.ARMED){
            if(this.cooldown > 0)
                this.cooldown--;
        }else if(this.state == LandmineState.TRIGGERED){
            if(!this.collision)
                this.trigger();
            else
                this.collision = false;
        }
        this.renderTransitionTicks++;
    }

    public boolean onRightClick(Player player, InteractionHand hand){
        if(this.state == LandmineState.UNARMED){
            ItemStack stack = player.getItemInHand(hand);
            if(stack.isEmpty()){
                if(player.isCrouching()){
                    if(this.type.itemFilter == null || !this.stack.isEmpty()){
                        this.updateState(LandmineState.ARMED);
                        this.cooldown = LandminesConfig.activationDelay.get();
                        this.dataChanged();
                        return true;
                    }else if(this.type.tooltipItem != null && !this.level.isClientSide)
                        player.displayClientMessage(TextComponents.translation("landmines.require_item", TextComponents.block(this.type.getBlock()).get(), TextComponents.item(this.type.tooltipItem).color(ChatFormatting.GOLD).get()).color(ChatFormatting.YELLOW).get(), true);
                }else if(!this.stack.isEmpty()){
                    player.setItemInHand(hand, this.stack);
                    this.stack = ItemStack.EMPTY;
                    this.dataChanged();
                    return true;
                }
            }else if(this.stack.isEmpty() && this.type.itemFilter != null && this.type.itemFilter.test(stack)){
                this.stack = stack.copy();
                this.stack.setCount(1);
                stack.shrink(1);
                player.setItemInHand(hand, stack);
                this.dataChanged();
                return true;
            }
        }
        return false;
    }

    public void onEntityCollide(Entity entity){
        if(this.state == LandmineState.ARMED){
            if(this.cooldown > 0)
                return;

            this.level.playSound(null, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5,
                Landmines.trigger_sound, SoundSource.BLOCKS, 0.5f, 1);

            if(this.type.instantTrigger)
                this.trigger();
            else{
                this.updateState(LandmineState.TRIGGERED);
                this.collision = true;
                this.dataChanged();
            }
        }else if(this.state == LandmineState.TRIGGERED)
            this.collision = true;
    }

    private void trigger(){
        ItemStack stack = this.stack;
        if(this.type.reusable.get()){
            this.updateState(LandmineState.UNARMED);
            this.stack = ItemStack.EMPTY;
            this.collision = false;
        }else if(this.getBlockState().getBlock() instanceof LandmineBlock)
            this.level.removeBlock(this.getBlockPos(), false);

        this.type.effect.applyEffect(this.level, this.getBlockPos(), stack);
    }

    private void updateState(LandmineState state){
        this.lastState = this.state;
        this.state = state;
        this.renderTransitionTicks = 0;
        this.setChanged();
    }

    public boolean hasShape(){
        return this.state == LandmineState.UNARMED;
    }

    public LandmineState getState(){
        return this.state;
    }

    public LandmineState getLastState(){
        return this.lastState;
    }

    public BlockState getRenderBlockState(){
        return this.type.getBlock().defaultBlockState()
            .setValue(LandmineBlock.ON, this.state == LandmineState.UNARMED && (this.type.itemFilter == null || !this.stack.isEmpty()));
    }

    public ItemStack getStack(){
        return this.stack;
    }

    @Override
    public AABB getRenderBoundingBox(){
        return BlockShape.createBlockShape(3, -2, 3, 13, 1.125, 13).offset(this.getBlockPos()).simplify();
    }

    @Override
    protected CompoundTag writeData(){
        CompoundTag compound = new CompoundTag();
        compound.putInt("state", this.state.index);
        compound.putInt("lastState", this.lastState.index);
        compound.putBoolean("collision", this.collision);
        compound.putInt("cooldown", this.cooldown);
        compound.put("stack", this.stack.serializeNBT());
        compound.putInt("renderTransitionTicks", this.renderTransitionTicks);
        return compound;
    }

    @Override
    protected void readData(CompoundTag compound){
        this.state = LandmineState.fromIndex(compound.getInt("state"));
        this.lastState = LandmineState.fromIndex(compound.getInt("lastState"));
        this.collision = compound.getBoolean("collision");
        this.cooldown = compound.getInt("cooldown");
        this.stack = ItemStack.of(compound.getCompound("stack"));
        this.renderTransitionTicks = compound.getInt("renderTransitionTicks");
    }

    public enum LandmineState {
        UNARMED(0), ARMED(1), TRIGGERED(2);

        private final int index;

        LandmineState(int index){
            this.index = index;
        }

        private static LandmineState fromIndex(int index){
            for(LandmineState state : LandmineState.values())
                if(state.index == index)
                    return state;
            return null;
        }
    }
}
