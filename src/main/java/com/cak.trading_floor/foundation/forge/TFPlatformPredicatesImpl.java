package com.cak.trading_floor.foundation.forge;

import com.cak.trading_floor.foundation.TFPlatformPredicates;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.FakePlayer;

public class TFPlatformPredicatesImpl implements TFPlatformPredicates.TFPlatformPredicatesImplementor {

    public static void register() {
        TFPlatformPredicates.PLATFORM = new TFPlatformPredicatesImpl();
    }

    public boolean isFakePlayer(LivingEntity player) {
        return player instanceof FakePlayer;
    }
    
}
