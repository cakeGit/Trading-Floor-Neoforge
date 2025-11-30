package com.cake.trading_floor.content.trading_depot.displays;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.foundation.TFLang;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.api.behaviour.display.DisplaySource;
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

public class TradeProductSumDisplay extends SingleLineDisplaySource {
    
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
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine && allowsLabeling(context))
            addLabelingTextBox(builder);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addLabelingTextBox(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip(ImmutableList.of(CreateLang.translateDirect("display_source.label")
                            .withStyle(s -> s.withColor(0x5391E1)),
                    CreateLang.translateDirect("gui.schedule.lmb_edit")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
        }, "Label");
    }

    @Override
    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {

        if (allowsLabeling(context)) {
            String label = context.sourceConfig()
                    .getString("Label");
            if (!label.isEmpty()) {
                return ImmutableList.of(ImmutableList.of(Component.literal(label + " "), provideLine(context, stats)));
            }
        }

        return super.provideFlapDisplayText(context, stats);
    }
}
