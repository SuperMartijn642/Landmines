package com.supermartijn642.landmines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements CustomBlockEntityRenderer<LandmineBlockEntity,LandmineRenderer.State> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public State createStateHolder(){
        return new State();
    }

    @Override
    public void updateState(State state, LandmineBlockEntity entity, UpdateContext context){
        BlockState visualState = entity.getRenderBlockState();
        if(entity.getState() != LandmineBlockEntity.LandmineState.UNARMED)
            visualState = visualState.setValue(LandmineBlock.ON, (entity.renderTransitionTicks / BLINK_TIME) % 2 == 0);
        state.visualState = visualState;
        state.tint = ClientUtils.getMinecraft().getBlockColors().getColor(visualState, entity.getLevel(), entity.getBlockPos());
        state.offset = getRenderOffset(entity, context.partialTicks());
    }

    @Override
    public void submit(SubmitNodeCollector output, State state, RenderContext context){
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(0, state.offset, 0);

        ModelFeatureRenderer.CrumblingOverlay breakingOverlay = context.breakingOverlay();
        output.submitBlockModel(
            poseStack,
            ItemBlockRenderTypes.getRenderType(state.visualState),
            ClientUtils.getBlockRenderer().getBlockModel(state.visualState),
            ARGB.red(state.tint), ARGB.green(state.tint), ARGB.red(state.tint),
            context.packedLight(),
            breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(),
            0
        );

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
        BlockState visualState;
        int tint;
        double offset;
    }
}
