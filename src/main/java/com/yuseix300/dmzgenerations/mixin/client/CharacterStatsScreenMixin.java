package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.gui.character.CharacterStatsScreen;
import com.yuseix300.dmzgenerations.client.ClientAgeData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = CharacterStatsScreen.class, remap = false)
public class CharacterStatsScreenMixin {

    @ModifyArg(
            method = "renderPlayerInfo",
            at = @At(value = "INVOKE",
                    target = "Lcom/dragonminez/client/util/TextUtil;renderAdvancedTooltip(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/network/chat/Component;Ljava/util/List;Ljava/util/List;I)V",
                    remap = false),
            index = 7,
            remap = false)
    private List<Component> dmzgenerations$addAgeToNameTooltip(List<Component> description) {
        if (description != null && ClientAgeData.hasData()) {
            description.add(Component.empty());
            description.add(Component.translatable("tooltip.dmzgenerations.age",
                    ClientAgeData.getAgeYears(),
                    Component.translatable(ClientAgeData.getStage().translationKey())));
            description.add(Component.translatable("tooltip.dmzgenerations.generation",
                    ClientAgeData.getGeneration()));
            description.add(Component.translatable("tooltip.dmzgenerations.buff",
                    String.format("%.2f", ClientAgeData.getGenerationMultiplier())));
        }
        return description;
    }
}
