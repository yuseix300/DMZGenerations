package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.gui.MasterTextScreen;
import com.dragonminez.client.gui.buttons.TexturedTextButton;
import com.dragonminez.common.stats.StatsData;
import com.yuseix300.dmzgenerations.age.LifeStage;
import com.yuseix300.dmzgenerations.client.ClientAgeData;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import com.yuseix300.dmzgenerations.network.RebirthC2S;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MasterTextScreen.class)
public abstract class MasterTextScreenRebirthMixin extends Screen {

    private MasterTextScreenRebirthMixin(Component title) {
        super(title);
    }

    private static final ResourceLocation DMZGEN$BUTTONS =
            ResourceLocation.fromNamespaceAndPath("dragonminez", "textures/gui/buttons/characterbuttons.png");

    @Inject(method = "initDende", at = @At("TAIL"), remap = false)
    private void dmzgenerations$dendeRebirth(int x, int y, StatsData stats, CallbackInfo ci) {
        dmzgenerations$addRebirthButton(x, y);
    }

    @Inject(method = "initEnma", at = @At("TAIL"), remap = false)
    private void dmzgenerations$enmaRebirth(int x, int y, StatsData stats, CallbackInfo ci) {
        dmzgenerations$addRebirthButton(x, y);
    }

    @Unique
    private void dmzgenerations$addRebirthButton(int x, int y) {
        if (!ClientAgeData.hasData() || ClientAgeData.getStage() != LifeStage.ELDER) return;

        this.addRenderableWidget(new TexturedTextButton.Builder()
                .position(x, y - 24)
                .size(74, 20)
                .texture(DMZGEN$BUTTONS)
                .textureCoords(0, 28, 0, 48)
                .textureSize(74, 20)
                .message(Component.translatable("gui.dmzgenerations.button.rebirth"))
                .onPress(b -> {
                    GenNetwork.sendToServer(new RebirthC2S());
                    this.onClose();
                })
                .build());
    }
}
