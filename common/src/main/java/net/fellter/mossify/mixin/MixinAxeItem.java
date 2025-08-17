package net.fellter.mossify.mixin;

import java.util.Map;
import java.util.Optional;

import net.fellter.mossify.MossifiableBlockRegistry;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
@Debug(export = true)
public abstract class MixinAxeItem extends Item {
	@Shadow
	private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		if (! context.getHand().equals(Hand.MAIN_HAND)) return false;
		assert playerEntity != null;
		return playerEntity.getOffHandStack().contains(DataComponentTypes.BLOCKS_ATTACKS)
				&& ! playerEntity.shouldCancelInteraction();
	}

	public MixinAxeItem(Settings settings) {
		super(settings);
	}

	@Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsageContext;getPlayer()Lnet/minecraft/entity/player/PlayerEntity;"), cancellable = true)
	private void fellter$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		Optional<BlockState> optional = fellter$getOptional(context.getWorld().getBlockState(context.getBlockPos()));
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();

		if (shouldCancelStripAttempt(context)) {
			cir.setReturnValue(ActionResult.PASS);
		} else {
			if (optional.isPresent() && playerEntity != null && playerEntity.isSneaking()) {
				ItemStack itemStack = context.getStack();
				if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
					Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, pos, itemStack);
				}

				world.setBlockState(pos, optional.get(), 11);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(playerEntity, optional.get()));
				world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
				Block.dropStack(world, pos, new ItemStack(Items.MOSS_BLOCK));

				itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));

				cir.setReturnValue(ActionResult.SUCCESS);
			}
		}
	}

	@Unique
	private Optional<BlockState> fellter$getOptional(BlockState state) {
		Map<Block, Block> map = MossifiableBlockRegistry.BLOCK_MAP.inverse();

		if (map.containsKey(state.getBlock())) {
			return Optional.ofNullable(map.get(state.getBlock()).getStateWithProperties(state));
		} else {
			return Optional.empty();
		}
	}
}
