/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.api.client.control;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.control.base.MGuiBaseTextField;
import ru.mousecray.mouseproject.api.client.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.dim.GuiVector;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.utils.MStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiSimpleField extends MGuiBaseTextField<MGuiSimpleField> {
    public MGuiSimpleField(GuiShape shape, MGuiString placeholder) {
        super(shape, placeholder);
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE,
                        GuiVector.of(104, 0), GuiVector.of(80, 10)
                )
                .addTexture(0)
                .build());
    }
}