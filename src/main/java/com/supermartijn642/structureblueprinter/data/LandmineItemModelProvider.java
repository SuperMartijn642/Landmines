package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import java.util.function.BiFunction;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineItemModelProvider {

    private final BiFunction<String,String,BlockModelBuilder> stringToBuilder;
    private final BiFunction<String,ResourceLocation,BlockModelBuilder> locationToBuilder;

    public LandmineItemModelProvider(BiFunction<String,String,BlockModelBuilder> stringToBuilder, BiFunction<String,ResourceLocation,BlockModelBuilder> locationToBuilder){
        this.stringToBuilder = stringToBuilder;
        this.locationToBuilder = locationToBuilder;
    }

    protected void registerModels(){
        for(LandmineType type : LandmineType.values())
            this.addLandmineTypeModel(type);
    }

    private void addLandmineTypeModel(LandmineType type){
        this.withExistingParent("item/" + type.getSuffix() + "_landmine", new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_on"));
    }

    private BlockModelBuilder withExistingParent(String name, String parent){
        return this.stringToBuilder.apply(name, parent);
    }

    private BlockModelBuilder withExistingParent(String name, ResourceLocation parent){
        return this.locationToBuilder.apply(name, parent);
    }
}
