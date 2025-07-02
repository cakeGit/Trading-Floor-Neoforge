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

    public static final ModConfigSpec.BooleanValue SHOULD_PRODUCE_EXPERIENCE = BUILDER
        .comment("Controls whether depots produce experience when trading")
        .define("shouldProduceExperience", false);

    public static final ModConfigSpec.DoubleValue CHANCE_PER_EXPERIENCE = BUILDER
        .comment("The chance for each experience item to be produced when trading")
        .defineInRange("chancePerExperience", 0.2, 0, 1);

    public static final ModConfigSpec.IntValue GENERATED_EXPERIENCE_COUNT = BUILDER
        .comment("Controls how many chances for experience to generate there are per work cycle")
        .defineInRange("generatedExperienceCount", 4, 1, 64);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxTradePerWork;
    public static boolean shouldProduceExperience;
    public static double chancePerExperience;
    public static int generatedExperienceCount;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC && event.getConfig().getType() == ModConfig.Type.SERVER) {
            maxTradePerWork = MAX_TRADE_PER_WORK.get();
            shouldProduceExperience = SHOULD_PRODUCE_EXPERIENCE.get();
            chancePerExperience = CHANCE_PER_EXPERIENCE.get();
            generatedExperienceCount = GENERATED_EXPERIENCE_COUNT.get();
        }
    }
}
