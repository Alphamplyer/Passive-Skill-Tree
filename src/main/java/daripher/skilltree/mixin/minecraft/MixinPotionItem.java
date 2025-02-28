package daripher.skilltree.mixin.minecraft;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.potion.PotionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

@Mixin(PotionItem.class)
public class MixinPotionItem extends Item {
	private MixinPotionItem() {
		super(null);
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (PotionHelper.isMixture(itemStack)) return getMixtureName(itemStack);
		if (PotionHelper.isSuperiorPotion(itemStack)) return getSuperiorPotionName(itemStack);
		return super.getName(itemStack);
	}

	protected Component getMixtureName(ItemStack itemStack) {
		String mixtureId = getMixtureId(itemStack);
		MutableComponent translatedName = Component.translatable(mixtureId);
		// no special name
		if (translatedName.getString().equals(mixtureId)) {
			return Component.translatable(getDescriptionId() + ".mixture");
		}
		return translatedName;
	}

	protected Component getSuperiorPotionName(ItemStack stack) {
		Component potionName = super.getName(stack);
		return Component.translatable("potion.superior", potionName);
	}

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void addAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo callbackInfo) {
		if (tooltipFlag != TooltipFlag.Default.ADVANCED) return;
		addAdvancedTooltip(itemStack, level, components);
	}

	private void addAdvancedTooltip(ItemStack itemStack, Level level, List<Component> components) {
		if (PotionHelper.isMixture(itemStack)) {
			// formatter:off
			PotionUtils.getMobEffects(itemStack).stream()
				.map(MobEffectInstance::getEffect)
				.map(MobEffect::getDescriptionId)
				.map(s -> s.replaceAll("effect.", ""))
				.map(Component::literal)
				.map(c -> c.withStyle(ChatFormatting.DARK_GRAY))
				.forEach(components::add);
			// formatter:on
		}
	}

	protected String getMixtureId(ItemStack itemStack) {
		var name = new StringBuilder(getDescriptionId() + ".mixture");
		// formatter:off
		PotionUtils.getMobEffects(itemStack).stream()
			.map(MobEffectInstance::getEffect)
			.map(MobEffect::getDescriptionId)
			.map(id -> id.replaceAll("effect.", ""))
			.forEach(id -> name.append("." + id));
		// formatter:on
		return name.toString();
	}
}
