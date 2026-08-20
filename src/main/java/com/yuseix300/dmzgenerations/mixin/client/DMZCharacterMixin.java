package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.common.stats.character.Character;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.client.ClientAgeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Character.class, remap = false)
public abstract class DMZCharacterMixin {

    @Shadow private float[] rgbHairColor;

    private static final float[] GRAY = {0.78f, 0.78f, 0.80f};
    private static boolean dmzgenerations$logged = false;

    @org.spongepowered.asm.mixin.Unique
    private float dmzgenerations$grayStrength() {
        double age = com.yuseix300.dmzgenerations.client.AgeCharacterLink.ageFor(
                (com.dragonminez.common.stats.character.Character) (Object) this);
        return GenerationsConfig.get().grayHairStrength(age);
    }

    @Inject(method = "getRgbHairColor", at = @At("HEAD"))
    private void dmzgenerations$grayHead(CallbackInfoReturnable<float[]> cir) {
        if (dmzgenerations$grayStrength() > 0.0f) {
            this.rgbHairColor = null;
        }
    }

    @Inject(method = "getRgbHairColor", at = @At("RETURN"), cancellable = true)
    private void dmzgenerations$grayReturn(CallbackInfoReturnable<float[]> cir) {
        float g = dmzgenerations$grayStrength();
        if (g <= 0.0f) return;
        float[] rgb = cir.getReturnValue();
        if (rgb == null || rgb.length < 3) return;
        cir.setReturnValue(new float[]{
                rgb[0] + (GRAY[0] - rgb[0]) * g,
                rgb[1] + (GRAY[1] - rgb[1]) * g,
                rgb[2] + (GRAY[2] - rgb[2]) * g
        });
    }
}
