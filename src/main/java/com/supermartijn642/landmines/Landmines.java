package com.supermartijn642.landmines;

import com.supermartijn642.landmines.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Objects;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("landmines")
public class Landmines {

    public static final CreativeModeTab GROUP = new CreativeModeTab("landmines") {
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(LandmineType.EXPLOSIVE.getItem());
        }
    };

    @ObjectHolder(value = "landmines:trigger_sound", registryName = "minecraft:sound_event")
    public static SoundEvent trigger_sound;

    public Landmines(){
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent e){
            if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS))
                onBlockRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
                onTileEntityRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                onItemRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS))
                onSoundRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onBlockRegistry(IForgeRegistry<Block> registry){
            for(LandmineType type : LandmineType.values())
                type.registerBlock(registry);
        }

        public static void onTileEntityRegistry(IForgeRegistry<BlockEntityType<?>> registry){
            for(LandmineType type : LandmineType.values())
                type.registerTileEntity(registry);
        }

        public static void onItemRegistry(IForgeRegistry<Item> registry){
            for(LandmineType type : LandmineType.values())
                type.registerItem(registry);
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent e){
            e.getGenerator().addProvider(e.includeClient(), new LandmineBlockModelProvider(e));
            e.getGenerator().addProvider(e.includeClient(), new LandmineItemModelProvider(e));
            e.getGenerator().addProvider(e.includeClient(), new LandmineBlockStateProvider(e));
            e.getGenerator().addProvider(e.includeClient(), new LandmineLanguageProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new LandmineLootTableProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new LandmineTagsProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new LandmineBlockTagsProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new LandmineRecipeProvider(e));
        }

        public static void onSoundRegistry(IForgeRegistry<SoundEvent> registry){
            registry.register("trigger_sound", new SoundEvent(new ResourceLocation("landmines", "trigger_sound")));
        }
    }

}
