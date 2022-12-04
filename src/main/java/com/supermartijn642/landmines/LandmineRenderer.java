package com.supermartijn642.landmines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements CustomBlockEntityRenderer<LandmineBlockEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        poseStack.pushPose();
        poseStack.translate(0, getRenderOffset(entity, partialTicks), 0);

        BlockState state = entity.getRenderBlockState();
        if(entity.getState() != LandmineBlockEntity.LandmineState.UNARMED)
            state = state.setValue(LandmineBlock.ON, (entity.renderTransitionTicks / BLINK_TIME) % 2 == 0);
        BakedModel model = ClientUtils.getBlockRenderer().getBlockModel(state);
        for(RenderType renderType : model.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY))
            ClientUtils.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(renderType), state, model, 0, 0, 0, combinedLight, combinedOverlay, ModelData.EMPTY, renderType);

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
}
