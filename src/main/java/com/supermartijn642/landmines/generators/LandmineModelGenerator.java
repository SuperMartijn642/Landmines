package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineModelGenerator extends ModelGenerator {

    public LandmineModelGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        for(LandmineType type : LandmineType.values()){
            // Block models
            this.model("block/types/" + type.getSuffix() + "_landmine_off").parent("block/landmine").texture("type", "block/types/" + type.getSuffix() + "_landmine_off");
            this.model("block/types/" + type.getSuffix() + "_landmine_on").parent("block/landmine").texture("type", "block/types/" + type.getSuffix() + "_landmine_on");
            // Item model
            this.model("item/" + type.getSuffix() + "_landmine").parent("block/types/" + type.getSuffix() + "_landmine_on");
        }
    }
}
