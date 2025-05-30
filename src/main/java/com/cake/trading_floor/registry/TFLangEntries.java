package com.cake.trading_floor.registry;

import com.cake.trading_floor.TradingFloor;

import java.util.HashMap;
import java.util.Map;

public class TFLangEntries {
    
    public static void addEntries() {
        addIdLangEntries(
            "tooltip.trading_depot.trading_depot_info", "Trading Depot Info:",
            "tooltip.trading_depot.contents", "Trading Depot Contents:",
            "tooltip.trading_depot.contents.input", "Input (This depot only):",
            "tooltip.trading_depot.contents.output", "Output:",
            "tooltip.trading_depot.last_trade", "Last Trade:",
            "tooltip.trading_depot.connected_to_other", "Connected To",
            "tooltip.trading_depot.other_trading_depot", "other Trading Depot",
            "tooltip.trading_depot.other_trading_depots", "other Trading Depots",
            
            "tooltip.trading_depot.filtering.trade_filter", "Trade Output Filter",
            
            "display_link.trading_depot.no_trade", "No Trade",
            "display_link.trading_depot.trades_completed", "Trades completed:",
            
            "jei.potential_trade_type.title", "Villager Trading",
            
            "jei.missing_disclaimer", "Some trades may be missing or inaccurate when working with other mods",
            
            "jei.note.type_specific", "Villager-type specific",
            "jei.note.randomised_cost", "Randomised price",
            "jei.note.randomised_dye", "Randomised dye color",
            "jei.note.enchanted_variants", "Randomised enchantments"
        );

        addLangEntries(
            "trading_floor.display_source.trade_product_sum", "Trade Products Total",
            "trading_floor.display_source.trade_completed_count", "Trades Completed Count",
            "block.trading_floor.trading_depot.tooltip.summary", "Trade with Villagers, note that villagers will _only trade during normal working times of day_."
        );
    }
    
    public static void addIdLangEntries(String... rawEntries) {
        Map<String, String> entries = new HashMap<>();
        
        for (int i = 0; i < rawEntries.length; i += 2) {
            entries.put(rawEntries[i], rawEntries[i + 1]);
        }
        
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            TradingFloor.REGISTRATE.addRawLang(TradingFloor.MOD_ID + "." + entry.getKey(), entry.getValue());
        }
    }

    public static void addLangEntries(String... rawEntries) {
        Map<String, String> entries = new HashMap<>();

        for (int i = 0; i < rawEntries.length; i += 2) {
            entries.put(rawEntries[i], rawEntries[i + 1]);
        }

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            TradingFloor.REGISTRATE.addRawLang(entry.getKey(), entry.getValue());
        }
    }
    
}
