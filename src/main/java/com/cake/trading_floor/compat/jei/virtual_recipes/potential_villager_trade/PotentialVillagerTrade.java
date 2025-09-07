package com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import com.cake.trading_floor.TradingFloor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class PotentialVillagerTrade implements Recipe<RecipeInput> {
    
    public static List<PotentialVillagerTrade> buildPotentialTrades(RegistryAccess provider, Level level) {
        ArrayList<PotentialVillagerTrade> trades = new ArrayList<>();
        Set<PotentialMerchantOfferInfo> existingOffers = new HashSet<>();
        
        for (Map.Entry<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> professionOffers : VillagerTrades.TRADES.entrySet()) {
            for (Int2ObjectMap.Entry<VillagerTrades.ItemListing[]> levelOffers : professionOffers.getValue().int2ObjectEntrySet()) {
                int index = 0;
                for (VillagerTrades.ItemListing listing : levelOffers.getValue()) {
                    
                    @Nullable PotentialMerchantOfferInfo offer = VillagerItemListingResolver.tryResolve(listing, provider, level);
                    
                    if (offer != null && !existingOffers.contains(offer)) {
                        trades.add(new PotentialVillagerTrade(
                            TradingFloor.asResource("trade_" + professionOffers.getKey().name().replace(":", "_") + "_level_" + levelOffers.getIntKey() + "_" + index),
                            levelOffers.getIntKey(),
                            professionOffers.getKey(),
                            offer
                        ));
                        existingOffers.add(offer);
                    }
                    
                    index++;
                }
            }
        }
        return trades;
    }
    
    ResourceLocation id;
    int villagerLevel;
    VillagerProfession profession;
    PotentialMerchantOfferInfo offer;
    
    public PotentialVillagerTrade(ResourceLocation id, int villagerLevel, VillagerProfession profession, PotentialMerchantOfferInfo offer) {
        this.id = id;
        this.villagerLevel = villagerLevel;
        this.profession = profession;
        this.offer = offer;
    }

    
    public int getVillagerLevel() {
        return villagerLevel;
    }
    
    public VillagerProfession getProfession() {
        return profession;
    }
    
    public PotentialMerchantOfferInfo getOffer() {
        return offer;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RecipeType<?> getType() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

}
