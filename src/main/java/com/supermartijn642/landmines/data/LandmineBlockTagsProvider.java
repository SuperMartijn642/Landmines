package com.supermartijn642.landmines.data;

import com.supermartijn642.landmines.LandmineType;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Created 8/5/2021 by SuperMartijn642
 */
public class LandmineBlockTagsProvider extends BlockTagsProvider {

    public LandmineBlockTagsProvider(GatherDataEvent e){
        super(e.getGenerator(), "additionallanterns", e.getExistingFileHelper());
    }

    @Override
    protected void addTags(){
        TagAppender<Block> pickaxeTag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);
        for(LandmineType type : LandmineType.values())
            pickaxeTag.add(type.getBlock());
    }
}
