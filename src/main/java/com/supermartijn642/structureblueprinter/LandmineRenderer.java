package com.supermartijn642.structureblueprinter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements BlockEntityRenderer<LandmineTileEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay){
        matrixStack.pushPose();
        matrixStack.translate(0, getRenderOffset(tileEntity, partialTicks), 0);

        BlockState state = tileEntity.getRenderBlockState();
        if(tileEntity.getState() != LandmineTileEntity.LandmineState.UNARMED)
            state = state.setValue(LandmineBlock.ON, (tileEntity.renderTransitionTicks / BLINK_TIME) % 2 == 0);
        BakedModel model = ClientUtils.getBlockRenderer().getBlockModel(state);
        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), renderTypeBuffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)), state, model, 0, 0, 0, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        matrixStack.popPose();
    }

    private static double getRenderOffset(LandmineTileEntity tileEntity, float partialTicks){
        double targetPosition = getTargetPosition(tileEntity.getState());
        double fromPosition = getTargetPosition(tileEntity.getLastState());
        return fromPosition + (targetPosition - fromPosition) * Math.min(1, (tileEntity.renderTransitionTicks + partialTicks) / TRANSITION_TIME);
    }

    private static double getTargetPosition(LandmineTileEntity.LandmineState state){
        return state == LandmineTileEntity.LandmineState.UNARMED ? 0 :
            state == LandmineTileEntity.LandmineState.ARMED ? -0.125 :
                state == LandmineTileEntity.LandmineState.TRIGGERED ? 0 : 0;
    }
}
