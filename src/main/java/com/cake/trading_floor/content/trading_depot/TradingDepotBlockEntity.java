package com.cake.trading_floor.content.trading_depot;

import com.cake.trading_floor.Config;
import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotValueBox;
import com.cake.trading_floor.foundation.AttachedTradingDepotFinder;
import com.cake.trading_floor.foundation.MerchantOfferInfo;
import com.cake.trading_floor.foundation.TFLang;
import com.cake.trading_floor.foundation.advancement.TFAdvancementBehaviour;
import com.cake.trading_floor.foundation.advancement.TFAdvancements;
import com.cake.trading_floor.registry.TFParticleEmitters;
import com.cake.trading_floor.registry.TFRegistry;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TradingDepotBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    
    TradingDepotBehaviour tradingDepotBehaviour;
    FilteringBehaviour filtering;
    
    public TradingDepotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    List<BlockEntity> tradingDepotsForDisplay = new ArrayList<>();
    
    /**
     * Note that last trade is not set to null after a failed trade, only the count changes
     */
    @Nullable
    MerchantOfferInfo lastTrade;
    int lastTradeCount = 0;
    
    //Tracking data for displays

    int tradeOutputSum = 0;
    int currentTradeCompletedCount = 0;
    
    /**
     * Advancement only
     */
    int emeraldsProduced = 0;
    
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BlockPos attachedWorkstationPosition = getBlockPos().relative(
            getBlockState().getValue(TradingDepotBlock.FACING).getOpposite()
        );
        updateOtherSourcesForTooltip(attachedWorkstationPosition);
        
        tradingDepotBehaviour.addContentsToTooltip(tooltip);
        
        if (lastTrade != null) {
            TFLang.translate("tooltip.trading_depot.last_trade")
                .add(TFLang.text(" (x" + lastTradeCount + ")").color(lastTradeCount == 0 ? 0xFF5555 : 0x55FFFF))
                .forGoggles(tooltip);
            
            addTradeToGoggles(tooltip, lastTrade);
        }
        
        int tradingDepotOtherSourceCount = tradingDepotsForDisplay.size() - 1;
        
        if (tradingDepotOtherSourceCount > 0) {
            TFLang.text("").forGoggles(tooltip);
            TFLang.translate("tooltip.trading_depot.connected_to_other")
                .add(TFLang.text(" " + tradingDepotOtherSourceCount + " ").style(ChatFormatting.AQUA))
                .translate("tooltip.trading_depot.other_trading_depot" + (tradingDepotOtherSourceCount > 1 ? "s" : ""))
                .style(ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip);
        }
        return true;
    }
    
    private void addTradeToGoggles(List<Component> tooltip, MerchantOfferInfo trade) {
        LangBuilder costText = TFLang.itemStack(trade.getCostA());
        
        if (!trade.getCostB().isEmpty())
            costText.text(" + ")
                .add(TFLang.itemStack(trade.getCostB()))
                .style(ChatFormatting.GRAY);
        
        costText.forGoggles(tooltip, 1);
        
        TFLang.text("→ ")
            .add(TFLang.itemStack(trade.getResult()))
            .style(ChatFormatting.WHITE)
            .forGoggles(tooltip, 2);
    }
    
    private void updateOtherSourcesForTooltip(BlockPos attachedWorkstationPosition) {
        if (level == null) {
            tradingDepotsForDisplay = new ArrayList<>();
            return;
        }
        
        tradingDepotsForDisplay = AttachedTradingDepotFinder.lookForTradingDepots(level, attachedWorkstationPosition).stream()
            .map(blockPos -> level.getBlockEntity(blockPos))
            .filter(blockEntity -> blockEntity instanceof TradingDepotBlockEntity)
            .toList();
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tradingDepotBehaviour = new TradingDepotBehaviour(this));
        behaviours.add(filtering = new FilteringBehaviour(this, new TradingDepotValueBox())
            .withCallback($ -> tradingDepotBehaviour.resetInv()));
        
        filtering.setLabel(TFLang.translate("tooltip.trading_depot.filtering.trade_filter").component());
        
        TFAdvancementBehaviour.create(behaviours, this,
            TFAdvancements.MONEY_MONEY_MONEY, TFAdvancements.BUDDING_CAPITALIST, TFAdvancements.HAPPY_JEFF
        );
        
        tradingDepotBehaviour.filtering = filtering;
        tradingDepotBehaviour.addAdditionalBehaviours(behaviours);
    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            TFRegistry.TRADING_DEPOT_BLOCK_ENTITY.get(),
            (be, context) -> be.tradingDepotBehaviour.getRealItemHandler()
        );
    }
    
    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("LastTrade"))
            lastTrade = MerchantOfferInfo.read(registries, tag.getCompound("LastTrade"));
        lastTradeCount = tag.getInt("LastTradeCount");
        
        emeraldsProduced = tag.getInt("EmeraldsProduced");
        tradeOutputSum = tag.getInt("TradeOutputSum");
        currentTradeCompletedCount = tag.getInt("CurrentTradeCompletedCount");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if (lastTrade != null)
            tag.put("LastTrade", lastTrade.write(registries, new CompoundTag()));
        tag.putInt("LastTradeCount", lastTradeCount);

        if (getBehaviour(TFAdvancementBehaviour.TYPE).isOwnerPresent())
            tag.putInt("EmeraldsProduced", emeraldsProduced);

        tag.putInt("TradeOutputSum", tradeOutputSum);
        tag.putInt("CurrentTradeCompletedCount", currentTradeCompletedCount);
    }
    
    /**
     * Try to perform for the offer, return false if it was not completed (due to not having enough money), and put the
     * output into the cost A depot
     */
    protected boolean tryTakeMerchantOffer(MerchantOffer offer, TradingDepotBehaviour costASource, List<TradingDepotBehaviour> costBSources) {
        //Quickly check if the A cost matches, if not don't bother with anything else
        if (!ItemStack.isSameItem(offer.getBaseCostA(), costASource.getOfferStack())) return false;
        
        //Check the second cost if it's there
        ItemStack totalCostBSource = ItemStack.EMPTY;
        if (!offer.getCostB().isEmpty()) {
            costBSources = costBSources.stream()
                .filter(depot -> ItemStack.isSameItem(offer.getCostB(), depot.getOfferStack()))
                .toList();
            
            if (costBSources.isEmpty()) return false;
            
            int totalCostB = 0;
            for (TradingDepotBehaviour depot : costBSources)
                totalCostB += depot.getOfferStack().getCount();
            
            if (offer.getCostB().getCount() > totalCostB) return false;
            
            totalCostBSource = costBSources.get(0).getOfferStack().copyWithCount(totalCostB);
        }
        
        //Check both match
        if (!satisfiedBaseCostBy(offer, costASource.getOfferStack(), totalCostBSource)) return false;
        
        //Perform transaction
        costASource.setOfferStack(costASource.getOfferStack()
            .copyWithCount(costASource.getOfferStack().getCount() - offer.getBaseCostA().getCount()));
        takeTotalFromSources(costBSources, offer.getCostB().getCount());
        
        costASource.getResults().add(offer.assemble());
        
        return true;
    }
    
    protected void takeTotalFromSources(List<TradingDepotBehaviour> costBSources, int totalExtractCount) {
        int i = 0;
        
        while (totalExtractCount > 0) {
            if (costBSources.size() <= i) return;
            
            TradingDepotBehaviour costSource = costBSources.get(i);
            
            int currentCount = costSource.getOfferStack().getCount();
            int extractCount = Math.min(totalExtractCount, currentCount);
            
            costSource.setOfferStack(costSource.getOfferStack().copyWithCount(currentCount - extractCount));
            
            totalExtractCount -= extractCount;
            i++;
        }
    }
    
    public void tryTradeWith(Villager villager, List<TradingDepotBehaviour> allDepots) {
        if (!tradingDepotBehaviour.isOutputEmpty()) return;

        //Don't use self
        List<TradingDepotBehaviour> costBSources = allDepots.stream()
            .filter(depot -> depot != tradingDepotBehaviour)
            .toList();
        
        boolean hadSuccessfulTrade = false;
        boolean hasSpace = true;
        
        MerchantOfferInfo latestTrade = null;
        int latestTradeCount = 0;
        
        for (MerchantOffer offer : villager.getOffers()) {
            if (!hasSpace) break;
            
            if (!filtering.getFilter().isEmpty() && (
                !((filtering.getFilter().getItem() instanceof FilterItem) &&
                filtering.test(offer.getResult()) ||
                isRequiredItem(offer.getResult(), filtering.getFilter()))
            )) continue;
            List<TradingDepotBehaviour> filteredCostBSources = costBSources.stream().filter(depot -> depot.canBeUsedFor(offer)).toList();
            
            boolean trading = true;
            while (trading) {
                if (tradingDepotBehaviour.getResults().size() >= 8) {
                    tradingDepotBehaviour.combineOutputs();
                    if (tradingDepotBehaviour.getResults().size() >= 8) {
                        hasSpace = false;
                        break;
                    }
                }
                
                trading = tryTakeMerchantOffer(offer, tradingDepotBehaviour, filteredCostBSources);
                
                if (trading) {
                    latestTrade = new MerchantOfferInfo(offer);
                    latestTradeCount++;
                }
                
                hadSuccessfulTrade = hadSuccessfulTrade || trading;

                if (latestTradeCount >= Config.maxTradePerWork) {
                    break;
                }
            }
            
            //Only do one type of trade per cycle
            if (hadSuccessfulTrade) break;
        }
        
        if (hadSuccessfulTrade) {
            tradingDepotBehaviour.combineOutputs();
            villager.playCelebrateSound();
            getBehaviour(TFAdvancementBehaviour.TYPE).awardPlayer(TFAdvancements.MONEY_MONEY_MONEY);
            
            if (level instanceof ServerLevel serverLevel)
                TFParticleEmitters.TRADE_COMPLETED.emitToClients(serverLevel, Vec3.atCenterOf(getBlockPos()).add(0, 0.4, 0), 4);
        }
        
        if (latestTrade != null && !Objects.equals(lastTrade, latestTrade)) {
            currentTradeCompletedCount = 0;
            tradeOutputSum = 0;
        }
        lastTrade = latestTrade;
        
        if (latestTrade != null) {
            currentTradeCompletedCount += latestTradeCount;
            tradeOutputSum += latestTradeCount * latestTrade.getResult().getCount();
            
            lastTradeCount = latestTradeCount;
        }

        int experienceCount = getExperienceProduced();
        if (experienceCount > 0) {
            tradingDepotBehaviour.getResults().add(new ItemStack(AllItems.EXP_NUGGET.get(), experienceCount));
        }

        checkForAwardedAdvancements();
        
        notifyUpdate();
    }

    private int getExperienceProduced() {
        if (!Config.shouldProduceExperience) return 0;

        int experienceCount = 0;
        for (int i = 0; i < Config.generatedExperienceCount; i++) {
            if (level.random.nextDouble() < Config.chancePerExperience) {
                experienceCount++;
            }
        }
        return experienceCount;
    }

    private void checkForAwardedAdvancements() {
        if (lastTrade != null && lastTrade.getResult().is(Items.EMERALD)) {
            TFAdvancementBehaviour advancementBehaviour = getBehaviour(TFAdvancementBehaviour.TYPE);
            if (tradeOutputSum >= 64) {
                advancementBehaviour.awardPlayer(TFAdvancements.BUDDING_CAPITALIST);
            }
            if (tradeOutputSum >= 1000) {
                advancementBehaviour.awardPlayer(TFAdvancements.HAPPY_JEFF);
            }
        }
    }
    
    public static boolean satisfiedBaseCostBy(MerchantOffer offer, ItemStack playerOfferA, ItemStack playerOfferB) {
        return isRequiredItem(playerOfferA, offer.getBaseCostA()) &&
            playerOfferA.getCount() >= offer.getBaseCostA().getCount() &&
            isRequiredItem(playerOfferB, offer.getCostB()) &&
            playerOfferB.getCount() >= offer.getCostB().getCount();
    }
    
    private static boolean isRequiredItem(ItemStack available, ItemStack cost) {
        if (cost.isEmpty() && available.isEmpty()) {
            return true;
        }
        //Ignore damage
        ItemStack itemstack = available.copy();
        if (itemstack.getItem().isDamageable(itemstack)) {
            itemstack.setDamageValue(itemstack.getDamageValue());
        }
        return ItemStack.isSameItemSameComponents(itemstack, cost);
    }
    
    public boolean hasInputStack() {
        return tradingDepotBehaviour.getOffer() != null && !tradingDepotBehaviour.getOfferStack().isEmpty();
    }
    
    public int getCurrentTradeCompletedCount() {
        return currentTradeCompletedCount;
    }
    
    public int getTradeOutputSum() {
        return tradeOutputSum;
    }
    
    public @Nullable MerchantOfferInfo getLastTrade() {
        return lastTrade;
    }
    
}
