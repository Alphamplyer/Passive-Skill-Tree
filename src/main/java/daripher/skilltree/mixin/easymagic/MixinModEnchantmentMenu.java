package daripher.skilltree.mixin.easymagic;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.SkillTreeEnchantmentMenu;
import fuzs.easymagic.mixin.accessor.EnchantmentMenuAccessor;
import fuzs.easymagic.world.inventory.ModEnchantmentMenu;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(ModEnchantmentMenu.class)
public class MixinModEnchantmentMenu {
	private @Shadow @Final DataSlot enchantmentSeed;

	@Redirect(method = "updateLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"))
	private int decreaseLevelRequirements(RandomSource random, int slot, int power, ItemStack itemStack) {
		var levelRequirement = EnchantmentHelper.getEnchantmentCost(random, slot, power, itemStack);
		var costsBeforeReduction = ((SkillTreeEnchantmentMenu) (Object) this).getCostsBeforeReduction();
		costsBeforeReduction[slot] = levelRequirement;
		var player = ((PlayerContainer) (Object) this).getPlayer().get();
		var decreasedRequirement = daripher.skilltree.enchantment.EnchantmentHelper.reduceLevelRequirement(levelRequirement, player);
		return decreasedRequirement;
	}

	@Inject(method = "createEnchantmentInstance", at = @At("RETURN"), cancellable = true, remap = false)
	private void amplifyEnchantments(ItemStack itemStack, int slot, CallbackInfoReturnable<List<EnchantmentInstance>> callbackInfo) {
		var costsBeforeReduction = ((SkillTreeEnchantmentMenu) (Object) this).getCostsBeforeReduction();
		var enchantments = ((EnchantmentMenuAccessor) this).callGetEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		var random = RandomSource.create(enchantmentSeed.get());
		var player = ((PlayerContainer) (Object) this).getPlayer().get();
		daripher.skilltree.enchantment.EnchantmentHelper.amplifyEnchantments(enchantments, random, player);
		callbackInfo.setReturnValue(enchantments);
	}
}
