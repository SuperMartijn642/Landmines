package com.supermartijn642.landmines.data;

import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineTagsProvider extends ItemTagsProvider {

    public LandmineTagsProvider(GatherDataEvent e){
        super(e.getGenerator());
    }

    @Override
    protected void addTags(){
        this.tag(new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates"))).add(Items.STONE_PRESSURE_PLATE);
    }
}
