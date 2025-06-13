package com.cake.trading_floor.content.trading_depot;

import com.cake.trading_floor.content.trading_depot.behavior.TradingDepotBehaviour;
import com.cake.trading_floor.foundation.advancement.TFAdvancementBehaviour;
import com.cake.trading_floor.registry.TFRegistry;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class TradingDepotBlock extends HorizontalDirectionalBlock implements IBE<TradingDepotBlockEntity>, IWrenchable {

    public static final MapCodec<TradingDepotBlock> CODEC = simpleCodec(TradingDepotBlock::new);

    public static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 8, 16),
        Block.box(1, 8, 1, 15, 16, 15)
    );
    
    public TradingDepotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    protected static TradingDepotBehaviour get(BlockGetter worldIn, BlockPos pos) {
        return BlockEntityBehaviour.get(worldIn, pos, TradingDepotBehaviour.TYPE);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() == state.getValue(FACING).getOpposite())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;
        
        TradingDepotBehaviour behaviour = get(level, pos);
        if (behaviour == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean wasEmptyHanded = held.isEmpty();
        boolean skipItemPlacement = AllBlocks.MECHANICAL_ARM.isIn(held);
        
        if (wasEmptyHanded) {
            if (!behaviour.getResults().isEmpty()) {
                for (ItemStack stack : behaviour.getResults()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
                behaviour.getResults().clear();
            } else if (!behaviour.getOfferStack().isEmpty()) {
                player.getInventory().placeItemBackInInventory(behaviour.getOfferStack());
                behaviour.setOfferStack(ItemStack.EMPTY);
            }
        } else if (!skipItemPlacement) {
            TransportedItemStack transported = new TransportedItemStack(held);
            
            transported.insertedFrom = player.getDirection();
            transported.prevBeltPosition = .25f;
            transported.beltPosition = .25f;
            
            if (!behaviour.getOfferStack().isEmpty()) {
                player.getInventory().placeItemBackInInventory(behaviour.getOfferStack());
                behaviour.setOfferStack(ItemStack.EMPTY);
            }
            
            behaviour.setOfferStack(transported);
            player.setItemInHand(hand, ItemStack.EMPTY);
            
            AllSoundEvents.DEPOT_SLIDE.playOnServer(level, pos);
        }
        
        behaviour.blockEntity.notifyUpdate();
        return ItemInteractionResult.SUCCESS;
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }
    
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
    
    @Override
    public Class<TradingDepotBlockEntity> getBlockEntityClass() {
        return TradingDepotBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends TradingDepotBlockEntity> getBlockEntityType() {
        return TFRegistry.TRADING_DEPOT_BLOCK_ENTITY.get();
    }
    
    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        if (entityIn.level().isClientSide)
            return;
        
        ItemEntity itemEntity = (ItemEntity) entityIn;
        DirectBeltInputBehaviour inputBehaviour =
            BlockEntityBehaviour.get(worldIn, BlockPos.containing(entityIn.position().subtract(0, 0.1, 0)), DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour == null)
            return;
        ItemStack remainder = inputBehaviour.handleInsertion(itemEntity.getItem(), Direction.DOWN, false);
        itemEntity.setItem(remainder);
        if (remainder.isEmpty())
            itemEntity.discard();
    }
    
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        TFAdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TradingDepotBlockEntity(TFRegistry.TRADING_DEPOT_BLOCK_ENTITY.get(), pos, state);
    }
}
