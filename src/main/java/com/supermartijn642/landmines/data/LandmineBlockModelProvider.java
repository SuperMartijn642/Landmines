package com.supermartijn642.landmines.data;

import com.supermartijn642.landmines.LandmineType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import java.util.function.BiFunction;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlockModelProvider {

    private final BiFunction<String,String,BlockModelBuilder> stringToBuilder;
    private final BiFunction<String,ResourceLocation,BlockModelBuilder> locationToBuilder;

    public LandmineBlockModelProvider(BiFunction<String,String,BlockModelBuilder> stringToBuilder, BiFunction<String,ResourceLocation,BlockModelBuilder> locationToBuilder){
        this.stringToBuilder = stringToBuilder;
        this.locationToBuilder = locationToBuilder;
    }

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

    private BlockModelBuilder withExistingParent(String name, String parent){
        return this.stringToBuilder.apply(name, parent);
    }

    private BlockModelBuilder withExistingParent(String name, ResourceLocation parent){
        return this.locationToBuilder.apply(name, parent);
    }
}
