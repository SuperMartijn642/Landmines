package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.block.EntityHoldingBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock implements EntityHoldingBlock, IWaterLoggable {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(false, BlockProperties.create(Material.METAL, DyeColor.GRAY).destroyTime(0.5f).explosionResistance(0.5f));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(ON, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context){
        TileEntity entity = level.getBlockEntity(pos);
        return entity instanceof LandmineBlockEntity && !((LandmineBlockEntity)entity).hasShape() ? BlockShape.empty().getUnderlying() : SHAPE.getUnderlying();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, IWorld level, BlockPos pos, BlockPos pos2){
        return state.canSurvive(level, pos) ? super.updateShape(state, direction, state2, level, pos, pos2) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos){
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        IFluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public IFluidState getFluidState(BlockState state){
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.type.getBlockEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder){
        builder.add(ON, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entity){
        TileEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof LandmineBlockEntity)
            ((LandmineBlockEntity)blockEntity).onEntityCollide(entity);
    }

    @Override
    protected InteractionFeedback interact(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide, Vec3d hitLocation){
        TileEntity entity = level.getBlockEntity(pos);
        if(entity instanceof LandmineBlockEntity)
            return ((LandmineBlockEntity)entity).onRightClick(player, hand) ? InteractionFeedback.SUCCESS : InteractionFeedback.PASS;
        return super.interact(state, level, pos, player, hand, hitSide, hitLocation);
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean p_196243_5_){
        if(state.getBlock() != newState.getBlock()){
            TileEntity entity = level.getBlockEntity(pos);
            if(entity instanceof LandmineBlockEntity)
                InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), ((LandmineBlockEntity)entity).getStack());

            super.onRemove(state, level, pos, newState, p_196243_5_);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state){
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos){
        return BlockShape.empty().getUnderlying();
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockReader level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("landmines." + this.type.getSuffix() + ".info").color(TextFormatting.GRAY).get());
        if(this.type.itemFilter != null && this.type.tooltipItem != null)
            info.accept(TextComponents.translation("landmines.info.item", TextComponents.item(this.type.tooltipItem).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
        info.accept(TextComponents.translation("landmines.info.reusable", TextComponents.translation("landmines.info.reusable." + this.type.reusable.get()).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
    }
}
