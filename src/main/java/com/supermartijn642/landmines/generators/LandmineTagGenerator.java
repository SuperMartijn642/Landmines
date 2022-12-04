package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.landmines.LandmineType;
import net.minecraft.world.item.Items;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineTagGenerator extends TagGenerator {

    public LandmineTagGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        // Pressure plates
        this.itemTag("stone_pressure_plates").add(Items.STONE_PRESSURE_PLATE).add(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE);

        // Mineable tags
        for(LandmineType type : LandmineType.values())
            this.blockMineableWithPickaxe().add(type.getBlock());
    }
}
