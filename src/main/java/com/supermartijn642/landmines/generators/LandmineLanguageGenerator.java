package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;
import com.supermartijn642.landmines.Landmines;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineLanguageGenerator extends LanguageGenerator {

    public LandmineLanguageGenerator(ResourceCache cache){
        super("landmines", cache, "en_us");
    }

    @Override
    public void generate(){
        this.itemGroup(Landmines.GROUP, "Landmines");
        this.translation("landmines.trigger_sound", "Landmine");
        this.translation("landmines.require_item", "%1$s requires a %2$s to be armed!");
        this.translation("landmines.info.item", "Required item: %s");
        this.translation("landmines.info.reusable", "Reusable: %s");
        this.translation("landmines.info.reusable.true", "True");
        this.translation("landmines.info.reusable.false", "False");
        for(LandmineType type : LandmineType.values()){
            this.block(type.getBlock(), type.englishTranslation + " Landmine");
            this.translation("landmines." + type.getSuffix() + ".info", type.englishDescription);
        }
    }
}
