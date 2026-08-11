package com.yuseix300.dmzgenerations.mixin.client;

import com.dragonminez.client.render.DMZPlayerRenderer;
import com.dragonminez.common.stats.StatsCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.client.ClientAgeData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Mixin(DMZPlayerRenderer.class)
public class DMZPlayerRendererMixin {

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void dmzgenerations$ageScale(PoseStack instance, float x, float y, float z,
                                         AbstractClientPlayer entity, float entityYaw, float partialTick,
                                         PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (GenerationsConfig.get().modelScalingEnabled && !isOozaru(entity)) {
            double age = renderAge(entity);
            if (age >= 0.0) {
                float scale = GenerationsConfig.get().modelScaleForAge(age);
                x *= scale;
                y *= scale;
                z *= scale;
            }
        }
        instance.scale(x, y, z);
    }

    @Inject(method = "preRender", at = @At("HEAD"), remap = false)
    private void dmzgenerations$chibiHead(PoseStack poseStack, AbstractClientPlayer animatable, BakedGeoModel model,
                                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                          float partialTick, int packedLight, int packedOverlay,
                                          float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!GenerationsConfig.get().modelScalingEnabled) return;

        float headScale = 1.0f;
        double age = renderAge(animatable);
        if (!isOozaru(animatable) && age >= 0.0
                && com.yuseix300.dmzgenerations.age.LifeStage.fromAge(age) == com.yuseix300.dmzgenerations.age.LifeStage.CHILD) {
            float scale = GenerationsConfig.get().modelScaleForAge(age);
            if (scale != 1.0f) {
                float chibiStrength = GenerationsConfig.get().chibiStrength(age);
                float fullChibi = (1.0f / scale) * (float) GenerationsConfig.get().chibiHeadScale;
                headScale = 1.0f + (fullChibi - 1.0f) * chibiStrength;
            }
        }

        float finalHeadScale = headScale;
        model.getBone("head").ifPresent(bone -> setScale(bone, finalHeadScale));
    }

    @org.spongepowered.asm.mixin.Unique
    private static double renderAge(AbstractClientPlayer entity) {
        if (com.yuseix300.dmzgenerations.client.CreationAgePreview.active) {
            return com.yuseix300.dmzgenerations.client.CreationAgePreview.previewAge();
        }
        return ClientAgeData.ageOf(entity.getId());
    }

    private static void setScale(software.bernie.geckolib.cache.object.GeoBone bone, float scale) {
        bone.setScaleX(scale);
        bone.setScaleY(scale);
        bone.setScaleZ(scale);
    }

    private static boolean isOozaru(AbstractClientPlayer entity) {
        return entity.getCapability(StatsCapability.INSTANCE)
                .map(stats -> stats.getCharacter().getRenderLogicKey().toLowerCase().startsWith("oozaru"))
                .orElse(false);
    }
}
