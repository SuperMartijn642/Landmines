package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

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
        for(LandmineType type : LandmineType.values())
            this.add(type.getBlock(), type.englishTranslation + " Landmine");
    }
}
