package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.block.TickableBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    public final LandmineType type;
    private LandmineState state = LandmineState.UNARMED, lastState = this.state;
    public boolean collision;
    public int cooldown;
    private ItemStack stack = ItemStack.EMPTY;
    public int renderTransitionTicks = 0;

    public LandmineBlockEntity(LandmineType type){
        super(type.getBlockEntityType());
        this.type = type;
    }

    @Override
    public void update(){
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

    public boolean onRightClick(EntityPlayer player, EnumHand hand){
        if(this.state == LandmineState.UNARMED){
            ItemStack stack = player.getHeldItem(hand);
            if(stack.isEmpty()){
                if(player.isSneaking()){
                    if(this.type.itemFilter == null || !this.stack.isEmpty()){
                        this.updateState(LandmineState.ARMED);
                        this.cooldown = LandminesConfig.activationDelay.get();
                        this.dataChanged();
                        return true;
                    }else if(this.type.tooltipItem != null && !this.world.isRemote)
                        player.sendStatusMessage(TextComponents.translation("landmines.require_item", TextComponents.block(this.type.getBlock()).get(), TextComponents.item(this.type.tooltipItem).color(TextFormatting.GOLD).get()).color(TextFormatting.YELLOW).get(), true);
                }else if(!this.stack.isEmpty()){
                    player.setHeldItem(hand, this.stack);
                    this.stack = ItemStack.EMPTY;
                    this.dataChanged();
                    return true;
                }
            }else if(this.stack.isEmpty() && this.type.itemFilter != null && this.type.itemFilter.test(stack)){
                this.stack = stack.copy();
                this.stack.setCount(1);
                stack.shrink(1);
                player.setHeldItem(hand, stack);
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

            this.world.playSound(null, this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5,
                Landmines.trigger_sound, SoundCategory.BLOCKS, 0.5f, 1);

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
            this.world.setBlockToAir(this.getPos());

        this.type.effect.applyEffect(this.world, this.getPos(), stack);
    }

    private void updateState(LandmineState state){
        this.lastState = this.state;
        this.state = state;
        this.renderTransitionTicks = 0;
        this.markDirty();
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

    public IBlockState getRenderBlockState(){
        return this.type.getBlock().getDefaultState()
            .withProperty(LandmineBlock.ON, this.state == LandmineState.UNARMED && (this.type.itemFilter == null || !this.stack.isEmpty()));
    }

    public ItemStack getStack(){
        return this.stack;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return BlockShape.createBlockShape(3, -2, 3, 13, 1.125, 13).offset(this.getPos()).simplify();
    }

    @Override
    protected NBTTagCompound writeData(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("state", this.state.index);
        compound.setInteger("lastState", this.lastState.index);
        compound.setBoolean("collision", this.collision);
        compound.setInteger("cooldown", this.cooldown);
        compound.setTag("stack", this.stack.serializeNBT());
        compound.setInteger("renderTransitionTicks", this.renderTransitionTicks);
        return compound;
    }

    @Override
    protected void readData(NBTTagCompound compound){
        this.state = LandmineState.fromIndex(compound.getInteger("state"));
        this.lastState = LandmineState.fromIndex(compound.getInteger("lastState"));
        this.collision = compound.getBoolean("collision");
        this.cooldown = compound.getInteger("cooldown");
        this.stack = new ItemStack(compound.getCompoundTag("stack"));
        this.renderTransitionTicks = compound.getInteger("renderTransitionTicks");
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
