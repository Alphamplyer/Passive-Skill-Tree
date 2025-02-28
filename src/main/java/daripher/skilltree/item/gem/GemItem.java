package daripher.skilltree.item.gem;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

public abstract class GemItem extends Item {
	public GemItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) {
				components.add(Component.translatable("gem.disabled").withStyle(ChatFormatting.RED));
				return;
			}
		}
		MutableComponent gemTooltip = Component.translatable("gem.tooltip").withStyle(ChatFormatting.YELLOW);
		components.add(gemTooltip);
		appendBonusesTooltip(stack, components);
	}

	public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
		return !GemHelper.hasGem(stack, socket);
	}

	public void insertInto(Player player, ItemStack itemStack, ItemStack gemStack, int gemSlot, double gemPower) {
		GemHelper.insertGem(player, itemStack, gemStack, gemSlot, gemPower);
	}

	public static MutableComponent formatGemClass(String gemClass) {
		return Component.translatable("gem_class_format", Component.translatable("gem_class." + gemClass));
	}

	public abstract Optional<Pair<Attribute, AttributeModifier>> getGemBonus(Player player, ItemStack itemStack, ItemStack gemStack);

	protected abstract void appendBonusesTooltip(ItemStack stack, List<Component> components);
}
