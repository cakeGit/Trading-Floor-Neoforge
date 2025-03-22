package com.cake.trading_floor.mixin.item_listings;

import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cake.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class EnchantBookForEmeraldsAccessMixin implements ResolvableItemListing {

    @Shadow @Final private TagKey<Enchantment> tradeableEnchantments;

    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess provider) {
        Optional<HolderSet.Named<Enchantment>> enchantmentHolders = provider.registryOrThrow(Registries.ENCHANTMENT).getTag(this.tradeableEnchantments);

        if (enchantmentHolders.isEmpty()) return null;

        List<Holder<Enchantment>> enchantments = enchantmentHolders.get().stream().toList();

        List<ItemStack> booksList = new ArrayList<>();
        
        for (Holder<Enchantment> enchantmentHolder : enchantments) {
            Enchantment enchantment = enchantmentHolder.value();
            for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++)
                booksList.add(
                    EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantmentHolder, i))
                );
        }
        
        return new PotentialMerchantOfferInfo(
            Items.EMERALD.getDefaultInstance(),
            ItemStack.EMPTY,
            booksList
        ).noteRandomisedEmeraldCost();
    }
    
}
