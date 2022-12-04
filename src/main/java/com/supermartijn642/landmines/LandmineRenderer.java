package com.supermartijn642.landmines;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer implements CustomBlockEntityRenderer<LandmineBlockEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineBlockEntity entity, float partialTicks, int destroyStage, float alpha){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-entity.getPos().getX(), -entity.getPos().getY() + getRenderOffset(entity, partialTicks), -entity.getPos().getZ());

        IBlockState state = entity.getRenderBlockState();
        if(entity.getState() != LandmineBlockEntity.LandmineState.UNARMED)
            state = state.withProperty(LandmineBlock.ON, (entity.renderTransitionTicks / BLINK_TIME) % 2 == 0);

        GlStateManager.disableLighting();

        ScreenUtils.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        IBakedModel model = ClientUtils.getBlockRenderer().getModelForState(state);
        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModel(entity.getWorld(), model, state, entity.getPos(), buffer, true);

        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
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
