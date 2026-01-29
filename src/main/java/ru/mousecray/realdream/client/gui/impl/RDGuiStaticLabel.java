package ru.mousecray.realdream.client.gui.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.RDFontSize;
import ru.mousecray.realdream.client.gui.RDGuiLabel;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.event.RDGuiMouseClickEvent;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RDGuiStaticLabel extends RDGuiLabel<RDGuiStaticLabel> {
    public RDGuiStaticLabel(String text, FontRenderer fontRenderer, GuiShape elementShape, int color, RDFontSize fontSize, @Nullable SoundEvent soundClick) {
        super(text, fontRenderer, elementShape, color, fontSize, soundClick);
    }

    public RDGuiStaticLabel(String text, FontRenderer fontRenderer, GuiShape elementShape, int color, RDFontSize fontSize) {
        this(text, fontRenderer, elementShape, color, fontSize, null);
    }

    @Override
    public void onClick(RDGuiMouseClickEvent<RDGuiStaticLabel> event) {
        // Static label usually does nothing on click
    }
}
