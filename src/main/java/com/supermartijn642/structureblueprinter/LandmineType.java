package com.supermartijn642.structureblueprinter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public enum LandmineType {

    EXPLOSIVE(LandminesConfig.explosionReusable, false, null, null, LandmineEffect.EXPLOSION, "Explosive", "Explodes when triggered."),
    POTION(LandminesConfig.potionReusable, false, stack -> stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION, Items.POTION, LandmineEffect.POTION, "Potion", "Throws a potion when triggered."),
    LAUNCH(LandminesConfig.launchReusable, true, null, null, LandmineEffect.LAUNCH, "Launch", "Launches players and mobs into the air when triggered."),
    TELEPORT(LandminesConfig.teleportReusable, false, stack -> stack.getItem() == Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, LandmineEffect.TELEPORT, "Teleport", "Teleports players and mobs when triggered."),
    FIRE(LandminesConfig.fireReusable, false, stack -> stack.getItem() == Items.FIRE_CHARGE, Items.FIRE_CHARGE, LandmineEffect.FIRE, "Fire", "Sets players and mobs on fire when triggered."),
    SNOW(LandminesConfig.snowReusable, false, stack -> stack.getItem() == Items.SNOW_BLOCK, Items.SNOW_BLOCK, LandmineEffect.SNOW, "Snow", "Spawns snow when triggered."),
    ZOMBIE(LandminesConfig.zombieReusable, false, null, null, LandmineEffect.ZOMBIE, "Zombie", "Spawns zombies when triggered."),
    LEVITATION(LandminesConfig.levitationReusable, true, null, null, LandmineEffect.LEVITATION, "Levitation", "Gives players and mobs levitation when triggered."),
    LIGHTNING(LandminesConfig.lightningReusable, true, null, null, LandmineEffect.LIGHTNING, "Lightning", "Strikes lightning when triggered."),
    ARROWS(LandminesConfig.arrowsReusable, false, stack -> stack.getItem() == Items.ARROW, Items.ARROW, LandmineEffect.ARROWS, "Arrow", "Shoots out arrows when triggered."),
    FAKE(LandminesConfig.fakeReusable, false, null, null, LandmineEffect.NOTHING, "Fake", "A fake landmine disguised as an Explosive Landmine.");

    private BlockEntityType<LandmineTileEntity> tileEntityType;
    private LandmineBlock block;
    private BlockItem item;
    public final Supplier<Boolean> reusable;
    public final boolean instantTrigger;
    public final Predicate<ItemStack> itemFilter;
    public final Item tooltipItem;
    public final LandmineEffect effect;
    public final String englishTranslation;
    public final String englishDescription;

    LandmineType(Supplier<Boolean> reusable, boolean instantTrigger, Predicate<ItemStack> itemFilter, Item tooltipItem, LandmineEffect effect, String englishTranslation, String englishDescription){
        this.reusable = reusable;
        this.instantTrigger = instantTrigger;
        this.itemFilter = itemFilter;
        this.tooltipItem = tooltipItem;
        this.effect = effect;
        this.englishTranslation = englishTranslation;
        this.englishDescription = englishDescription;
    }

    public String getSuffix(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    public LandmineBlock getBlock(){
        return this.block;
    }

    public LandmineTileEntity getTileEntity(BlockPos pos, BlockState state){
        return new LandmineTileEntity(this, pos, state);
    }

    public BlockEntityType<LandmineTileEntity> getTileEntityType(){
        return this.tileEntityType;
    }

    public BlockItem getItem(){
        return this.item;
    }

    public void registerBlock(IForgeRegistry<Block> registry){
        if(this.block != null)
            throw new IllegalStateException("Blocks have already been registered!");

        this.block = new LandmineBlock(this);
        registry.register(this.block);
    }

    public void registerTileEntity(IForgeRegistry<BlockEntityType<?>> registry){
        if(this.tileEntityType != null)
            throw new IllegalStateException("Tile entities have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering tile entity types!");

        this.tileEntityType = BlockEntityType.Builder.of(this::getTileEntity, this.block).build(null);
        this.tileEntityType.setRegistryName(this.getSuffix() + "_landmine_tile_entity");
        registry.register(this.tileEntityType);
    }

    public void registerItem(IForgeRegistry<Item> registry){
        if(this.item != null)
            throw new IllegalStateException("Items have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering items!");

        this.item = new BlockItem(this.block, new Item.Properties().tab(Landmines.GROUP));
        this.item.setRegistryName(this.block.getRegistryName());
        registry.register(this.item);
    }
}
