package com.simibubi.create.content.contraptions.fluids.pipes;

import java.util.Optional;

import com.simibubi.create.content.contraptions.relays.elementary.BracketedTileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class BracketBlockItem extends BlockItem {

	public BracketBlockItem(Block p_i48527_1_, Properties p_i48527_2_) {
		super(p_i48527_1_, p_i48527_2_);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);
		BracketBlock bracketBlock = getBracketBlock();
		Player player = context.getPlayer();

		BracketedTileEntityBehaviour behaviour = TileEntityBehaviour.get(world, pos, BracketedTileEntityBehaviour.TYPE);

		if (behaviour == null)
			return InteractionResult.FAIL;
		if (!behaviour.canHaveBracket())
			return InteractionResult.FAIL;
		if (world.isClientSide)
			return InteractionResult.SUCCESS;

		Optional<BlockState> suitableBracket = bracketBlock.getSuitableBracket(state, context.getClickedFace());
		if (!suitableBracket.isPresent() && player != null)
			suitableBracket =
				bracketBlock.getSuitableBracket(state, Direction.orderedByNearest(player)[0].getOpposite());
		if (!suitableBracket.isPresent())
			return InteractionResult.SUCCESS;

		BlockState bracket = behaviour.getBracket();
		behaviour.applyBracket(suitableBracket.get());
		
		if (!world.isClientSide && player != null)
			behaviour.triggerAdvancements(world, player, state);
		
		if (player == null || !player.isCreative()) {
			context.getItemInHand()
				.shrink(1);
			if (bracket != Blocks.AIR.defaultBlockState()) {
				ItemStack returnedStack = new ItemStack(bracket.getBlock());
				if (player == null)
					Block.popResource(world, pos, returnedStack);
				else
					player.inventory.placeItemBackInInventory(world, returnedStack);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private BracketBlock getBracketBlock() {
		return (BracketBlock) getBlock();
	}

}
