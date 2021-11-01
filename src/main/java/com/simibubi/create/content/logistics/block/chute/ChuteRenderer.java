package com.simibubi.create.content.logistics.block.chute;

import com.jozufozu.flywheel.util.transform.MatrixTransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.block.chute.ChuteBlock.Shape;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;

public class ChuteRenderer extends SafeTileEntityRenderer<ChuteTileEntity> {

	public ChuteRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(ChuteTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		if (te.item.isEmpty())
			return;
		BlockState blockState = te.getBlockState();
		if (blockState.getValue(ChuteBlock.FACING) != Direction.DOWN)
			return;
		if (blockState.getValue(ChuteBlock.SHAPE) != Shape.WINDOW
			&& (te.bottomPullDistance == 0 || te.itemPosition.get(partialTicks) > .5f))
			return;

		renderItem(te, partialTicks, ms, buffer, light, overlay);
	}

	public static void renderItem(ChuteTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		ItemRenderer itemRenderer = Minecraft.getInstance()
			.getItemRenderer();
		MatrixTransformStack msr = MatrixTransformStack.of(ms);
		ms.pushPose();
		msr.centre();
		float itemScale = .5f;
		float itemPosition = te.itemPosition.get(partialTicks);
		ms.translate(0, -.5 + itemPosition, 0);
		ms.scale(itemScale, itemScale, itemScale);
		msr.rotateX(itemPosition * 180);
		msr.rotateY(itemPosition * 180);
		itemRenderer.renderStatic(te.item, TransformType.FIXED, light, overlay, ms, buffer);
		ms.popPose();
	}

}
