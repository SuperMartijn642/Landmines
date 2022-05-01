package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockShape;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock implements EntityBlock, SimpleWaterloggedBlock {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(type.getSuffix() + "_landmine", false, Properties.of(Material.METAL, DyeColor.GRAY).strength(0.5f));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(ON, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof LandmineTileEntity && !((LandmineTileEntity)entity).hasShape() ? BlockShape.empty().getUnderlying() : SHAPE.getUnderlying();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor world, BlockPos pos, BlockPos pos2){
        return state.canSurvive(world, pos) ? super.updateShape(state, direction, state2, world, pos, pos2) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
        BlockPos blockpos = pos.below();
        return canSupportCenter(world, blockpos, Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return this.type.getTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == this.type.getTileEntityType() ?
            (world2, pos, state2, entity) -> ((LandmineTileEntity)entity).tick() : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(ON, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity){
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            ((LandmineTileEntity)tileEntity).onEntityCollide(entity);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult){
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            return ((LandmineTileEntity)tileEntity).onRightClick(player, hand) ? InteractionResult.sidedSuccess(world.isClientSide) : InteractionResult.FAIL;
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean p_196243_5_){
        if(!state.is(newState.getBlock())){
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof LandmineTileEntity)
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LandmineTileEntity)tileEntity).getStack());

            super.onRemove(state, world, pos, newState, p_196243_5_);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
        return BlockShape.empty().getUnderlying();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag){
        list.add(TextComponents.translation("landmines." + this.type.getSuffix() + ".info").color(ChatFormatting.GRAY).get());
        if(this.type.itemFilter != null && this.type.tooltipItem != null)
            list.add(TextComponents.translation("landmines.info.item", TextComponents.item(this.type.tooltipItem).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        list.add(TextComponents.translation("landmines.info.reusable", TextComponents.translation("landmines.info.reusable." + this.type.reusable.get()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
    }
}
