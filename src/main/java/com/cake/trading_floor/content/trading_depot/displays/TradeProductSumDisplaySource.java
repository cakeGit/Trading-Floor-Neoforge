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
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class TradeProductSumDisplaySource extends SingleLineDisplaySource {
    
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof TradingDepotBlockEntity depot))
            return null;
        
        if (depot.getLastTrade() == null)
            return TFLang.translate("display_link.trading_depot.no_trade").component();
        
        ItemStack stack = depot.getLastTrade().getResult().copyWithCount(depot.getTradeOutputSum());
        
        return TFLang.itemStackAlt(stack).component();
    }

    @Override
    protected String getTranslationKey() {
        return "trade_product_sum";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Default";
    }

}
