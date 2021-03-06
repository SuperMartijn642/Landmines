package com.supermartijn642.landmines;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockShape;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlock extends BaseBlock implements IWaterLoggable {

    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 0.75, 13),
        BlockShape.createBlockShape(5, 0.75, 5, 11, 1.25, 11)
    );

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public final LandmineType type;

    public LandmineBlock(LandmineType type){
        super(type.getSuffix() + "_landmine", false, Properties.of(Material.METAL, DyeColor.GRAY).harvestTool(ToolType.PICKAXE).strength(0.5f));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(ON, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context){
        TileEntity entity = world.getBlockEntity(pos);
        return entity instanceof LandmineTileEntity && !((LandmineTileEntity)entity).hasShape() ? BlockShape.empty().getUnderlying() : SHAPE.getUnderlying();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, IWorld world, BlockPos pos, BlockPos pos2){
        return state.canSurvive(world, pos) ? super.updateShape(state, direction, state2, world, pos, pos2) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
        BlockPos blockpos = pos.below();
        return canSupportCenter(world, blockpos, Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return this.type.getTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder){
        builder.add(ON, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity){
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            ((LandmineTileEntity)tileEntity).onEntityCollide(entity);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult){
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof LandmineTileEntity)
            return ((LandmineTileEntity)tileEntity).onRightClick(player, hand) ? ActionResultType.sidedSuccess(world.isClientSide) : ActionResultType.FAIL;
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_){
        if(!state.is(newState.getBlock())){
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof LandmineTileEntity)
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LandmineTileEntity)tileEntity).getStack());

            super.onRemove(state, world, pos, newState, p_196243_5_);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state){
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context){
        return BlockShape.empty().getUnderlying();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag flag){
        list.add(TextComponents.translation("landmines." + this.type.getSuffix() + ".info").color(TextFormatting.AQUA).get());
        if(this.type.itemFilter != null && this.type.tooltipItem != null)
            list.add(TextComponents.translation("landmines.info.item", TextComponents.item(this.type.tooltipItem).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
        list.add(TextComponents.translation("landmines.info.reusable", TextComponents.translation("landmines.info.reusable." + this.type.reusable.get()).color(TextFormatting.GOLD).get()).color(TextFormatting.GRAY).get());
    }
}
