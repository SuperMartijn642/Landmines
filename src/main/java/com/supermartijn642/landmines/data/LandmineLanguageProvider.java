package com.supermartijn642.landmines.data;

import com.supermartijn642.landmines.LandmineType;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineLanguageProvider extends LanguageProvider {

    public LandmineLanguageProvider(GatherDataEvent e){
        super(e.getGenerator(), "landmines", "en_us");
    }

    @Override
    protected void addTranslations(){
        this.add("itemGroup.landmines", "Landmines");
        this.add("landmines.trigger_sound", "Landmine");
        this.add("landmines.require_item", "%1$s requires a %2$s to be armed!");
        this.add("landmines.info.item", "Required item: %s");
        this.add("landmines.info.reusable", "Reusable: %s");
        this.add("landmines.info.reusable.true", "True");
        this.add("landmines.info.reusable.false", "False");
        for(LandmineType type : LandmineType.values()){
            this.add("landmines." + type.getSuffix() + ".info", type.englishDescription);
            this.add(type.getBlock(), type.englishTranslation + " Landmine");
        }
    }
}
