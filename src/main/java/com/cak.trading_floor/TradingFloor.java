package com.cak.trading_floor;

import com.cak.trading_floor.forge.TradingFloorData;
import com.cak.trading_floor.forge.content.depot.TradingDepotBlockEntity;
import com.cak.trading_floor.forge.network.TFPackets;
import com.cak.trading_floor.foundation.advancement.TFAdvancements;
import com.cak.trading_floor.foundation.forge.TFPlatformPacketsImpl;
import com.cak.trading_floor.foundation.forge.TFPlatformPredicatesImpl;
import com.cak.trading_floor.foundation.ponder_scenes.TFPonderPlugin;
import com.cak.trading_floor.registry.TFDisplaySources;
import com.cak.trading_floor.registry.TFLangEntries;
import com.cak.trading_floor.registry.TFParticleEmitters;
import com.cak.trading_floor.registry.TFRegistry;
import com.cak.trading_floor.registry.forge.TFPlatformRegistryImpl;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TradingFloor.MOD_ID)
public class TradingFloor {

    public static final String MOD_ID = "trading_floor";
    public static final String NAME = "Create: Trading Floor";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static String PLATFORM = "Unknown";

    public TradingFloor() {
        init();
    }

    public static void init() {

        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = NeoForge.EVENT_BUS;

        TFPlatformPredicatesImpl.register();
        TFPlatformPacketsImpl.register();
        TFPlatformRegistryImpl.register();

        TFRegistry.REGISTRATE.registerEventListeners(eventBus);
        TFRegistry.REGISTRATE.addDataGenerator(ProviderType.LANG, TradingFloor::addPostInitLang);

        TradingFloor.PLATFORM = "Forge";
        TradingFloor.init();
        TFPackets.register();

        eventBus.addListener(TradingFloorData::gatherData);
        eventBus.addListener(TradingFloor::clientInit);
        eventBus.addListener(TradingFloor::commonInit);

        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);

        TFRegistry.init();
        TFParticleEmitters.register();

        TFLangEntries.addEntries();
        TFDisplaySources.register();


        LOGGER.info("{} initializing! Platform: {}", NAME, PLATFORM);
        TFRegistry.init();
        TFParticleEmitters.register();

        TFLangEntries.addEntries();
        TFDisplaySources.register();
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new TFPonderPlugin());
    }

    public static ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    private static void addPostInitLang(RegistrateLangProvider registrateLangProvider) {
        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new TFPonderPlugin());
        PonderIndex.getLangAccess().provideLang(TradingFloor.MOD_ID, registrateLangProvider::add);

        TFAdvancements.provideLang(registrateLangProvider::add);
    }

    public static void commonInit(final FMLCommonSetupEvent event) {
        event.enqueueWork(TFAdvancements::register);
    }

    public static class ModBusEvents {
        @net.neoforged.bus.api.SubscribeEvent
        public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
            TradingDepotBlockEntity.registerCapabilities(event);
        }
    }

}
