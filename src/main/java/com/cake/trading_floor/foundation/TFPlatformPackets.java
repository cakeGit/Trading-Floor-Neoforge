package com.cake.trading_floor.foundation;

import com.cake.trading_floor.network.packets.EmitParticlesFromInstancePacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TFPlatformPackets {

    public static void sendEmitParticlesToNear(ServerLevel level, ParticleEmitter particleEmitter, Vec3 origin, int count, BlockPos pos, int sendPacketRange) {
        CatnipServices.NETWORK.sendToClientsAround(level, pos, sendPacketRange, new EmitParticlesFromInstancePacket(origin, count, particleEmitter.hashCode()));
    }

}
