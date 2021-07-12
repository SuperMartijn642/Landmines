package com.supermartijn642.structureblueprinter;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.ScreenUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer extends TileEntitySpecialRenderer<LandmineTileEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(x - tileEntity.getPos().getX(), y - tileEntity.getPos().getY() + getRenderOffset(tileEntity, partialTicks), z - tileEntity.getPos().getZ());

        IBlockState state = tileEntity.getRenderBlockState();
        if(tileEntity.getState() != LandmineTileEntity.LandmineState.UNARMED)
            state = state.withProperty(LandmineBlock.ON, (tileEntity.renderTransitionTicks / BLINK_TIME) % 2 == 0);

        GlStateManager.disableLighting();

        ScreenUtils.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        IBakedModel model = ClientUtils.getBlockRenderer().getModelForState(state);
        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModel(tileEntity.getWorld(), model, state, tileEntity.getPos(), buffer, true);

        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
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
