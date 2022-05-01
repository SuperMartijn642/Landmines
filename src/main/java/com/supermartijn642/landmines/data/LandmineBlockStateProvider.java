package com.supermartijn642.landmines.data;

import com.supermartijn642.landmines.LandmineBlock;
import com.supermartijn642.landmines.LandmineType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlockStateProvider extends BlockStateProvider {

    public LandmineBlockStateProvider(GatherDataEvent e){
        super(e.getGenerator(), "landmines", e.getExistingFileHelper());
    }

    @Override
    protected void registerStatesAndModels(){
        new LandmineBlockModelProvider(this.models()::withExistingParent, this.models()::withExistingParent).registerModels();
        new LandmineItemModelProvider(this.models()::withExistingParent, this.models()::withExistingParent).registerModels();
        for(LandmineType type : LandmineType.values())
            this.addLandmineTypeBlockStates(type);
    }

    private void addLandmineTypeBlockStates(LandmineType type){
        this.getVariantBuilder(type.getBlock()).forAllStatesExcept(
            state -> new ConfiguredModel[]{
                state.getValue(LandmineBlock.ON) ?
                    new ConfiguredModel(this.models().getExistingFile(new ResourceLocation("landmines","block/types/" + type.getSuffix() + "_landmine_on"))) :
                    new ConfiguredModel(this.models().getExistingFile(new ResourceLocation("landmines","block/types/" + type.getSuffix() + "_landmine_off")))
            },
            BlockStateProperties.WATERLOGGED
        );
    }
}
