package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineRecipeGenerator extends RecipeGenerator {

    public LandmineRecipeGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        // Explosion
        this.shaped(LandmineType.EXPLOSIVE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.TNT))
            .unlockedBy(Item.getItemFromBlock(Blocks.TNT));

        // Potion
        this.shaped(LandmineType.POTION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', "dustRedstone")
            .input('D', Items.GLASS_BOTTLE)
            .unlockedByOreDict("dustRedstone");

        // Launch
        this.shaped(LandmineType.LAUNCH.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.SLIME_BLOCK))
            .input('D', "dustRedstone")
            .input('E', Item.getItemFromBlock(Blocks.PISTON))
            .unlockedByOreDict("dustRedstone");

        // Teleport
        this.shaped(LandmineType.TELEPORT.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', "enderpearl")
            .unlockedByOreDict("enderpearl");

        // Fire
        this.shaped(LandmineType.FIRE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', "netherrack")
            .unlockedByOreDict("netherrack");

        // Snow
        this.shaped(LandmineType.SNOW.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.PACKED_ICE))
            .unlockedBy(Item.getItemFromBlock(Blocks.ICE));

        // Zombie
        this.shaped(LandmineType.ZOMBIE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("CCC")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Items.ROTTEN_FLESH)
            .unlockedBy(Items.ROTTEN_FLESH);

        // Levitation
        this.shaped(LandmineType.LEVITATION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.DISPENSER))
            .input('D', "dustRedstone")
            .input('E', Items.SHULKER_SHELL)
            .unlockedBy(Items.SHULKER_SHELL);

        // Lightning
        this.shaped(LandmineType.LIGHTNING.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" D ")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.END_ROD))
            .input('D', "blockIron")
            .unlockedBy(Item.getItemFromBlock(Blocks.END_ROD));

        // Arrows
        this.shaped(LandmineType.ARROWS.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', Item.getItemFromBlock(Blocks.DISPENSER))
            .input('D', Items.ARROW)
            .unlockedBy(Item.getItemFromBlock(Blocks.DISPENSER));

        // Fake
        this.shaped(LandmineType.FAKE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE))
            .input('B', "ingotIron")
            .input('C', "plankWood")
            .unlockedBy(Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE));
    }
}
