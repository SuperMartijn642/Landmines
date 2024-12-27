package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.ItemInfoGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;

/**
 * Created 26/12/2024 by SuperMartijn642
 */
public class LandmineItemInfoGenerator extends ItemInfoGenerator {

    public LandmineItemInfoGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        for(LandmineType type : LandmineType.values())
            this.simpleInfo(type.getItem(), "block/types/" + type.getSuffix() + "_landmine_on");
    }
}
