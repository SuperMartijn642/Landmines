package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.LootTableGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineLootTableGenerator extends LootTableGenerator {

    public LandmineLootTableGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        // Make sure blocks drop their item when mined
        for(LandmineType type : LandmineType.values())
            this.dropSelf(type.getBlock());
    }
}
