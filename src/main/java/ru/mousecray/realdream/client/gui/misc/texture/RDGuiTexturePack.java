package ru.mousecray.realdream.client.gui.misc.texture;

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
public class RDGuiTexturePack {
    public static RDGuiTexturePack EMPTY = new RDGuiTexturePack(new HashMap<>());

    private final Map<IGuiButtonState, RDGuiTexture> textures;

    private RDGuiTexturePack(Map<IGuiButtonState, RDGuiTexture> textures) { this.textures = textures; }

    public RDGuiTexture getTexture(IGuiButtonState state)                 { return textures.get(state); }

    public RDGuiTexture getCalculatedTexture(GuiButtonActionState actionState, GuiButtonPersistentState persistentState) {
        RDGuiTexture texture;
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
        private final Map<IGuiButtonState, RDGuiTexture> textures = new HashMap<>();
        private final ResourceLocation                   baseTexture;
        private final IGuiVector                         textureSize;
        private final IGuiVector                         startPos;
        private final IGuiVector                         elementSize;

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
                    new RDGuiTexture(
                            baseTexture, textureSize,
                            new GuiVector(startPos.x(), startPos.y() + elementSize.y() * index),
                            elementSize
                    )
            );
            return this;
        }

        public RDGuiTexturePack build() { return new RDGuiTexturePack(textures); }
    }
}
