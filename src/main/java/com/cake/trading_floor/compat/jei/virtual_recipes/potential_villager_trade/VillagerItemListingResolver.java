package com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import com.cake.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.Level;

public class VillagerItemListingResolver {
    
    public static PotentialMerchantOfferInfo tryResolve(VillagerTrades.ItemListing listing, RegistryAccess provider, Level level) {
        try {
            ResolvableItemListing resolver = (ResolvableItemListing) listing;
            return resolver.create_trading_floor$resolve(provider, level);
        } catch (ClassCastException e) {
            return null;
        }
    }
    
}
