package com.supermartijn642.structureblueprinter.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineTagsProvider extends ItemTagsProvider {

    public LandmineTagsProvider(GatherDataEvent e){
        super(e.getGenerator(), new BlockTagsProvider(e.getGenerator()), "landmines", e.getExistingFileHelper());
    }

    @Override
    protected void addTags(){
        this.tag(ItemTags.bind("landmines:stone_pressure_plates")).add(Items.STONE_PRESSURE_PLATE, Items.POLISHED_BLACKSTONE_PRESSURE_PLATE);
    }
}
