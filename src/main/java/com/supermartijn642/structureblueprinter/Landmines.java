package com.supermartijn642.structureblueprinter;

import com.supermartijn642.structureblueprinter.data.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("landmines")
public class Landmines {

    public static final ItemGroup GROUP = new ItemGroup("landmines") {
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(LandmineType.EXPLOSIVE.getItem());
        }
    };

    @ObjectHolder("landmines:landmine_tile_entity")
    public static TileEntityType<LandmineTileEntity> landmine_tile_entity;

    @ObjectHolder("landmines:trigger_sound")
    public static SoundEvent trigger_sound;

    public Landmines(){
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onBlockRegistry(RegistryEvent.Register<Block> e){
            for(LandmineType type : LandmineType.values())
                type.registerBlock(e.getRegistry());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> e){
            for(LandmineType type : LandmineType.values())
                type.registerTileEntity(e.getRegistry());
        }

        @SubscribeEvent
        public static void onItemRegistry(RegistryEvent.Register<Item> e){
            for(LandmineType type : LandmineType.values())
                type.registerItem(e.getRegistry());
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent e){
            e.getGenerator().addProvider(new LandmineBlockStateProvider(e));
            e.getGenerator().addProvider(new LandmineLanguageProvider(e));
            e.getGenerator().addProvider(new LandmineLootTableProvider(e));
            e.getGenerator().addProvider(new LandmineTagsProvider(e));
            e.getGenerator().addProvider(new LandmineRecipeProvider(e));
        }

        @SubscribeEvent
        public static void onSoundRegistry(RegistryEvent.Register<SoundEvent> e) {
            e.getRegistry().register(new SoundEvent(new ResourceLocation("landmines", "trigger_sound")).setRegistryName("trigger_sound"));
        }
    }

}
