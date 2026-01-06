package ru.mousecray.realdream.client.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.dim.GuiVector;
import ru.mousecray.realdream.client.gui.dim.IGuiVector;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;
import ru.mousecray.realdream.client.gui.state.IGuiButtonState;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiTexturePack {
    public static GuiTexturePack EMPTY = new GuiTexturePack(new HashMap<>());

    private final Map<IGuiButtonState, GuiTexture> textures;

    private GuiTexturePack(Map<IGuiButtonState, GuiTexture> textures) { this.textures = textures; }

    public GuiTexture getTexture(IGuiButtonState state)               { return textures.get(state); }

    public GuiTexture getCalculatedTexture(GuiButtonActionState actionState, GuiButtonPersistentState persistentState) {
        GuiTexture texture;
        if (actionState != null) {
            texture = textures.get(actionState.combine(persistentState));
            if (texture == null) {
                texture = textures.get(actionState);
                if (texture == null) texture = textures.get(persistentState);
            }
        } else texture = textures.get(persistentState);

        return texture;
    }
    @SideOnly(Side.CLIENT)
    public static class Builder {
        private final Map<IGuiButtonState, GuiTexture> textures = new HashMap<>();
        private final ResourceLocation                 baseTexture;
        private final IGuiVector                       textureSize;
        private final IGuiVector                       startPos;
        private final IGuiVector                       elementSize;

        private Builder(ResourceLocation baseTexture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize) {
            this.baseTexture = baseTexture;
            this.textureSize = textureSize;
            this.startPos = startPos;
            this.elementSize = elementSize;
        }

        public static Builder create(ResourceLocation baseTexture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize) {
            return new Builder(baseTexture, textureSize, startPos, elementSize);
        }

        public Builder addTexture(IGuiButtonState state, int index) {
            textures.put(state,
                    new GuiTexture(
                            baseTexture, textureSize,
                            new GuiVector(startPos.x(), startPos.y() + elementSize.y() * index),
                            elementSize
                    )
            );
            return this;
        }

        public GuiTexturePack build() { return new GuiTexturePack(textures); }
    }
}
