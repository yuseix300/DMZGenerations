package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.age.LifeStage;
import com.yuseix300.dmzgenerations.client.ClientAgeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.cache.object.GeoBone;

@Mixin(DMZSkinLayer.class)
public class DMZSkinLayerMixin {

    private static final float HAIR_BASE_MARGIN = 0.55f;
    private static final float HAIR_BASE_PUSH = -0.05f;

    private static boolean dmzgenerations$logged = false;

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setPosZ(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$pushZ(GeoBone bone, float value) {
        float chibi = dmzgenerations$chibiHeadScale();
        bone.setPosZ(chibi <= 1.0f ? value : value - HAIR_BASE_PUSH);
    }

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleX(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateX(GeoBone bone, float value) {
        bone.setScaleX(dmzgenerations$shell(value));
    }

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleY(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateY(GeoBone bone, float value) {
        bone.setScaleY(dmzgenerations$shell(value));
    }

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleZ(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateZ(GeoBone bone, float value) {
        bone.setScaleZ(dmzgenerations$shell(value));
    }

    private static float dmzgenerations$shell(float value) {
        float chibi = dmzgenerations$chibiHeadScale();
        return chibi <= 1.0f ? value : chibi + HAIR_BASE_MARGIN;
    }

    private static float dmzgenerations$chibiHeadScale() {
        double age = ClientAgeData.renderAgeYears;
        float result = 1.0f;
        if (age >= 0.0 && LifeStage.fromAge(age) == LifeStage.CHILD) {
            GenerationsConfig cfg = GenerationsConfig.get();
            float bodyScale = cfg.modelScaleForAge(age);
            if (bodyScale != 1.0f) {
                float strength = cfg.chibiStrength(age);
                float fullChibi = (1.0f / bodyScale) * (float) cfg.chibiHeadScale;
                result = 1.0f + (fullChibi - 1.0f) * strength;
            }
        }
        if (!dmzgenerations$logged && age >= 0.0) {
            dmzgenerations$logged = true;
            DMZGenerations.LOGGER.info("[dmzgenerations] hair_base ACTIVE: age={}, chibiHeadScale={}",
                    age, result);
        }
        return result;
    }
}
