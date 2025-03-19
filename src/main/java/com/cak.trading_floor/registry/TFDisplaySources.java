package com.cak.trading_floor.registry;

import com.cak.trading_floor.content.trading_depot.displays.CurrentTradeCompletedCountDisplay;
import com.cak.trading_floor.content.trading_depot.displays.TradeProductSumDisplay;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class TFDisplaySources {

    public static final RegistryEntry<DisplaySource, CurrentTradeCompletedCountDisplay> TRADE_COMPLETED_COUNT = simple("trade_completed_count", CurrentTradeCompletedCountDisplay::new);
    public static final RegistryEntry<DisplaySource, TradeProductSumDisplay> TRADE_PRODUCT_SUM = simple("trade_product_sum", TradeProductSumDisplay::new);

    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return TFRegistry.REGISTRATE.displaySource(name, supplier).register();
    }

    public static void register() {
    }

}
