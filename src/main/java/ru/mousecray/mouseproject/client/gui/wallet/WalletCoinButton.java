/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.client.gui.wallet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.api.client.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.control.base.MGuiSelectableButton;
import ru.mousecray.mouseproject.api.client.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.dim.layout.GuiScaleType;
import ru.mousecray.mouseproject.api.client.event.GuiTickEvent;
import ru.mousecray.mouseproject.common.economy.CoinValue;

import javax.annotation.Nonnull;

public class WalletCoinButton extends MGuiSelectableButton<WalletCoinButton> {
    private       CoinValue coinValue;
    private final String    cachedName;

    public WalletCoinButton(GuiShape elementShape, CoinValue coinValue) {
        super(elementShape, MGuiString.simple(coinValue.getFormattedValue(CoinValue.FormatType.SHORT)));
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        GuiScreenWallet.TEXTURES, GuiScreenWallet.TEXTURES_SIZE,
                        GuiVector.of(230, 0), GuiVector.of(10, 13)
                )
                .addTexture(0)
                .addTexture(1, MGuiElementState.HOVERED)
                .addTexture(2, MGuiElementState.PRESSED)
                .addTexture(3, MGuiElementState.SELECTED)
                .addTexture(4, MGuiElementState.SELECTED, MGuiElementState.HOVERED)
                .addTexture(5, MGuiElementState.SELECTED, MGuiElementState.PRESSED)
                .build()
        );
        this.coinValue = coinValue;
        cachedName = new ItemStack(coinValue.getType().getItem(), 1).getDisplayName();
        setTextOffset(GuiVector.of(0, getShape().height() / 3f));
        int length = coinValue.getFormattedValue(CoinValue.FormatType.SHORT).length();
        if (length > 4) setTextScaleMultiplayer((float) Math.max(0.5, 4d / length));
        setScaleRules(new GuiScaleRules(GuiScaleType.ORIGIN_VERTICAL));
    }

    public void setCount(CoinValue count) {
        coinValue = count;
        String formattedValue = coinValue.getFormattedValue(CoinValue.FormatType.SHORT);
        int    length         = formattedValue.length();
        if (length > 4) setTextScaleMultiplayer((float) Math.max(0.5, 4d / length));
        setText(formattedValue);
    }

    public CoinValue getCount() { return coinValue; }

    @Override
    public void onDrawForeground(@Nonnull GuiTickEvent<WalletCoinButton> event) {
        super.onDrawForeground(event);
        float width           = getCalculatedShape().width();
        float height          = getCalculatedShape().height();
        float x               = getCalculatedShape().x();
        float y               = getCalculatedShape().y();
        float partialTicks    = event.getPartialTick();
        float itemDefaultSize = 16.0f;
        float scale           = Math.min(width / itemDefaultSize, height / itemDefaultSize) / 1.5f;

        float sizeX = x + (width - itemDefaultSize * scale) / 2;
        float sizeY = y + (height - itemDefaultSize * scale) / 4f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(sizeX + scale * 13f / 1.6f, sizeY + scale * 14f / 1.7f, 150.0F);
        GlStateManager.scale(scale * 13f, scale * 14f, scale * 13f);

        MGuiRenderHelper.enableBrightItemLighting();
        GlStateManager.enableRescaleNormal();

        if (coinValue != null) {
            if (getStateManager().has(MGuiElementState.HOVERED)) {
                GlStateManager.translate(0, 0, 0);
                GlStateManager.scale(1.2f, 1.2f, 1.2f);
                float rotationAngle = ((System.currentTimeMillis() % 2000) / 2000.0f) * 360.0f;
                rotationAngle += partialTicks * 9.0f;
                GlStateManager.rotate(rotationAngle, 0.0f, 1.0f, 0.0f);
            }
            GlStateManager.enableDepth();
            event.getMc().getRenderItem().renderItem(
                    new ItemStack(coinValue.getType().getItem()),
                    ItemCameraTransforms.TransformType.FIXED
            );
            GlStateManager.disableDepth();
        }

        MGuiRenderHelper.disableBrightItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    public void onDrawLast(@Nonnull GuiTickEvent<WalletCoinButton> event) {
        super.onDrawLast(event);
        drawButtonTooltip(event);
    }

    private final ScaledResolution sc = new ScaledResolution(Minecraft.getMinecraft());

    protected void drawButtonTooltip(GuiTickEvent<WalletCoinButton> event) {
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();

        if (mouseHover(event.getMc(), mouseX, mouseY)) {
            if (cachedName != null && !cachedName.isEmpty()) {
                GlStateManager.pushMatrix();

                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                MGuiRenderHelper.drawTooltip(cachedName + " " + coinValue, event.getMc(), mouseX, mouseY, getFontSize(), sc);

                GlStateManager.popMatrix();
            }
        }
    }
}