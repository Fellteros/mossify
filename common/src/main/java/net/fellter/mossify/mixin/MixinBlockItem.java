package net.fellter.mossify.mixin;

import net.fellter.mossify.MossifiableBlockRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {
	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	private void fellter$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (context.getStack().isOf(Items.MOSS_BLOCK)) {
			World world = context.getWorld();
			BlockPos pos = context.getBlockPos();
			BlockState state = world.getBlockState(pos);
			PlayerEntity playerEntity = context.getPlayer();

			if (MossifiableBlockRegistry.BLOCK_MAP.containsKey(state.getBlock()) && playerEntity != null) {
				if (playerEntity.isSneaking()) {
					playerEntity.swingHand(context.getHand());
					context.getStack().decrementUnlessCreative(1, playerEntity);

					BlockState mossy = MossifiableBlockRegistry.BLOCK_MAP.get(state.getBlock()).getStateWithProperties(state);

					world.setBlockState(pos, mossy);
					world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(playerEntity, mossy));
					cir.setReturnValue(ActionResult.SUCCESS);
				}
			}
		}
	}
}
