package com.supermartijn642.landmines.data;

import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

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
