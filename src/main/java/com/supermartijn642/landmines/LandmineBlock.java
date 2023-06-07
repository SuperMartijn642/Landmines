package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.block.EntityHoldingBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock implements EntityHoldingBlock, SimpleWaterloggedBlock {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(false, BlockProperties.create().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).destroyTime(0.5f).explosionResistance(0.5f));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(ON, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof LandmineBlockEntity && !((LandmineBlockEntity)entity).hasShape() ? BlockShape.empty().getUnderlying() : SHAPE.getUnderlying();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2){
        return state.canSurvive(level, pos) ? super.updateShape(state, direction, state2, level, pos, pos2) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos){
        return canSupportCenter(level, pos.below(), Direction.UP);
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

    @Override
    public BlockEntity createNewBlockEntity(BlockPos pos, BlockState state){
        return this.type.getBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(ON, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof LandmineBlockEntity)
            ((LandmineBlockEntity)blockEntity).onEntityCollide(entity);
    }

    @Override
    protected InteractionFeedback interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, Direction hitSide, Vec3 hitLocation){
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof LandmineBlockEntity)
            return ((LandmineBlockEntity)entity).onRightClick(player, hand) ? InteractionFeedback.SUCCESS : InteractionFeedback.PASS;
        return super.interact(state, level, pos, player, hand, hitSide, hitLocation);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_196243_5_){
        if(!state.is(newState.getBlock())){
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof LandmineBlockEntity)
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), ((LandmineBlockEntity)entity).getStack());

            super.onRemove(state, level, pos, newState, p_196243_5_);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return BlockShape.empty().getUnderlying();
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable BlockGetter level, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("landmines." + this.type.getSuffix() + ".info").color(ChatFormatting.GRAY).get());
        if(this.type.itemFilter != null && this.type.tooltipItem != null)
            info.accept(TextComponents.translation("landmines.info.item", TextComponents.item(this.type.tooltipItem).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
        info.accept(TextComponents.translation("landmines.info.reusable", TextComponents.translation("landmines.info.reusable." + this.type.reusable.get()).color(ChatFormatting.GOLD).get()).color(ChatFormatting.GRAY).get());
    }
}
