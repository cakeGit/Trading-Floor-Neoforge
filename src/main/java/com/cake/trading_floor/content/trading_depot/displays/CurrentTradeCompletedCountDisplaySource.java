package com.cake.trading_floor.content.trading_depot.displays;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.foundation.TFLang;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class CurrentTradeCompletedCountDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof TradingDepotBlockEntity depot))
            return null;

        if (depot.getLastTrade() == null)
            return TFLang.translate("display_link.trading_depot.no_trade").component();

        return Component.literal(String.valueOf(depot.getCurrentTradeCompletedCount()));
    }

    @Override
    protected String getTranslationKey() {
        return "trade_completed_count";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Number";
    }

}
