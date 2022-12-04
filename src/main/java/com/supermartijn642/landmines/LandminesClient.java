package com.supermartijn642.landmines;

import com.supermartijn642.core.registry.ClientRegistrationHandler;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class LandminesClient {

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("landmines");
        // Register the renderers
        for(LandmineType type : LandmineType.values())
            handler.registerCustomBlockEntityRenderer(type::getBlockEntityType, LandmineRenderer::new);
    }
}
