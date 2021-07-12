package com.supermartijn642.structureblueprinter;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void setup(ModelRegistryEvent e){
        for(LandmineType type : LandmineType.values()){
            ModelLoader.setCustomModelResourceLocation(type.getItem(), 0, new ModelResourceLocation(type.getItem().getRegistryName(), "inventory"));
            ClientRegistry.bindTileEntitySpecialRenderer(type.getTileEntityClass(), new LandmineRenderer());
        }
    }

}
