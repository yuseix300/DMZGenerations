package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.cache.object.GeoBone;

@Mixin(DMZSkinLayer.class)
public class DMZSkinLayerMixin {

    private static final float HAIR_BASE_EXCESS = 0.5f;

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleX(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateX(GeoBone bone, float value) {
        bone.setScaleX(dmzgenerations$grow(bone.getScaleX(), value));
    }

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleY(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateY(GeoBone bone, float value) {
        bone.setScaleY(dmzgenerations$grow(bone.getScaleY(), value));
    }

    @Redirect(method = "lambda$renderHair$0",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setScaleZ(F)V", ordinal = 0),
            remap = false, require = 0)
    private void dmzgenerations$inflateZ(GeoBone bone, float value) {
        bone.setScaleZ(dmzgenerations$grow(bone.getScaleZ(), value));
    }

    private static float dmzgenerations$grow(float orig, float value) {
        float excess = Math.max(0.0f, orig - 1.0f);
        return value + excess * HAIR_BASE_EXCESS;
    }
}
