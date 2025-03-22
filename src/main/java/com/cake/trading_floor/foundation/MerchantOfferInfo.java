package com.cake.trading_floor.foundation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Objects;

public class MerchantOfferInfo {
    
    final ItemStack costA;
    final ItemStack costB;
    final ItemStack result;
    
    public MerchantOfferInfo(MerchantOffer offer) {
        this.costA = offer.getBaseCostA();
        this.costB = offer.getCostB();
        this.result = offer.getResult();
    }
    
    public MerchantOfferInfo(ItemStack costA, ItemStack costB, ItemStack result) {
        this.costA = costA;
        this.costB = costB;
        this.result = result;
    }
    
    protected MerchantOfferInfo(HolderLookup.Provider registryAccess, CompoundTag tag) {
        this.costA = ItemStack.parseOptional(registryAccess, tag.getCompound("CostA"));
        this.costB = ItemStack.parseOptional(registryAccess, tag.getCompound("CostB"));
        this.result = ItemStack.parseOptional(registryAccess, tag.getCompound("Result"));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchantOfferInfo that)) return false;
        return ItemStack.isSameItemSameComponents(costA, that.costA) && ItemStack.isSameItemSameComponents(costB, that.costB) && ItemStack.isSameItemSameComponents(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(costA, costB, result);
    }
    
    public static MerchantOfferInfo read(HolderLookup.Provider provider, CompoundTag tag) {
        return new MerchantOfferInfo(provider, tag);
    }
    
    public Tag write(HolderLookup.Provider registryAccess, CompoundTag tag) {
        tag.put("CostA", costA.saveOptional(registryAccess));
        tag.put("CostB", costB.saveOptional(registryAccess));
        tag.put("Result", result.saveOptional(registryAccess));
        return tag;
    }
    
    public ItemStack getCostA() {
        return costA;
    }
    
    public ItemStack getCostB() {
        return costB;
    }
    
    public ItemStack getResult() {
        return result;
    }
    
}
