package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.landmines.LandmineType;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineTagGenerator extends TagGenerator {

    public LandmineTagGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        // Mineable tags
        for(LandmineType type : LandmineType.values())
            this.blockMineableWithPickaxe().add(type.getBlock());
    }
}
