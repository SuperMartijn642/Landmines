package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

/**
 * Created 04/12/2022 by SuperMartijn642
 */
public class LandmineRecipeGenerator extends RecipeGenerator {

    public LandmineRecipeGenerator(ResourceCache cache){
        super("landmines", cache);
    }

    @Override
    public void generate(){
        // Pressure plates tag
        ITag<Item> pressurePlates = ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates"));

        // Explosion
        this.shaped(LandmineType.EXPLOSIVE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.TNT)
            .unlockedBy(Items.TNT);

        // Potion
        this.shaped(LandmineType.POTION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Tags.Items.DUSTS_REDSTONE)
            .input('D', Items.GLASS_BOTTLE)
            .unlockedBy(Tags.Items.DUSTS_REDSTONE);

        // Launch
        this.shaped(LandmineType.LAUNCH.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.SLIME_BLOCK)
            .input('D', Tags.Items.DUSTS_REDSTONE)
            .input('E', Items.PISTON)
            .unlockedBy(Tags.Items.DUSTS_REDSTONE);

        // Teleport
        this.shaped(LandmineType.TELEPORT.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Tags.Items.ENDER_PEARLS)
            .unlockedBy(Tags.Items.ENDER_PEARLS);

        // Fire
        this.shaped(LandmineType.FIRE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Tags.Items.NETHERRACK)
            .unlockedBy(Tags.Items.NETHERRACK);

        // Snow
        this.shaped(LandmineType.SNOW.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.PACKED_ICE)
            .unlockedBy(Items.ICE);

        // Zombie
        this.shaped(LandmineType.ZOMBIE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("CCC")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.ROTTEN_FLESH)
            .unlockedBy(Items.ROTTEN_FLESH);

        // Levitation
        this.shaped(LandmineType.LEVITATION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.DISPENSER)
            .input('D', Tags.Items.DUSTS_REDSTONE)
            .input('E', Items.SHULKER_SHELL)
            .unlockedBy(Items.SHULKER_SHELL);

        // Lightning
        this.shaped(LandmineType.LIGHTNING.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" D ")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.END_ROD)
            .input('D', Tags.Items.STORAGE_BLOCKS_IRON)
            .unlockedBy(Items.END_ROD);

        // Arrows
        this.shaped(LandmineType.ARROWS.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Items.DISPENSER)
            .input('D', Items.ARROW)
            .unlockedBy(Items.DISPENSER);

        // Fake
        this.shaped(LandmineType.FAKE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', ItemTags.PLANKS)
            .unlockedBy(pressurePlates);
    }
}
