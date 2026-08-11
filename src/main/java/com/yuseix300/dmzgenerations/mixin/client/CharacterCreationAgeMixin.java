package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.gui.buttons.CustomTextureButton;
import com.dragonminez.client.gui.character.CharacterCustomizationScreen;
import com.dragonminez.client.util.TextUtil;
import com.yuseix300.dmzgenerations.age.LifeStage;
import com.yuseix300.dmzgenerations.client.CreationAgePreview;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import com.yuseix300.dmzgenerations.network.SetStartAgeC2S;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CharacterCustomizationScreen.class)
public abstract class CharacterCreationAgeMixin extends Screen {

    private CharacterCreationAgeMixin(Component title) {
        super(title);
    }

    private static final ResourceLocation DMZGEN$BUTTONS =
            ResourceLocation.fromNamespaceAndPath("dragonminez", "textures/gui/buttons/characterbuttons.png");

    @Unique private static final int DMZGEN$AGE_ROW_Y = 186;

    @Shadow(remap = false) @Final private Screen previousScreen;

    @Inject(method = "initAuraClassTab", at = @At("TAIL"), remap = false)
    private void dmzgenerations$addAgeButtons(int top, CallbackInfo ci) {
        if (this.previousScreen == null) return;
        int y = top + DMZGEN$AGE_ROW_Y;
        this.addRenderableWidget(dmzgenerations$arrow(30, y, true, b -> dmzgenerations$cycle(-1)));
        this.addRenderableWidget(dmzgenerations$arrow(125, y, false, b -> dmzgenerations$cycle(1)));
    }

    @Inject(method = "renderAuraClassText", at = @At("TAIL"), remap = false)
    private void dmzgenerations$renderAge(GuiGraphics graphics, int centerX, int top, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.previousScreen == null) return;
        LifeStage stage = LifeStage.values()[CreationAgePreview.stageIndex];
        Component label = Component.literal("AGE: ").withStyle(ChatFormatting.AQUA)
                .append(Component.translatable(stage.translationKey()).withStyle(ChatFormatting.YELLOW));
        TextUtil.drawCenteredStringWithBorder(graphics, this.font, label, centerX, top + DMZGEN$AGE_ROW_Y + 4, 0xFFFFFF);
    }

    @Inject(method = "renderPlayerModel", at = @At("HEAD"), remap = false)
    private void dmzgenerations$previewOn(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        if (this.previousScreen != null) CreationAgePreview.active = true;
    }

    @Inject(method = "renderPlayerModel", at = @At("RETURN"), remap = false)
    private void dmzgenerations$previewOff(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        CreationAgePreview.active = false;
    }

    @Inject(method = "finish", at = @At("TAIL"), remap = false)
    private void dmzgenerations$sendStartAge(CallbackInfo ci) {
        if (this.previousScreen != null) {
            GenNetwork.sendToServer(new SetStartAgeC2S(CreationAgePreview.stageIndex));
        }
        CreationAgePreview.active = false;
    }

    @Unique
    private void dmzgenerations$cycle(int delta) {
        int count = LifeStage.values().length;
        CreationAgePreview.stageIndex = Math.max(0, Math.min(count - 1, CreationAgePreview.stageIndex + delta));
    }

    @Unique
    private CustomTextureButton dmzgenerations$arrow(int x, int y, boolean isLeft, CustomTextureButton.OnPress onPress) {
        return new CustomTextureButton.Builder()
                .position(x, y)
                .size(10, 15)
                .texture(DMZGEN$BUTTONS)
                .textureCoords(isLeft ? 32 : 20, 0, isLeft ? 32 : 20, 14)
                .textureSize(8, 14)
                .message(Component.empty())
                .onPress(onPress)
                .build();
    }
}
