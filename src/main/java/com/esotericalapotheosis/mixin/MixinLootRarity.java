package com.esotericalapotheosis.mixin;

import com.esotericalapotheosis.EsotericalApotheosis;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.color.GradientColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LootRarity.class, remap = false)
public class MixinLootRarity {

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void esotericalapotheosis$getGradientColor(CallbackInfoReturnable<net.minecraft.network.chat.TextColor> cir) {
        LootRarity self = (LootRarity) (Object) this;
        ResourceLocation id = RarityRegistry.INSTANCE.getKey(self);
        if (id != null) {
            GradientColor gradient = EsotericalApotheosis.GRADIENT_COLORS.get(id);
            if (gradient != null) {
                cir.setReturnValue(gradient);
            }
        }
    }

    @Inject(method = "toComponent", at = @At("HEAD"), cancellable = true)
    private void esotericalapotheosis$toComponent(CallbackInfoReturnable<Component> cir) {
        LootRarity self = (LootRarity) (Object) this;
        ResourceLocation id = RarityRegistry.INSTANCE.getKey(self);
        if (id != null) {
            GradientColor gradient = EsotericalApotheosis.GRADIENT_COLORS.get(id);
            if (gradient != null) {
                cir.setReturnValue(Component.translatable("rarity." + id).withStyle(Style.EMPTY.withColor(gradient)));
            }
        }
    }
}
