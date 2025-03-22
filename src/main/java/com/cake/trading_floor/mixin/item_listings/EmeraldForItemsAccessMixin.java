package com.cake.trading_floor.mixin.item_listings;

import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cake.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EmeraldForItems")
public class EmeraldForItemsAccessMixin implements ResolvableItemListing {

    @Shadow @Final private ItemCost itemStack;

    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess provider) {
        ItemStack itemStack = this.itemStack.itemStack();
        return new PotentialMerchantOfferInfo(itemStack, ItemStack.EMPTY, new ItemStack(Items.EMERALD));
    }
    
}
