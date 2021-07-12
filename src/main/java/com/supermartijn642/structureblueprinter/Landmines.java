package com.supermartijn642.structureblueprinter;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = Landmines.MODID, name = Landmines.NAME, version = Landmines.VERSION, dependencies = Landmines.DEPENDENCIES)
public class Landmines {

    public static final String MODID = "landmines";
    public static final String NAME = "Landmines";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "required-after:supermartijn642corelib@[1.0.9,);required-after:supermartijn642configlib@[1.0.8,)";

    public static final CreativeTabs GROUP = new CreativeTabs("landmines") {
        @Override
        public ItemStack getTabIconItem(){
            return new ItemStack(LandmineType.EXPLOSIVE.getItem());
        }
    };

    @GameRegistry.ObjectHolder("landmines:trigger_sound")
    public static SoundEvent trigger_sound;

    public Landmines(){
    }

    @Mod.EventBusSubscriber
    public static class ModEvents {

        @SubscribeEvent
        public static void onBlockRegistry(RegistryEvent.Register<Block> e){
            for(LandmineType type : LandmineType.values()){
                type.registerBlock(e.getRegistry());
                type.registerTileEntity();
            }
        }

        @SubscribeEvent
        public static void onItemRegistry(RegistryEvent.Register<Item> e){
            for(LandmineType type : LandmineType.values())
                type.registerItem(e.getRegistry());
        }

        @SubscribeEvent
        public static void onSoundRegistry(RegistryEvent.Register<SoundEvent> e){
            e.getRegistry().register(new SoundEvent(new ResourceLocation("landmines", "trigger_sound")).setRegistryName("trigger_sound"));
        }
    }

}
