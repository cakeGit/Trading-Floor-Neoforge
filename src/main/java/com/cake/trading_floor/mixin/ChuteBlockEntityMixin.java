package com.cake.trading_floor.mixin;

import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.content.trading_depot.TradingDepotItemHandler;
import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**Extends the fan reach of chutes by one block when dealing with trading depots*/
@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    @Shadow protected abstract void handleInput(@Nullable IItemHandler inv, float startLocation);

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Inject(method = "grabCapability", at = @At("RETURN"), cancellable = true)
    public void grabCapability(Direction side, CallbackInfoReturnable<IItemHandler> cir) {
        if (cir.getReturnValue() != null || side != Direction.DOWN)
            return;

        BlockState block = level.getBlockState(getBlockPos().relative(Direction.DOWN, 1));
        if (!block.isAir()) return;

        BlockEntity be = level.getBlockEntity(getBlockPos().relative(Direction.DOWN, 2));
        if (!(be instanceof TradingDepotBlockEntity tbe))
            return;

        cir.setReturnValue(tbe.getBehaviour(TradingDepotBehaviour.TYPE).getRealItemHandler());
    }
    
    @Redirect(method = "handleInputFromBelow", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/chute/ChuteBlockEntity;handleInput(Lnet/neoforged/neoforge/items/IItemHandler;F)V"))
    private void redirect_handleInputFromBelow(ChuteBlockEntity instance, IItemHandler iItemHandler, float inv) {
        if (iItemHandler instanceof TradingDepotItemHandler tbie) {
            BlockEntity be = tbie.getBehaviour().blockEntity;
            if (be.getBlockPos().getY() == instance.getBlockPos().getY() - 2) {
                handleInput(iItemHandler, inv - 1);
                return;
            }
        }
        handleInput(iItemHandler, inv);
    }
    
    @Shadow
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    
    }
    
}
