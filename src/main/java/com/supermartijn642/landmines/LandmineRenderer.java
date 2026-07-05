package com.supermartijn642.landmines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements CustomBlockEntityRenderer<LandmineBlockEntity,LandmineRenderer.State> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    private static final Matrix4fc IDENTITY_MATRIX = new Matrix4f().identity();

    @Override
    public State createStateHolder(){
        return new State();
    }

    @Override
    public void updateState(State state, LandmineBlockEntity entity, UpdateContext context){
        if(!(entity.getLevel() instanceof ClientLevel level))
            return;
        BlockState visualState = entity.getRenderBlockState();
        if(entity.getState() != LandmineBlockEntity.LandmineState.UNARMED)
            visualState = visualState.setValue(LandmineBlock.ON, (entity.renderTransitionTicks / BLINK_TIME) % 2 == 0);
        BlockStateModel model = ClientUtils.getMinecraft().getModelManager().getBlockStateModelSet().get(visualState);
        BlockPos pos = entity.getBlockPos();
        long seed = visualState.getSeed(pos);
        RandomSource random = context.randomSource(seed);
        QuadEmitter emitter = state.blockRenderState.setupMesh(IDENTITY_MATRIX, model.hasMaterialFlag(level, pos, visualState, random, BakedQuad.FLAG_TRANSLUCENT));
        random.setSeed(seed);
        model.emitQuads(emitter, level, pos, visualState, random, _ -> false);
        IntList tintLayers = state.blockRenderState.tintLayers();
        for(BlockTintSource tintSource : ClientUtils.getMinecraft().getBlockColors().getTintSources(visualState))
            tintLayers.add(tintSource.colorInWorld(visualState, level, pos));
        state.offset = getRenderOffset(entity, context.partialTicks());
    }

    @Override
    public void submit(SubmitNodeCollector output, State state, RenderContext context){
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(0, state.offset, 0);

        ModelFeatureRenderer.CrumblingOverlay breakingOverlay = context.breakingOverlay();
        state.blockRenderState.submit(poseStack, output, context.packedLight(), breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(), 0);

        poseStack.popPose();
    }

    private static double getRenderOffset(LandmineBlockEntity entity, float partialTicks){
        double targetPosition = getTargetPosition(entity.getState());
        double fromPosition = getTargetPosition(entity.getLastState());
        return fromPosition + (targetPosition - fromPosition) * Math.min(1, (entity.renderTransitionTicks + partialTicks) / TRANSITION_TIME);
    }

    private static double getTargetPosition(LandmineBlockEntity.LandmineState state){
        return state == LandmineBlockEntity.LandmineState.UNARMED ? 0 :
            state == LandmineBlockEntity.LandmineState.ARMED ? -0.125 :
            state == LandmineBlockEntity.LandmineState.TRIGGERED ? 0 : 0;
    }

    public static class State {
        final BlockModelRenderState blockRenderState = new BlockModelRenderState();
        double offset;
    }
}
