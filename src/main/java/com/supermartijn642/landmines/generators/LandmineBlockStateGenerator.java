package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.BlockStateGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineBlock;
import com.supermartijn642.landmines.LandmineType;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineBlockStateGenerator extends BlockStateGenerator {

    public LandmineBlockStateGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        for(LandmineType type : LandmineType.values()){
            this.blockState(type.getBlock()).variantsForAll(
                (state, variant) -> variant.model("block/types/" + type.getSuffix() + (state.get(LandmineBlock.ON) ? "_landmine_on" : "_landmine_off"))
            );
        }
    }
}
