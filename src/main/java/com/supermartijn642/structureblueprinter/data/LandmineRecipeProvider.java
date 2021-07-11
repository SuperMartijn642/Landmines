package com.supermartijn642.structureblueprinter.data;

import com.supermartijn642.structureblueprinter.LandmineType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class LandmineRecipeProvider extends RecipeProvider {

    public static final RecipeCreator EXPLOSION = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.EXPLOSIVE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.TNT)
            .unlocks("has_tnt", hasItems.apply(() -> Items.TNT))
            .save(recipeConsumer);
    };
    public static final RecipeCreator POTION = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.POTION::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/redstone")))
            .define('D', () -> Items.GLASS_BOTTLE)
            .unlocks("has_redstone", hasTag.apply(new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/redstone"))))
            .save(recipeConsumer);
    };
    public static final RecipeCreator LAUNCH = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.LAUNCH::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.SLIME_BLOCK)
            .define('D', new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/redstone")))
            .define('E', () -> Items.PISTON)
            .unlocks("has_redstone", hasTag.apply(new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/redstone"))))
            .save(recipeConsumer);
    };
    public static final RecipeCreator TELEPORT = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.TELEPORT::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', new ItemTags.Wrapper(new ResourceLocation("forge", "ender_pearls")))
            .unlocks("has_ender_pearl", hasTag.apply(new ItemTags.Wrapper(new ResourceLocation("forge", "ender_pearls"))))
            .save(recipeConsumer);
    };
    public static final RecipeCreator FIRE = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.FIRE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', new ItemTags.Wrapper(new ResourceLocation("forge", "netherrack")))
            .unlocks("has_netherrack", hasTag.apply(new ItemTags.Wrapper(new ResourceLocation("forge", "netherrack"))))
            .save(recipeConsumer);
    };
    public static final RecipeCreator SNOW = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.SNOW::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.PACKED_ICE)
            .unlocks("has_ice", hasItems.apply(() -> Items.ICE))
            .save(recipeConsumer);
    };
    public static final RecipeCreator ZOMBIE = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.ZOMBIE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("CCC")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.ROTTEN_FLESH)
            .unlocks("has_rotten_flesh", hasItems.apply(() -> Items.ROTTEN_FLESH))
            .save(recipeConsumer);
    };
    public static final RecipeCreator LEVITATION = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.LEVITATION::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.DISPENSER)
            .define('D', new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/redstone")))
            .define('E', () -> Items.SHULKER_SHELL)
            .unlocks("has_dispenser", hasItems.apply(() -> Items.SHULKER_SHELL))
            .save(recipeConsumer);
    };
    public static final RecipeCreator LIGHTNING = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.LIGHTNING::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" D ")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.END_ROD)
            .define('D', new ItemTags.Wrapper(new ResourceLocation("forge", "storage_blocks/iron")))
            .unlocks("has_end_rod", hasItems.apply(() -> Items.END_ROD))
            .save(recipeConsumer);
    };
    public static final RecipeCreator ARROWS = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.ARROWS::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DDD")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', () -> Items.DISPENSER)
            .define('D', () -> Items.ARROW)
            .unlocks("has_dispenser", hasItems.apply(() -> Items.DISPENSER))
            .save(recipeConsumer);
    };
    public static final RecipeCreator FAKE = (hasItems, hasTag, recipeConsumer) -> {
        ShapedRecipeBuilder.shaped(LandmineType.FAKE::getItem)
            .pattern(" A ")
            .pattern("BCB")
            .define('A', new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates")))
            .define('B', new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron")))
            .define('C', new ItemTags.Wrapper(new ResourceLocation("minecraft", "planks")))
            .unlocks("has_pressure_plate", hasTag.apply(new ItemTags.Wrapper(new ResourceLocation("landmines", "stone_pressure_plates"))))
            .save(recipeConsumer);
    };

    public LandmineRecipeProvider(GatherDataEvent e){
        super(e.getGenerator());
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> recipeConsumer){
        EXPLOSION.accept(this::has, this::has, recipeConsumer);
        POTION.accept(this::has, this::has, recipeConsumer);
        LAUNCH.accept(this::has, this::has, recipeConsumer);
        TELEPORT.accept(this::has, this::has, recipeConsumer);
        FIRE.accept(this::has, this::has, recipeConsumer);
        SNOW.accept(this::has, this::has, recipeConsumer);
        ZOMBIE.accept(this::has, this::has, recipeConsumer);
        LEVITATION.accept(this::has, this::has, recipeConsumer);
        LIGHTNING.accept(this::has, this::has, recipeConsumer);
        ARROWS.accept(this::has, this::has, recipeConsumer);
        FAKE.accept(this::has, this::has, recipeConsumer);
    }

    private interface RecipeCreator {

        void accept(Function<IItemProvider,InventoryChangeTrigger.Instance> hasItems, Function<Tag<Item>,InventoryChangeTrigger.Instance> hasTag, Consumer<IFinishedRecipe> recipeConsumer);
    }
}
