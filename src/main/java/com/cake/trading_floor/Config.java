package com.cake.trading_floor;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = TradingFloor.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_TRADE_PER_WORK = BUILDER
        .comment("Controls how many trades a worker can perform in one work cycle")
        .defineInRange("maxTradePerWork", 4, 1, Integer.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxTradePerWork;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC && event.getConfig().getType() == ModConfig.Type.SERVER) {
            maxTradePerWork = MAX_TRADE_PER_WORK.get();
        }
    }
}
