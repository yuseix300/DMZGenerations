package com.yuseix300.dmzgenerations.client;

import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.stats.StatsCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AgeHudOverlay implements IGuiOverlay {

    public static final AgeHudOverlay INSTANCE = new AgeHudOverlay();

    private static final float HUD_BASE_SCALE = 2.25f;
    private static final float HUD_BASE_WIDTH = 184.0f;

    private static final float HUD_LOCAL_X = 32.0f;
    private static final float HUD_LOCAL_Y = 09.0f;
    private static final float TEXT_SCALE = 0.5f;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        if (mc.options.hideGui || mc.options.renderDebug || mc.player == null) return;
        if (!ClientAgeData.hasData()) return;

        if (ConfigManager.getUserConfig().getAlternativeHud()) return;
        boolean hasCharacter = mc.player.getCapability(StatsCapability.INSTANCE)
                .map(data -> data.getStatus().isHasCreatedCharacter())
                .orElse(false);
        if (!hasCharacter) return;

        int anchorX = ConfigManager.getUserConfig().getXenoverseHudPosX();
        int anchorY = ConfigManager.getUserConfig().getXenoverseHudPosY();
        float userScale = ConfigManager.getUserConfig().getXenoverseHudScale();
        float finalScale = Math.min(HUD_BASE_SCALE * userScale, (screenWidth * 0.5f) / HUD_BASE_WIDTH);

        Component text = Component.translatable(
                "hud.dmzgenerations.age",
                ClientAgeData.getAgeYears(),
                Component.translatable(ClientAgeData.getStage().translationKey()));

        graphics.pose().pushPose();
        graphics.pose().translate(anchorX, anchorY, 0);
        graphics.pose().scale(finalScale, finalScale, 1.0f);

        graphics.pose().pushPose();
        graphics.pose().translate(HUD_LOCAL_X, HUD_LOCAL_Y, 0);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0f);
        graphics.drawString(mc.font, text, 0, 0, 0xFFFFFF);
        graphics.pose().popPose();

        graphics.pose().popPose();
    }
}
