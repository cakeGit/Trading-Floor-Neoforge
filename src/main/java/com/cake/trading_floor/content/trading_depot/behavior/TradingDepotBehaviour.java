package com.cake.trading_floor.content.trading_depot.behavior;

import com.cake.trading_floor.content.trading_depot.TradingDepotItemHandler;
import com.cake.trading_floor.foundation.TFLang;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TradingDepotBehaviour extends BlockEntityBehaviour {
    
    public static final BehaviourType<TradingDepotBehaviour> TYPE = new BehaviourType<>();
    public FilteringBehaviour filtering;
    
    final TradingDepotItemHandler itemHandler;
    
    TransportedItemStack offer;
    List<ItemStack> result;
    List<TransportedItemStack> incoming;
    
    VersionedInventoryTrackerBehaviour invVersionTracker;
    
    boolean pruneEmptyStacksNextTick = false;
    
    public TradingDepotBehaviour(SmartBlockEntity be) {
        super(be);
        itemHandler = new TradingDepotItemHandler(this);
        result = new ArrayList<>();
        incoming = new ArrayList<>();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        Level world = blockEntity.getLevel();
        if (world == null) return;
        
        if (pruneEmptyStacksNextTick) {
            result = new ArrayList<>(
                result.stream()
                    .filter(stack -> !stack.isEmpty())
                    .toList()
            );
            pruneEmptyStacksNextTick = false;
        }
        
        for (Iterator<TransportedItemStack> iterator = incoming.iterator(); iterator.hasNext(); ) {
            TransportedItemStack ts = iterator.next();
            if (!tick(ts))
                continue;
            if (world.isClientSide && !blockEntity.isVirtual())
                continue;
            if (offer == null) {
                offer = ts;
            } else {
                if (!ItemHelper.canItemStackAmountsStack(offer.stack, ts.stack)) {
                    Vec3 vec = VecHelper.getCenterOf(blockEntity.getBlockPos());
                    Containers.dropItemStack(blockEntity.getLevel(), vec.x, vec.y + .5f, vec.z, ts.stack);
                } else {
                    offer.stack.grow(ts.stack.getCount());
                }
            }
            iterator.remove();
            blockEntity.notifyUpdate();
        }
        
        if (offer == null)
            return;
        tick(offer);
    }
    
    public void doPruneEmptyStacksNextTick() {
        pruneEmptyStacksNextTick = true;
    }
    
    protected boolean tick(TransportedItemStack input) {
        input.prevBeltPosition = input.beltPosition;
        input.prevSideOffset = input.sideOffset;
        float diff = .5f - input.beltPosition;
        if (diff > 1 / 512f) {
            if (diff > 1 / 32f && !BeltHelper.isItemUpright(input.stack))
                input.angle += 1;
            input.beltPosition += diff / 4f;
        }
        return diff < 1 / 16f;
    }
    
    public void addAdditionalBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(blockEntity)
            .allowingBeltFunnels()
            .setInsertionHandler(this::tryInsertingFromSide));
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(blockEntity));
    }
    
    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;
        
        int size = transportedStack.stack.getCount();
        
        transportedStack = transportedStack.copy();
        
        transportedStack.beltPosition = side.getAxis().isVertical() ? .5f : 0;
        transportedStack.insertedFrom = side;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        
        ItemStack remainder = insert(transportedStack, simulate);
        if (remainder.getCount() != size)
            blockEntity.notifyUpdate();
        
        return remainder;
    }
    
    public int getPresentStackSize() {
        int cumulativeStackSize = 0;
        cumulativeStackSize += getOfferStack().getCount();
        for (ItemStack stack : result)
            cumulativeStackSize += stack
                .getCount();
        return cumulativeStackSize;
    }
    
    public int getRemainingSpace() {
        int cumulativeStackSize = getPresentStackSize();
        for (TransportedItemStack transportedItemStack : incoming)
            cumulativeStackSize += transportedItemStack.stack.getCount();
        return 64 - cumulativeStackSize;
    }
    
    public ItemStack insert(TransportedItemStack input, boolean simulate) {
        int remainingSpace = getRemainingSpace();
        ItemStack inserted = input.stack;
        if (remainingSpace <= 0)
            return inserted;
        if (this.offer != null && !this.offer.stack.isEmpty() && !ItemHelper.canItemStackAmountsStack(this.offer.stack, inserted))
            return inserted;
        
        ItemStack returned = ItemStack.EMPTY;
        if (remainingSpace < inserted.getCount()) {
            returned = input.stack.copyWithCount(inserted.getCount() - remainingSpace);
            if (!simulate) {
                TransportedItemStack copy = input.copy();
                copy.stack.setCount(remainingSpace);
                if (this.offer != null && !this.offer.stack.isEmpty())
                    incoming.add(copy);
                else
                    this.offer = copy;
            }
        } else {
            if (!simulate) {
                if (this.offer != null && !this.offer.stack.isEmpty())
                    incoming.add(input);
                else
                    this.offer = input;
            }
        }
        return returned;
    }
    
    public boolean isEmpty() {
        return offer == null && isOutputEmpty();
    }
    
    public boolean isOutputEmpty() {
        for (ItemStack stack : result)
            if (!stack.isEmpty())
                return false;
        return true;
    }
    
    @Override
    public void destroy() {
        super.destroy();
        Level level = getWorld();
        BlockPos pos = getPos();
        ItemHelper.dropContents(level, pos, itemHandler);
        for (TransportedItemStack transportedItemStack : incoming)
            Block.popResource(level, pos, transportedItemStack.stack);
    }

    @Override
    public void unload() {
        if (itemHandler != null)
            blockEntity.invalidateCapabilities();
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        offer = null;
        if (nbt.contains("Input"))
            offer = TransportedItemStack.read(nbt.getCompound("Input"), registries);
        
        int outputCount = nbt.getInt("OutputCount");
        result = new ArrayList<>(outputCount);
        
        for (int i = 0; i < outputCount; i++) {
            result.add(ItemStack.parseOptional(registries, nbt.getCompound("Output" + i)));
        }
        
        ListTag list = nbt.getList("Incoming", Tag.TAG_COMPOUND);
        incoming = NBTHelper.readCompoundList(list, c -> TransportedItemStack.read(c, registries));
    }
    
    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (offer != null)
            nbt.put("Input", offer.serializeNBT(registries));
        
        nbt.putInt("OutputCount", result.size());
        for (int i = 0; i < result.size(); i++) {
            nbt.put("Output" + i, result.get(i).saveOptional(registries));
        }
        
        if (!incoming.isEmpty())
            nbt.put("Incoming", NBTHelper.writeCompoundList(incoming, stack -> stack.serializeNBT(registries)));
    }

    public ItemStack getOfferStack() {
        return offer == null ? ItemStack.EMPTY : offer.stack;
    }
    
    public void setOfferStack(TransportedItemStack input) {
        this.offer = input;
    }
    
    public void setOfferStack(ItemStack input) {
        if (this.offer != null)
            this.offer.stack = input;
        else
            this.offer = new TransportedItemStack(input);
    }
    
    
    public void removeOfferStack() {
        this.offer = null;
    }
    
    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
    
    public void combineOutputs() {
        List<ItemStack> result = new ArrayList<>();
        
        for (ItemStack stack : this.result) {
            
            for (ItemStack other : result) {
                if (!ItemHelper.canItemStackAmountsStack(stack, other)) continue;
                
                int newCount = Math.min(other.getCount() + stack.getCount(), other.getMaxStackSize());
                int filledCount = newCount - other.getCount();
                
                other.setCount(newCount);
                stack.setCount(stack.getCount() - filledCount);
            }
            
            if (!stack.isEmpty())
                result.add(stack);
        }
        
        this.result = result;
    }
    
    public boolean canBeUsedFor(MerchantOffer offer) {
        return filtering.test(offer.getResult());
    }
    
    public void addContentsToTooltip(List<Component> tooltip) {
        TFLang.translate("tooltip.trading_depot.contents")
            .forGoggles(tooltip);
        
        if (offer != null && !offer.stack.isEmpty()) {
            TFLang.translate("tooltip.trading_depot.contents.input")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);
            
            TFLang.itemStack(offer.stack)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 2);
        }
        
        if (!result.isEmpty()) {
            TFLang.translate("tooltip.trading_depot.contents.output")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);
            
            for (ItemStack stack : result) {
                TFLang.itemStack(stack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 2);
            }
        }
    }
    
    public List<ItemStack> getResults() {
        return result;
    }
    
    public void resetInv() {
        invVersionTracker.reset();
    }
    
    public TransportedItemStack getOffer() {
        return offer;
    }
    
    public List<TransportedItemStack> getIncoming() {
        return incoming;
    }
    
    public TradingDepotItemHandler getRealItemHandler() {
        return itemHandler;
    }
    
    public void spinOfferOrSomething() {
        offer.angle += (int) ((Math.random() * 10 + 10) * (Math.random() > 0.5f ? -1 : 1));
    }
    
}
