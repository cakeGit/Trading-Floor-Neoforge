package com.cake.trading_floor.content.trading_depot.displays;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.foundation.TFLang;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CurrentTradeCompletedCountDisplay extends DisplaySource {
    
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof TradingDepotBlockEntity depot))
            return List.of();
        
        if (depot.getLastTrade() == null)
            return List.of(TFLang.translate("display_link.trading_depot.no_trade").component());
        
        return List.of(TFLang.translate("display_link.trading_depot.trades_completed")
            .text(" " + depot.getCurrentTradeCompletedCount()).component());
    }
    
}
