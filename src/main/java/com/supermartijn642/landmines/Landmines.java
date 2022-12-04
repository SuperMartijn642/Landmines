package com.supermartijn642.landmines;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.landmines.generators.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = "@mod_id@", name = "@mod_name@", version = "@mod_version@", dependencies = "required-after:forge@@forge_dependency@;required-after:supermartijn642corelib@@core_library_dependency@;required-after:supermartijn642configlib@@config_library_dependency@")
public class Landmines {

    public static final CreativeItemGroup GROUP = CreativeItemGroup.create("landmines", LandmineType.EXPLOSIVE::getItem);

    @RegistryEntryAcceptor(namespace = "landmines", identifier = "trigger_sound", registry = RegistryEntryAcceptor.Registry.SOUND_EVENTS)
    public static SoundEvent trigger_sound;

    public Landmines(){
        register();
        if(CommonUtils.getEnvironmentSide().isClient())
            LandminesClient.register();
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("landmines");
        // Blocks, block entity types, and items
        for(LandmineType type : LandmineType.values()){
            handler.registerBlockCallback(type::registerBlock);
            handler.registerBlockEntityTypeCallback(type::registerBlockEntity);
            handler.registerItemCallback(type::registerItem);
        }
        // Trigger sound
        handler.registerSoundEvent("trigger_sound", () -> new SoundEvent(new ResourceLocation("landmines", "trigger_sound")));
    }

    private static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("landmines");
        handler.addGenerator(LandmineModelGenerator::new);
        handler.addGenerator(LandmineBlockStateGenerator::new);
        handler.addGenerator(LandmineLanguageGenerator::new);
        handler.addGenerator(LandmineLootTableGenerator::new);
        handler.addGenerator(LandmineRecipeGenerator::new);
        handler.addGenerator(LandmineTagGenerator::new);
    }
}
