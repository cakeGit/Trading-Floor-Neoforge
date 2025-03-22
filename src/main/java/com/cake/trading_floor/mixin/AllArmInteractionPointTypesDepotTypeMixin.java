package com.cake.trading_floor.mixin;

import com.cake.trading_floor.registry.TFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DepotType", remap = false)
public class AllArmInteractionPointTypesDepotTypeMixin {

    @Inject(method = "canCreatePoint", at = @At("RETURN"), cancellable = true)
    private void init(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            cir.setReturnValue(state.is(TFRegistry.TRADING_DEPOT.get()));
        }
    }

}
