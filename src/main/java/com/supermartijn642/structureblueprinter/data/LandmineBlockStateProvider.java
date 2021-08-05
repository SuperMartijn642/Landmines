package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineBlock;
import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class LandmineBlockStateProvider extends BlockStateProvider {

    public LandmineBlockStateProvider(GatherDataEvent e){
        super(e.getGenerator(), "landmines", e.getExistingFileHelper());
    }

    @Override
    protected void registerStatesAndModels(){
        for(LandmineType type : LandmineType.values())
            this.addLandmineTypeBlockStates(type);
    }

    private void addLandmineTypeBlockStates(LandmineType type){
        this.getVariantBuilder(type.getBlock()).forAllStatesExcept(
            state -> new ConfiguredModel[]{
                state.getValue(LandmineBlock.ON) ?
                    new ConfiguredModel(this.models().getExistingFile(new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_on"))) :
                    new ConfiguredModel(this.models().getExistingFile(new ResourceLocation("landmines", "block/types/" + type.getSuffix() + "_landmine_off")))
            },
            BlockStateProperties.WATERLOGGED
        );
    }
}
