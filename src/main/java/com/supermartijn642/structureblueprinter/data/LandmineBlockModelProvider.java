package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlockModelProvider extends BlockModelProvider {

    public LandmineBlockModelProvider(GatherDataEvent e){
        super(e.getGenerator(), "landmines", e.getExistingFileHelper());
    }

    @Override
    protected void registerModels(){
        for(LandmineType type : LandmineType.values())
            this.addLandmineTypeModels(type);
    }

    private void addLandmineTypeModels(LandmineType type){
        this.withExistingParent("block/types/" + type.getSuffix() + "_landmine_off", new ResourceLocation("landmines", "block/landmine"))
            .texture("type", new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_off"));
        this.withExistingParent("block/types/" + type.getSuffix() + "_landmine_on", new ResourceLocation("landmines", "block/landmine"))
            .texture("type", new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_on"));
    }
}
