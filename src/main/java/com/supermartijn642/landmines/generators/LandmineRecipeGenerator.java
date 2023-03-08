package com.supermartijn642.landmines.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.landmines.LandmineType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

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
        TagKey<Item> pressurePlates = TagKey.create(Registries.ITEM, new ResourceLocation("landmines", "stone_pressure_plates"));

        // Explosion
        this.shaped(LandmineType.EXPLOSIVE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.TNT)
            .unlockedBy(Items.TNT);

        // Potion
        this.shaped(LandmineType.POTION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', ConventionalItemTags.REDSTONE_DUSTS)
            .input('D', Items.GLASS_BOTTLE)
            .unlockedBy(ConventionalItemTags.REDSTONE_DUSTS);

        // Launch
        this.shaped(LandmineType.LAUNCH.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.SLIME_BLOCK)
            .input('D', ConventionalItemTags.REDSTONE_DUSTS)
            .input('E', Items.PISTON)
            .unlockedBy(ConventionalItemTags.REDSTONE_DUSTS);

        // Teleport
        this.shaped(LandmineType.TELEPORT.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.ENDER_PEARL)
            .unlockedBy(Items.ENDER_PEARL);

        // Fire
        this.shaped(LandmineType.FIRE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.NETHERRACK)
            .unlockedBy(Items.NETHERRACK);

        // Snow
        this.shaped(LandmineType.SNOW.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.PACKED_ICE)
            .unlockedBy(Items.ICE);

        // Zombie
        this.shaped(LandmineType.ZOMBIE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("CCC")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.ROTTEN_FLESH)
            .unlockedBy(Items.ROTTEN_FLESH);

        // Levitation
        this.shaped(LandmineType.LEVITATION.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.DISPENSER)
            .input('D', ConventionalItemTags.REDSTONE_DUSTS)
            .input('E', Items.SHULKER_SHELL)
            .unlockedBy(Items.SHULKER_SHELL);

        // Lightning
        this.shaped(LandmineType.LIGHTNING.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" D ")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.END_ROD)
            .input('D', Items.IRON_BLOCK)
            .unlockedBy(Items.END_ROD);

        // Arrows
        this.shaped(LandmineType.ARROWS.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.DISPENSER)
            .input('D', Items.ARROW)
            .unlockedBy(Items.DISPENSER);

        // Fake
        this.shaped(LandmineType.FAKE.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .input('A', pressurePlates)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', ItemTags.PLANKS)
            .unlockedBy(pressurePlates);
    }
}
