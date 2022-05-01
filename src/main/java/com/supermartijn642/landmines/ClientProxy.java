package com.supermartijn642.landmines;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void setup(EntityRenderersEvent.RegisterRenderers e){
        for(LandmineType type : LandmineType.values())
            e.registerBlockEntityRenderer(type.getTileEntityType(), context -> new LandmineRenderer());
    }

}
