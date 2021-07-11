package com.supermartijn642.structureblueprinter;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public enum LandmineType {

    EXPLOSIVE(LandminesConfig.explosionReusable, false, null, LandmineEffect.EXPLOSION, "Explosive"),
    POTION(LandminesConfig.potionReusable, false, stack -> stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION, LandmineEffect.POTION, "Potion"),
    LAUNCH(LandminesConfig.launchReusable, true, null, LandmineEffect.LAUNCH, "Launch"),
    TELEPORT(LandminesConfig.teleportReusable, false, stack -> stack.getItem() == Items.CHORUS_FRUIT, LandmineEffect.TELEPORT, "Teleport"),
    FIRE(LandminesConfig.fireReusable, false, stack -> stack.getItem() == Items.FIRE_CHARGE, LandmineEffect.FIRE, "Fire"),
    SNOW(LandminesConfig.snowReusable, false, stack -> stack.getItem() == Items.SNOW_BLOCK, LandmineEffect.SNOW, "Snow"),
    ZOMBIE(LandminesConfig.zombieReusable, false, null, LandmineEffect.ZOMBIE, "Zombie"),
    LEVITATION(LandminesConfig.levitationReusable, true, null, LandmineEffect.LEVITATION, "Levitation"),
    LIGHTNING(LandminesConfig.lightningReusable, true, null, LandmineEffect.LIGHTNING, "Lightning"),
    ARROWS(LandminesConfig.arrowsReusable, false, stack -> stack.getItem() == Items.ARROW, LandmineEffect.ARROWS, "Arrow"),
    FAKE(LandminesConfig.fakeReusable, false, null, LandmineEffect.NOTHING, "Fake");

    private TileEntityType<LandmineTileEntity> tileEntityType;
    private LandmineBlock block;
    private BlockItem item;
    public final Supplier<Boolean> reusable;
    public final boolean instantTrigger;
    public final Predicate<ItemStack> itemFilter;
    public final LandmineEffect effect;
    public final String englishTranslation;

    LandmineType(Supplier<Boolean> reusable, boolean instantTrigger, Predicate<ItemStack> itemFilter, LandmineEffect effect, String englishTranslation){
        this.reusable = reusable;
        this.instantTrigger = instantTrigger;
        this.itemFilter = itemFilter;
        this.effect = effect;
        this.englishTranslation = englishTranslation;
    }

    public String getSuffix(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    public LandmineBlock getBlock(){
        return this.block;
    }

    public LandmineTileEntity getTileEntity(){
        return new LandmineTileEntity(this);
    }

    public TileEntityType<LandmineTileEntity> getTileEntityType(){
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

    public void registerTileEntity(IForgeRegistry<TileEntityType<?>> registry){
        if(this.tileEntityType != null)
            throw new IllegalStateException("Tile entities have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering tile entity types!");

        this.tileEntityType = TileEntityType.Builder.of(this::getTileEntity, this.block).build(null);
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
