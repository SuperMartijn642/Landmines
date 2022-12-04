package com.supermartijn642.landmines;

import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.item.BaseBlockItem;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.registry.RegistrationHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public enum LandmineType {

    EXPLOSIVE(LandminesConfig.explosionReusable, false, null, null, LandmineEffect.EXPLOSION, "Explosive", "Explodes when triggered."),
    POTION(LandminesConfig.potionReusable, false, stack -> stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION, Items.POTIONITEM, LandmineEffect.POTION, "Potion", "Throws a potion when triggered."),
    LAUNCH(LandminesConfig.launchReusable, true, null, null, LandmineEffect.LAUNCH, "Launch", "Launches players and mobs into the air when triggered."),
    TELEPORT(LandminesConfig.teleportReusable, false, stack -> stack.getItem() == Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, LandmineEffect.TELEPORT, "Teleport", "Teleports players and mobs when triggered."),
    FIRE(LandminesConfig.fireReusable, false, stack -> stack.getItem() == Items.FIRE_CHARGE, Items.FIRE_CHARGE, LandmineEffect.FIRE, "Fire", "Sets players and mobs on fire when triggered."),
    SNOW(LandminesConfig.snowReusable, false, stack -> stack.getItem() == Item.getItemFromBlock(Blocks.SNOW), Item.getItemFromBlock(Blocks.SNOW), LandmineEffect.SNOW, "Snow", "Spawns snow when triggered."),
    ZOMBIE(LandminesConfig.zombieReusable, false, null, null, LandmineEffect.ZOMBIE, "Zombie", "Spawns zombies when triggered."),
    LEVITATION(LandminesConfig.levitationReusable, true, null, null, LandmineEffect.LEVITATION, "Levitation", "Gives players and mobs levitation when triggered."),
    LIGHTNING(LandminesConfig.lightningReusable, true, null, null, LandmineEffect.LIGHTNING, "Lightning", "Strikes lightning when triggered."),
    ARROWS(LandminesConfig.arrowsReusable, false, stack -> stack.getItem() == Items.ARROW, Items.ARROW, LandmineEffect.ARROWS, "Arrow", "Shoots out arrows when triggered."),
    FAKE(LandminesConfig.fakeReusable, false, null, null, LandmineEffect.NOTHING, "Fake", "A fake landmine disguised as an Explosive Landmine.");

    private BaseBlockEntityType<LandmineBlockEntity> blockEntityType;
    private LandmineBlock block;
    private BaseBlockItem item;
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

    public LandmineBlockEntity getBlockEntity(){
        return new LandmineBlockEntity(this);
    }

    public BaseBlockEntityType<LandmineBlockEntity> getBlockEntityType(){
        return this.blockEntityType;
    }

    public BaseBlockItem getItem(){
        return this.item;
    }

    public void registerBlock(RegistrationHandler.Helper<Block> helper){
        if(this.block != null)
            throw new IllegalStateException("Blocks have already been registered!");

        this.block = new LandmineBlock(this);
        helper.register(this.getSuffix() + "_landmine", this.block);
    }

    public void registerBlockEntity(RegistrationHandler.Helper<BaseBlockEntityType<?>> helper){
        if(this.blockEntityType != null)
            throw new IllegalStateException("Block entities have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering block entity types!");

        this.blockEntityType = BaseBlockEntityType.create(this::getBlockEntity, this.block);
        helper.register(this.getSuffix() + "_landmine_tile_entity", this.blockEntityType);
    }

    public void registerItem(RegistrationHandler.Helper<Item> helper){
        if(this.item != null)
            throw new IllegalStateException("Items have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering items!");

        this.item = new BaseBlockItem(this.block, ItemProperties.create().group(Landmines.GROUP));
        helper.register(this.getSuffix() + "_landmine", this.item);
    }
}
