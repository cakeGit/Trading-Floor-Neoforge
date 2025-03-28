package com.cake.trading_floor.content.trading_depot;

import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TradingDepotRenderer extends SmartBlockEntityRenderer<TradingDepotBlockEntity> {

    public TradingDepotRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TradingDepotBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        TradingDepotBehaviour behaviour = blockEntity.getBehaviour(TradingDepotBehaviour.TYPE);
        TransportedItemStack transported = behaviour.getOffer();
        TransformStack<PoseTransformStack> msr = TransformStack.of(ms);

        ms.pushPose();
        ms.translate(.5, 1, .5);

        List<TransportedItemStack> tisStacks = new ArrayList<>(behaviour.getIncoming());
        if (transported != null)
            tisStacks.add(transported);

        // Render main items
        for (TransportedItemStack tis : tisStacks) {
            ms.pushPose();
            msr.nudge(0);
            float offset = Mth.lerp(partialTicks, tis.prevBeltPosition, tis.beltPosition);
            float sideOffset = Mth.lerp(partialTicks, tis.prevSideOffset, tis.sideOffset);

            if (tis.insertedFrom.getAxis()
                .isHorizontal()) {
                Vec3 offsetVec = Vec3.atLowerCornerOf(tis.insertedFrom.getOpposite()
                    .getNormal()).scale(.5f - offset);
                ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
                boolean alongX = tis.insertedFrom.getClockWise()
                    .getAxis() == Direction.Axis.X;
            }

            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = new Random(0);

            TransformStack.of(ms)
                    .rotateYDegrees(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90)
                    .rotateZDegrees(22.5F);

            renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, itemStack, angle, r);
            ms.popPose();
        }
        
        // Render output items
        for (int i = 0; i < behaviour.getResults().size(); i++) {
            ItemStack stack = behaviour.getResults().get(i);
            if (stack.isEmpty())
                continue;
            ms.pushPose();

            TransformStack.of(ms)
                .rotateYDegrees(90 - blockEntity.getBlockState().getValue(TradingDepotBlock.FACING).get2DDataValue() * 90)
                .rotateZDegrees(22.5F);

            msr.nudge(i);

            msr.rotateYDegrees((215 + 360 / 8f * i) % 360);
            ms.translate(.35, .01 / (i + 1), 0);
            Random r = new Random(i + 1);
            int angle = (int) (360 * r.nextFloat());

            renderItem(blockEntity.getLevel(), ms, buffer, light, overlay, stack, angle, r);
            ms.popPose();
        }

        ms.popPose();
    }

    public static void renderItem(Level level, PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack itemStack, int angle, Random r) {
        ItemRenderer itemRenderer = Minecraft.getInstance()
                .getItemRenderer();
        TransformStack<PoseTransformStack> msr = TransformStack.of(ms);
        int count = Mth.log2(itemStack.getCount()) / 2;
        boolean blockItem = itemRenderer.getModel(itemStack, null, null, 0)
                .isGui3d();

        ms.pushPose();
        msr.rotateYDegrees(angle);

        for (int i = 0; i <= count; i++) {
            ms.pushPose();
            if (blockItem)
                ms.translate(r.nextFloat() * .0625f * i, 0, r.nextFloat() * .0625f * i);
            ms.scale(.5f, .5f, .5f);
            if (!blockItem) {
                ms.translate(0, -3 / 16f, 0);
                msr.rotateXDegrees(90);
            }
            itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, level, 0);
            ms.popPose();

            if (!blockItem)
                msr.rotateYDegrees(10);
            ms.translate(0, blockItem ? 1 / 64d : 1 / 16d, 0);
        }
        
        ms.popPose();
    }

}
