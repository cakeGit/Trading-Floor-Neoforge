package com.cake.trading_floor.mixin.item_listings;

import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialMerchantOfferInfo;
import com.cake.trading_floor.foundation.access.ResolvableItemListing;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$SuspiciousStewForEmerald")
public class SussyStewForEmeraldsAccessMixin implements ResolvableItemListing {

    @Shadow @Final private SuspiciousStewEffects effects;

    @Override
    public @Nullable PotentialMerchantOfferInfo create_trading_floor$resolve(RegistryAccess access) {
        ItemStack itemstack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        itemstack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.effects);
        return new PotentialMerchantOfferInfo(new ItemStack(Items.EMERALD), ItemStack.EMPTY, itemstack);
    }

}
