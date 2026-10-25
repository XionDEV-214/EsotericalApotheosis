package com.esotericalapotheosis.mixin;

import com.ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AncientReforgingTableBlock.class, remap = false)
public class MixinAncientReforgingTableBlock {

    @Inject(method = "getMaxRarity", at = @At("HEAD"), cancellable = true)
    private void esotericalapotheosis$getMaxRarity(CallbackInfoReturnable<LootRarity> cir) {
        cir.setReturnValue(RarityRegistry.getMaxRarity().get());
    }

    @Inject(method = "m_5871_", at = @At("RETURN"))
    private void esotericalapotheosis$appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (tooltip.size() >= 2) {
            tooltip.remove(tooltip.size() - 1);
            tooltip.remove(tooltip.size() - 1);
        }
        tooltip.add(Component.translatable("tooltip.esotericalapotheosis.reforge_table.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.esotericalapotheosis.reforge_table.desc2", RarityRegistry.getMaxRarity().get().toComponent()).withStyle(ChatFormatting.GRAY));
    }
}
