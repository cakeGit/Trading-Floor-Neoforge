package com.cake.trading_floor.registry;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.content.trading_depot.displays.CurrentTradeCompletedCountDisplaySource;
import com.cake.trading_floor.content.trading_depot.displays.TradeProductSumDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class TFDisplaySources {

    public static final RegistryEntry<DisplaySource, CurrentTradeCompletedCountDisplaySource> TRADE_COMPLETED_COUNT = simple("trade_completed_count", CurrentTradeCompletedCountDisplaySource::new);
    public static final RegistryEntry<DisplaySource, TradeProductSumDisplaySource> TRADE_PRODUCT_SUM = simple("trade_product_sum", TradeProductSumDisplaySource::new);

    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return TradingFloor.REGISTRATE.displaySource(name, supplier).register();
    }

    public static void register() {
    }

}
