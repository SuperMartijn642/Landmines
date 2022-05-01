package com.supermartijn642.landmines.data;

import com.supermartijn642.landmines.LandmineType;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRecipeProvider extends RecipeProvider {

    public static final Consumer<Consumer<FinishedRecipe>> EXPLOSION = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.EXPLOSIVE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.TNT)
            .unlockedBy("has_tnt", has(() -> Items.TNT))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> POTION = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.POTION::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', ItemTags.createOptional(new ResourceLocation("forge", "dusts/redstone")))
            .define('D', () -> Items.GLASS_BOTTLE)
            .unlockedBy("has_redstone", has(ItemTags.createOptional(new ResourceLocation("forge", "dusts/redstone"))))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> LAUNCH = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.LAUNCH::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.SLIME_BLOCK)
            .define('D', ItemTags.createOptional(new ResourceLocation("forge", "dusts/redstone")))
            .define('E', () -> Items.PISTON)
            .unlockedBy("has_redstone", has(ItemTags.createOptional(new ResourceLocation("forge", "dusts/redstone"))))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> TELEPORT = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.TELEPORT::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', ItemTags.createOptional(new ResourceLocation("forge", "ender_pearls")))
            .unlockedBy("has_ender_pearl", has(ItemTags.createOptional(new ResourceLocation("forge", "ender_pearls"))))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> FIRE = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.FIRE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', ItemTags.createOptional(new ResourceLocation("forge", "netherrack")))
            .unlockedBy("has_netherrack", has(ItemTags.createOptional(new ResourceLocation("forge", "netherrack"))))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> SNOW = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.SNOW::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.PACKED_ICE)
            .unlockedBy("has_ice", has(() -> Items.ICE))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> ZOMBIE = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.ZOMBIE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("CCC")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.ROTTEN_FLESH)
            .unlockedBy("has_rotten_flesh", has(() -> Items.ROTTEN_FLESH))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> LEVITATION = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.LEVITATION::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.DISPENSER)
            .define('D', ItemTags.createOptional(new ResourceLocation("forge", "dusts/redstone")))
            .define('E', () -> Items.SHULKER_SHELL)
            .unlockedBy("has_dispenser", has(() -> Items.SHULKER_SHELL))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> LIGHTNING = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.LIGHTNING::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" D ")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.END_ROD)
            .define('D', ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/iron")))
            .unlockedBy("has_end_rod", has(() -> Items.END_ROD))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> ARROWS = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.ARROWS::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.DISPENSER)
            .define('D', () -> Items.ARROW)
            .unlockedBy("has_dispenser", has(() -> Items.DISPENSER))
            .save(recipeConsumer);
    };
    public static final Consumer<Consumer<FinishedRecipe>> FAKE = recipeConsumer -> {
        ShapedRecipeBuilder.shaped(LandmineType.FAKE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', ItemTags.createOptional(new ResourceLocation("forge", "ingots/iron")))
            .define('C', ItemTags.createOptional(new ResourceLocation("minecraft", "planks")))
            .unlockedBy("has_pressure_plate", has(ItemTags.createOptional(new ResourceLocation("landmines", "stone_pressure_plates"))))
            .save(recipeConsumer);
    };

    public LandmineRecipeProvider(GatherDataEvent e){
        super(e.getGenerator());
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer){
        EXPLOSION.accept(recipeConsumer);
        POTION.accept(recipeConsumer);
        LAUNCH.accept(recipeConsumer);
        TELEPORT.accept(recipeConsumer);
        FIRE.accept(recipeConsumer);
        SNOW.accept(recipeConsumer);
        ZOMBIE.accept(recipeConsumer);
        LEVITATION.accept(recipeConsumer);
        LIGHTNING.accept(recipeConsumer);
        ARROWS.accept(recipeConsumer);
        FAKE.accept(recipeConsumer);
    }
}
