package com.cake.trading_floor.content.trading_depot;

import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class TradingDepotItemHandler implements net.neoforged.neoforge.items.IItemHandler {
    
    TradingDepotBehaviour behaviour;
    
    public TradingDepotItemHandler(TradingDepotBehaviour behaviour) {
        this.behaviour = behaviour;
    }
    
    @Override
    public int getSlots() {
        return 1 + behaviour.getResults().size();
    }
    
    @Override
    public ItemStack getStackInSlot(int i) {
        return i == 0 ? behaviour.getOfferStack() : behaviour.getResults().get(i - 1);
    }
    
    @Override
    public ItemStack insertItem(int i, ItemStack arg, boolean bl) {
        if (i != 0) return arg;
        
        if (!behaviour.getOfferStack().isEmpty() && !ItemHelper.canItemStackAmountsStack(behaviour.getOfferStack(), arg))
            return arg;
        
        ItemStack existingStack = behaviour.getOfferStack();
        
        int oldCount = existingStack.getCount();
        int newCount = Math.min(arg.getMaxStackSize(), oldCount + arg.getCount());
        int added = newCount - oldCount;
        int remaining = arg.getCount() - added;
        
        if (!bl) {
            behaviour.setOfferStack(arg.copyWithCount(newCount));
            if (newCount != oldCount)
                behaviour.spinOfferOrSomething();
            behaviour.blockEntity.sendData();
        }
        
        return arg.copyWithCount(remaining);
    }
    
    @Override
    public ItemStack extractItem(int i, int j, boolean bl) {
        if (i == 0) return ItemStack.EMPTY;
        
        int listIndex = i - 1;
        if (listIndex >= behaviour.getResults().size()) return ItemStack.EMPTY;
        
        ItemStack currentStack = behaviour.getResults().get(listIndex);
        
        int extractedCount = Math.min(currentStack.getCount(), j);
        
        ItemStack resultStack = currentStack.copyWithCount(extractedCount);
        ItemStack remainderStack = currentStack.copyWithCount(currentStack.getCount() - extractedCount);
        
        if (!bl) {
            this.behaviour.getResults().set(listIndex, remainderStack);
            this.behaviour.doPruneEmptyStacksNextTick();
            behaviour.blockEntity.sendData();
        }
        
        return resultStack;
    }
    
    @Override
    public int getSlotLimit(int i) {
        return 64;
    }
    
    @Override
    public boolean isItemValid(int i, ItemStack arg) {
        return true;
    }
    
    public ItemStack insertItem(TransportedItemStack transportedItemStack, Direction direction, boolean b) {
        return insertItem(0, transportedItemStack.stack, b);
    }

    public TradingDepotBehaviour getBehaviour() {
        return behaviour;
    }

}
