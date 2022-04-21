package com.supermartijn642.landmines;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public enum LandmineType {

    EXPLOSIVE(LandminesConfig.explosionReusable, false, null, null, LandmineEffect.EXPLOSION, LandmineTileEntity.ExplosiveTileEntity.class),
    POTION(LandminesConfig.potionReusable, false, stack -> stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION, Items.POTIONITEM, LandmineEffect.POTION, LandmineTileEntity.PotionTileEntity.class),
    LAUNCH(LandminesConfig.launchReusable, true, null, null, LandmineEffect.LAUNCH, LandmineTileEntity.LaunchTileEntity.class),
    TELEPORT(LandminesConfig.teleportReusable, false, stack -> stack.getItem() == Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, LandmineEffect.TELEPORT, LandmineTileEntity.TeleportTileEntity.class),
    FIRE(LandminesConfig.fireReusable, false, stack -> stack.getItem() == Items.FIRE_CHARGE, Items.FIRE_CHARGE, LandmineEffect.FIRE, LandmineTileEntity.FireTileEntity.class),
    SNOW(LandminesConfig.snowReusable, false, stack -> stack.getItem() == Item.getItemFromBlock(Blocks.SNOW), Item.getItemFromBlock(Blocks.SNOW), LandmineEffect.SNOW, LandmineTileEntity.SnowTileEntity.class),
    ZOMBIE(LandminesConfig.zombieReusable, false, null, null, LandmineEffect.ZOMBIE, LandmineTileEntity.ZombieTileEntity.class),
    LEVITATION(LandminesConfig.levitationReusable, true, null, null, LandmineEffect.LEVITATION, LandmineTileEntity.LevitationTileEntity.class),
    LIGHTNING(LandminesConfig.lightningReusable, true, null, null, LandmineEffect.LIGHTNING, LandmineTileEntity.LightningTileEntity.class),
    ARROWS(LandminesConfig.arrowsReusable, false, stack -> stack.getItem() == Items.ARROW, Items.ARROW, LandmineEffect.ARROWS, LandmineTileEntity.ArrowsTileEntity.class),
    FAKE(LandminesConfig.fakeReusable, false, null, null, LandmineEffect.NOTHING, LandmineTileEntity.FakeTileEntity.class);

    private final Class<? extends LandmineTileEntity> tileEntityClass;
    private LandmineBlock block;
    private ItemBlock item;
    public final Supplier<Boolean> reusable;
    public final boolean instantTrigger;
    public final Predicate<ItemStack> itemFilter;
    public final Item tooltipItem;
    public final LandmineEffect effect;

    LandmineType(Supplier<Boolean> reusable, boolean instantTrigger, Predicate<ItemStack> itemFilter, Item tooltipItem, LandmineEffect effect, Class<? extends LandmineTileEntity> tileEntityClass){
        this.tileEntityClass = tileEntityClass;
        this.reusable = reusable;
        this.instantTrigger = instantTrigger;
        this.itemFilter = itemFilter;
        this.tooltipItem = tooltipItem;
        this.effect = effect;
    }

    public String getSuffix(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    public LandmineBlock getBlock(){
        return this.block;
    }

    public LandmineTileEntity getTileEntity(){
        try{
            return this.tileEntityClass.newInstance();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Class<? extends LandmineTileEntity> getTileEntityClass(){
        return this.tileEntityClass;
    }

    public ItemBlock getItem(){
        return this.item;
    }

    public void registerBlock(IForgeRegistry<Block> registry){
        if(this.block != null)
            throw new IllegalStateException("Blocks have already been registered!");

        this.block = new LandmineBlock(this);
        registry.register(this.block);
    }

    public void registerTileEntity(){
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering tile entity types!");

        GameRegistry.registerTileEntity(this.tileEntityClass, new ResourceLocation("landmines", this.getSuffix() + "_landmine_tile_entity"));
    }

    public void registerItem(IForgeRegistry<Item> registry){
        if(this.item != null)
            throw new IllegalStateException("Items have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering items!");

        this.item = new ItemBlock(this.block);
        this.item.setRegistryName(this.block.getRegistryName());
        registry.register(this.item);
    }
}
