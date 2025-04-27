package com.cake.trading_floor;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.foundation.advancement.TFAdvancements;
import com.cake.trading_floor.foundation.ponder_scenes.TFPonderPlugin;
import com.cake.trading_floor.network.TFPackets;
import com.cake.trading_floor.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TradingFloor.MOD_ID)
public class TradingFloor {

    public static final String MOD_ID = "trading_floor";
    public static final String NAME = "Create: Trading Floor";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public TradingFloor(IEventBus eventBus, ModContainer modContainer) {
        init(eventBus, modContainer);
    }

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(TradingFloor.MOD_ID)
        .setTooltipModifierFactory(item ->
            new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );;

    public void init(IEventBus eventBus, ModContainer modContainer) {

        TradingFloor.REGISTRATE.registerEventListeners(eventBus);
        
        TFPackets.register();

        TFRegistry.init();
        TFParticleEmitters.register();

        TFLangEntries.addEntries();
        TFDisplaySources.register();

        TFParticleEmitters.register();

        TFDisplaySources.register();

        TradingFloor.REGISTRATE.addDataGenerator(ProviderType.LANG, TradingFloor::addPostInitLang);

        eventBus.addListener(TradingFloorData::gatherData);
        eventBus.addListener(TradingFloor::registerCapabilities);
        eventBus.addListener(TradingFloor::clientInit);
        eventBus.addListener(TradingFloor::onRegister);
        eventBus.addListener(TradingFloor::commonInit);

        TradingFloor.LOGGER.info("Finished Initialisation For Mod: " + TradingFloor.MOD_ID);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        TradingDepotBlockEntity.registerCapabilities(event);
    }
    public static void onRegister(final RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            TFAdvancements.register();
            TFTriggers.register();
        }
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
