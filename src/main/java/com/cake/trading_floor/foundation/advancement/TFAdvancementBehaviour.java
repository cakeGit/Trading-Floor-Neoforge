package com.cake.trading_floor.foundation.advancement;

import com.cake.trading_floor.foundation.TFPlatformPredicates;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Mirror of {@link AdvancementBehaviour}, to work with TradingFloor's advancements
 */
public class TFAdvancementBehaviour extends BlockEntityBehaviour {
    
    public static final BehaviourType<TFAdvancementBehaviour> TYPE = new BehaviourType<>();
    
    private UUID playerId;
    private final Set<TFAdvancement> advancements;
    
    public static void create(List<BlockEntityBehaviour> behaviours, SmartBlockEntity be, TFAdvancement... advancements) {
        TFAdvancementBehaviour existing = (TFAdvancementBehaviour) behaviours.stream()
            .filter(blockEntityBehaviour -> blockEntityBehaviour instanceof TFAdvancementBehaviour)
            .findFirst().orElse(null);
        
        if (existing != null) {
            existing.advancements.addAll(List.of(advancements));
        } else {
            behaviours.add(new TFAdvancementBehaviour(be, advancements));
        }
    }
    
    protected TFAdvancementBehaviour(SmartBlockEntity be, TFAdvancement... advancements) {
        super(be);
        this.advancements = new HashSet<>();
        add(advancements);
    }
    
    public void add(TFAdvancement... advancements) {
        this.advancements.addAll(Arrays.asList(advancements));
    }
    
    public boolean isOwnerPresent() {
        return playerId != null;
    }
    
    public void setPlayer(UUID id) {
        Player player = getWorld().getPlayerByUUID(id);
        if (player == null)
            return;
        playerId = id;
        removeAwarded();
        blockEntity.setChanged();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        removeAwarded();
    }
    
    private void removeAwarded() {
        Player player = getPlayer();
        if (player == null)
            return;
        advancements.removeIf(c -> c.isAlreadyAwardedTo(player));
        if (advancements.isEmpty()) {
            playerId = null;
            blockEntity.setChanged();
        }
    }
    
    public void awardPlayerIfNear(TFAdvancement advancement, int maxDistance) {
        Player player = getPlayer();
        if (player == null)
            return;
        if (player.distanceToSqr(Vec3.atCenterOf(getPos())) > maxDistance * maxDistance)
            return;
        award(advancement, player);
    }
    
    public void awardPlayer(TFAdvancement advancement) {
        Player player = getPlayer();
        if (player == null)
            return;
        award(advancement, player);
    }
    
    private void award(TFAdvancement advancement, Player player) {
        if (advancements.contains(advancement))
            advancement.awardTo(player);
        removeAwarded();
    }
    
    private Player getPlayer() {
        if (playerId == null)
            return null;
        return getWorld().getPlayerByUUID(playerId);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (playerId != null)
            nbt.putUUID("Owner", playerId);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("Owner"))
            playerId = nbt.getUUID("Owner");
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
    
    public static void tryAward(BlockGetter reader, BlockPos pos, TFAdvancement advancement) {
        TFAdvancementBehaviour behaviour = BlockEntityBehaviour.get(reader, pos, TYPE);
        if (behaviour != null)
            behaviour.awardPlayer(advancement);
    }
    
    public static void setPlacedBy(Level worldIn, BlockPos pos, LivingEntity placer) {
        TFAdvancementBehaviour behaviour = BlockEntityBehaviour.get(worldIn, pos, TYPE);
        if (behaviour == null)
            return;
        if (TFPlatformPredicates.isFakePlayer(placer))
            return;
        if (placer instanceof ServerPlayer)
            behaviour.setPlayer(placer.getUUID());
    }
    
}
