package com.supermartijn642.landmines;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements CustomBlockEntityRenderer<LandmineBlockEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineBlockEntity entity, float partialTicks, int destroyStage){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.offset(-entity.getBlockPos().getX(), -entity.getBlockPos().getY() + getRenderOffset(entity, partialTicks), -entity.getBlockPos().getZ());

        BlockState state = entity.getRenderBlockState();
        if(entity.getState() != LandmineBlockEntity.LandmineState.UNARMED)
            state = state.setValue(LandmineBlock.ON, (entity.renderTransitionTicks / BLINK_TIME) % 2 == 0);

        GlStateManager.disableLighting();

        ScreenUtils.bindTexture(AtlasTexture.LOCATION_BLOCKS);
        IBakedModel model = ClientUtils.getBlockRenderer().getBlockModel(state);
        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(entity.getLevel(), model, state, entity.getBlockPos(), buffer, true, new Random(), 42L, EmptyModelData.INSTANCE);

        buffer.offset(0, 0, 0);
        tessellator.end();
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
