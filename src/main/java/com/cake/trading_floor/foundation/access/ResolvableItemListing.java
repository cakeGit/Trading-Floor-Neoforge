package com.cake.trading_floor.foundation.access;

import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ResolvableItemListing {
    @Nullable
    default PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess provider) {
        return null;
    }

    @Nullable
    default PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess provider, Level level) {
        return create_trading_floor$resolve(provider);
    }
}
