package com.supermartijn642.structureblueprinter;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.ScreenUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRenderer extends TileEntityRenderer<LandmineTileEntity> {

    private static final int TRANSITION_TIME = 10;
    private static final int BLINK_TIME = 8;

    @Override
    public void render(LandmineTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.offset(x - tileEntity.getBlockPos().getX(), y - tileEntity.getBlockPos().getY() + getRenderOffset(tileEntity, partialTicks), z - tileEntity.getBlockPos().getZ());

        BlockState state = tileEntity.getRenderBlockState();
        if(tileEntity.getState() != LandmineTileEntity.LandmineState.UNARMED)
            state = state.setValue(LandmineBlock.ON, (tileEntity.renderTransitionTicks / BLINK_TIME) % 2 == 0);

        GlStateManager.disableLighting();

        ScreenUtils.bindTexture(AtlasTexture.LOCATION_BLOCKS);
        IBakedModel model = ClientUtils.getBlockRenderer().getBlockModel(state);
        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(tileEntity.getLevel(), model, state, tileEntity.getBlockPos(), buffer, true, new Random(), 42L, EmptyModelData.INSTANCE);

        buffer.offset(0, 0, 0);
        tessellator.end();
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
