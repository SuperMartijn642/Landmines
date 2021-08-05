package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineItemModelProvider extends ItemModelProvider {

    public LandmineItemModelProvider(GatherDataEvent e){
        super(e.getGenerator(), "landmines", e.getExistingFileHelper());
    }

    @Override
    protected void registerModels(){
        for(LandmineType type : LandmineType.values())
            this.addLandmineTypeModel(type);
    }

    private void addLandmineTypeModel(LandmineType type){
        this.withExistingParent("item/" + type.getSuffix() + "_landmine", new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_on"));
    }
}
