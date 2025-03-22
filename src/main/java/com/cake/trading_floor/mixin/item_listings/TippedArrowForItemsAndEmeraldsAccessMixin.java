package com.cake.trading_floor.mixin.item_listings;

import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cake.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$TippedArrowForItemsAndEmeralds")
public class TippedArrowForItemsAndEmeraldsAccessMixin implements ResolvableItemListing {
    
    @Shadow @Final private int emeraldCost;
    
    @Shadow @Final private Item fromItem;
    
    @Shadow @Final private int fromCount;
    
    @Shadow @Final private ItemStack toItem;
    
    @Shadow @Final private int toCount;
    
    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess registryAccess, Level level) {
        List<Holder.Reference<Potion>> list = registryAccess.registryOrThrow(Registries.POTION).holders()
            .filter((potion) -> !potion.value().getEffects().isEmpty() && level.potionBrewing().isBrewablePotion(potion))
            .toList();
        
        ItemStack toItemBase = toItem.copyWithCount(toCount);
        
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance().copyWithCount(emeraldCost),
            fromItem.getDefaultInstance().copyWithCount(fromCount),
            list.stream().map(potion -> {
                ItemStack stack = toItemBase.copy();
                stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                return stack;
            }).toList()
        );
    }
    
}
