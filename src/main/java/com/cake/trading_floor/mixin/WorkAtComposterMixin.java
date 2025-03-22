package com.cake.trading_floor.mixin;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.cake.trading_floor.foundation.AttachedTradingDepotFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.WorkAtComposter;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(WorkAtComposter.class)
public class WorkAtComposterMixin extends WorkAtPoi {
    
    @Inject(method = "useWorkstation", at = @At("HEAD"))
    public void useWorkstation(ServerLevel level, Villager villager, CallbackInfo ci) {
        trading_floor$innerUseWorkstation(level, villager, ci);
    }
    
    @Unique
    private void trading_floor$innerUseWorkstation(ServerLevel level, Villager villager, CallbackInfo ci) {
        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return;
        
        BlockPos jobSitePos = jobSite.get().pos();
        
        List<BlockPos> tradingDepotPositions = AttachedTradingDepotFinder.lookForTradingDepots(level, jobSitePos);
        
        List<TradingDepotBlockEntity> tradingDepots = tradingDepotPositions.stream()
            .map(pos -> (TradingDepotBlockEntity) level.getBlockEntity(pos))
            .filter(Objects::nonNull)
            .filter(TradingDepotBlockEntity::hasInputStack)
            .toList();
        List<TradingDepotBehaviour> tradingDepotBehaviours = tradingDepots.stream()
            .map(e -> e.getBehaviour(TradingDepotBehaviour.TYPE))
            .toList();
        
        tradingDepots.forEach(depot -> depot.tryTradeWith(villager, tradingDepotBehaviours));
    }
    
    
}
