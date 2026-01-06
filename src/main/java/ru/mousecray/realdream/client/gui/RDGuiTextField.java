package ru.mousecray.realdream.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.event.*;
import ru.mousecray.realdream.client.gui.state.GuiButtonActionState;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class RDGuiTextField<T extends RDGuiTextField<T>> extends GuiTextField implements RDGuiElement<T> {
    private static final int CURSOR_RECT_COLOR      = -3092272;
    private static final int PLACEHOLDER_TEXT_COLOR = 0x686868;

    private final RDGuiTickEvent<T>
            updateEvent   = new RDGuiTickEvent<>(),
            drawBGEvent   = new RDGuiTickEvent<>(),
            drawFGEvent   = new RDGuiTickEvent<>(),
            drawLastEvent = new RDGuiTickEvent<>(),
            drawTextEvent = new RDGuiTickEvent<>();
    private final RDGuiMouseClickEvent<T>
            pressEvent   = new RDGuiMouseClickEvent<>(RDClickType.PRESS),
            releaseEvent = new RDGuiMouseClickEvent<>(RDClickType.RELEASE),
            clickEvent   = new RDGuiMouseClickEvent<>(RDClickType.CLICK);
    private final   RDGuiMouseMoveEvent<T> moveEvent      = new RDGuiMouseMoveEvent<>();
    private final   RDGuiTextTypedEvent<T> textTypedEvent = new RDGuiTextTypedEvent<>();
    private final   RDGuiSoundEvent<T>     soundEvent     = new RDGuiSoundEvent<>();
    private final   GuiShape               elementShape;
    private         GuiShape               calculatedElementShape;
    protected final RDFontSize             fontSize;

    protected StateColorContainer colorContainer = StateColorContainer.Builder
            .create(14737632)
            .addState(GuiButtonPersistentState.DISABLED, 7368816)
            .build();

    @Nullable private GuiButtonActionState     actionState     = null;
    @Nullable private GuiButtonPersistentState persistentState = GuiButtonPersistentState.NORMAL;
    private final     GuiTexturePack           texturePack;

    private                 int        partialTick;
    @Nullable private final SoundEvent soundClick;
    @Nullable private       String     placeholder;
    private                 boolean    hovered;

    public RDGuiTextField(FontRenderer fontRenderer,
                          @Nullable String placeholder, @Nullable String defaultText,
                          GuiShape elementShape,
                          @Nullable GuiTexturePack texturePack,
                          @Nullable SoundEvent soundClick, RDFontSize fontSize) {
        super(0, fontRenderer, (int) elementShape.getX(), (int) elementShape.getY(), (int) elementShape.getWidth(), (int) elementShape.getHeight());
        this.elementShape = elementShape;
        this.fontSize = fontSize;
        if (defaultText == null) defaultText = "";
        setText(defaultText);
        setMaxStringLength(999);
        setEnableBackgroundDrawing(true);
        this.placeholder = placeholder;
        this.texturePack = texturePack == null ? GuiTexturePack.EMPTY : texturePack;
        this.soundClick = soundClick;
    }

    @Override public void setId(int id)                      { this.id = id; }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public GuiShape getDrawShape()                 { return elementShape; }
    @Override public GuiShape getElementShape()              { return elementShape; }
    @Override public GuiShape getCalculatedDrawShape()       { return calculatedElementShape; }
    @Override public GuiShape getCalculatedElementShape()    { return calculatedElementShape; }
    @Override public GuiVector getPos()                      { return elementShape.getPos(); }
    @Override public GuiVector getSize()                     { return elementShape.getSize(); }
    @Nullable public String getPlaceholder()                 { return placeholder; }
    public void setPlaceholder(@Nullable String placeholder) { this.placeholder = placeholder; }

    @Override
    public void calculate(GuiVector defaultSize, GuiVector contentSize) {
        //TODO:
    }

    @Override @Nullable public GuiButtonActionState getActionState()         { return actionState; }
    @Override @Nullable public GuiButtonPersistentState getPersistentState() { return persistentState; }
    @Override public GuiTexturePack getTexturePack()                         { return texturePack; }

    @Override
    public boolean applyState(@Nullable GuiButtonPersistentState state) {
        processVanillaPersistentState(state);
        persistentState = state;
        return true;
    }

    @Override
    public final void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
        super.setEnableBackgroundDrawing(true);
    }

    protected boolean applyActionState(@Nullable GuiButtonActionState state) {
        processVanillaActionState(state);
        actionState = state;
        return true;
    }

    private void processVanillaActionState(@Nullable GuiButtonActionState state) {

    }

    private void processVanillaPersistentState(@Nullable GuiButtonPersistentState state) {
        setEnabled(state != GuiButtonPersistentState.DISABLED);
        setVisible(state != null);
    }

    protected void onAnyEventFire(RDGuiEvent<T> event) { }

    @Override protected final void onUpdate0(Minecraft mc, int mouseX, int mouseY) {
        if (++partialTick >= 20) partialTick = 0;

        RDGuiEventFactory.pushTickEvent(updateEvent, self(), mc, mouseX, mouseY, partialTick);
        onAnyEventFire(updateEvent);
        if (!updateEvent.isCancelled()) onUpdate(updateEvent);
        int           diffX     = mouseX - moveEvent.getMouseX();
        int           diffY     = mouseY - moveEvent.getMouseY();
        MoveDirection direction = MoveDirection.getMoveDirection(diffX, diffY);
        RDGuiEventFactory.pushMouseMoveEvent(moveEvent, self(), mc, mouseX, mouseY, direction);
    }


    protected final void onKeyTyped0(char typedChar, int keyCode) {
        textboxKeyTyped(typedChar, keyCode);
    }

    @Override protected final void onMouseEnter0(Minecraft mc, int mouseX, int mouseY) {
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            if (persistentState == null
                    || persistentState == GuiButtonPersistentState.DISABLED
                    || actionState == GuiButtonActionState.PRESSED) return;
            hovered = true;
            applyActionState(GuiButtonActionState.HOVER);
            onMouseEnter(moveEvent);
        }
    }

    @Override protected final void onMouseLeave0(Minecraft mc, int mouseX, int mouseY) {
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            if (actionState == GuiButtonActionState.HOVER) applyActionState(null);
            hovered = false;
            onMouseLeave(moveEvent);
        }
    }

    @Override protected final void onMouseReleased0(Minecraft mc, int mouseX, int mouseY) {
        if (persistentState != null) {
            RDGuiEventFactory.pushMouseClickEvent(releaseEvent, self(), mc, mouseX, mouseY);
            onAnyEventFire(releaseEvent);
            if (!releaseEvent.isCancelled()) {
                if (isMouseOver()) applyActionState(GuiButtonActionState.HOVER);
                else applyActionState(null);
                onMouseReleased(releaseEvent);
                if (isMouseOver()) {
                    RDGuiEventFactory.pushMouseClickEvent(clickEvent, self(), mc, mouseX, mouseY);
                    onAnyEventFire(clickEvent);
                    if (!clickEvent.isCancelled()) onClick(clickEvent);
                }
            }
        }
    }

    @Override protected final void onMousePressed0(Minecraft mc, int mouseX, int mouseY) {
        RDGuiEventFactory.pushMouseClickEvent(pressEvent, self(), mc, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            if (persistentState == null
                    || persistentState == GuiButtonPersistentState.DISABLED) return;
            applyActionState(GuiButtonActionState.PRESSED);
            onMousePressed(pressEvent);
            onPlaySound0(mc, mc.getSoundHandler(), soundClick, SoundSourceType.PRESS);
            super.mouseClicked(mouseX, mouseY, 0);
        }
    }

    protected final void onPlaySound0(Minecraft mc, SoundHandler soundHandler, @Nullable SoundEvent sound, SoundSourceType source) {
        if (sound != null) {
            RDGuiEventFactory.pushSoundEvent(soundEvent, self(), mc, moveEvent.getMouseX(), moveEvent.getMouseY(), soundHandler, sound, source);
            onAnyEventFire(soundEvent);
            if (!soundEvent.isCancelled()) onPlaySound(soundEvent);
        }
    }

    protected void onPlaySound(RDGuiSoundEvent<T> event) {
        event.getHandler().playSound(PositionedSoundRecord.getMasterRecord(event.getSound(), 1.0F));
    }

    public void mouseReleased(int mouseX, int mouseY)                 { onMouseReleased0(Minecraft.getMinecraft(), mouseX, mouseY); }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) { }

    @Override public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    @Override
    public final boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    protected void onClick(RDGuiMouseClickEvent<T> event) { }

    public final void drawTextBox(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        onDrawTextBox(mc, mouseX, mouseY, partialTicks);
    }

    protected final void onDrawTextBoxBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawBGEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (persistentState != null && !drawBGEvent.isCancelled()) drawTextBoxBackgroundLayer(drawBGEvent);
    }

    protected final void onDrawTextBoxForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawFGEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (persistentState != null && !drawFGEvent.isCancelled()) drawTextBoxForegroundLayer(drawFGEvent);
    }

    protected final void onDrawTextBoxText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawTextEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (persistentState != null && !drawTextEvent.isCancelled()) drawTextBoxTextLayer(drawTextEvent);
    }

    protected final void onDrawTextBoxLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        RDGuiEventFactory.pushTickEvent(drawLastEvent, self(), mc, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (persistentState != null && !drawLastEvent.isCancelled()) drawTextBoxLastLayer(drawLastEvent);
    }

    @Override
    public final void drawTextBox() {
        onDrawTextBox(Minecraft.getMinecraft(), moveEvent.getMouseX(), moveEvent.getMouseY(), Minecraft.getMinecraft().getRenderPartialTicks());
    }

    protected final void onDrawTextBox(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        onDrawTextBoxBackground(mc, mouseX, mouseY, partialTicks);
        onDrawTextBoxForeground(mc, mouseX, mouseY, partialTicks);
        onDrawTextBoxText(mc, mouseX, mouseY, partialTicks);
        onDrawTextBoxLast(mc, mouseX, mouseY, partialTicks);
    }

    protected void onUpdate(RDGuiTickEvent<T> event)              { }
    protected void onMouseReleased(RDGuiMouseClickEvent<T> event) { }
    protected void onMouseEnter(RDGuiMouseMoveEvent<T> event)     { }
    protected void onMouseLeave(RDGuiMouseMoveEvent<T> event)     { }
    protected void onMousePressed(RDGuiMouseClickEvent<T> event)  { }

    protected final int getHoverState(boolean mouseOver) {
        return persistentState == GuiButtonPersistentState.DISABLED ? 0 : mouseOver ? 2 : 1;
    }

    public final boolean isMouseOver() { return hovered; }

    public final void drawTextBoxForegroundLayer(int mouseX, int mouseY) {
        RDGuiEventFactory.pushTickEvent(drawFGEvent, self(), Minecraft.getMinecraft(), mouseX, mouseY, Minecraft.getMinecraft().getRenderPartialTicks());
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) drawTextBoxForegroundLayer(drawFGEvent);
    }

    protected void drawTextBoxForegroundLayer(RDGuiTickEvent<T> event) {
        if (persistentState == null) return;

        float scale        = fontSize.getScale();
        float inverseScale = 1.0F / scale;
        if (fontSize != RDFontSize.NORMAL) {
            scale = fontSize.getScale();
            inverseScale = 1.0F / scale;
        }

        FontRenderer fontRenderer    = event.getMc().fontRenderer;
        int          textColor       = colorContainer.getCalculatedColor(actionState, persistentState, 0);
        String       visibleText     = getText();
        int          cursorPos       = getCursorPosition() - lineScrollOffset;
        float        textX           = (x + width / 35f) * inverseScale;
        float        textY           = y * inverseScale + height * inverseScale / 2f * inverseScale - (fontRenderer.FONT_HEIGHT + 2) * inverseScale / 2f * inverseScale;
        int          selectionEndPos = getSelectionEnd() - lineScrollOffset;


        GlStateManager.pushMatrix();
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GlStateManager.scale(scale, scale, 1.0F);

        //noinspection ConstantValue
        if (getText() != null && getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
            GuiRenderHelper.drawString(fontRenderer, placeholder, textX, textY, PLACEHOLDER_TEXT_COLOR, fontSize != RDFontSize.SMALL);
        }

        if (selectionEndPos > visibleText.length()) selectionEndPos = visibleText.length();

        boolean showCursor    = isFocused() && partialTick % 20 < 10 && cursorPos >= 0 && cursorPos <= visibleText.length();
        boolean isCursorAtEnd = getCursorPosition() < getText().length() || getText().length() >= getMaxStringLength();

        float cursorX = textX;
        if (!visibleText.isEmpty()) {
            String textBeforeCursor = cursorPos >= 0 && cursorPos <= visibleText.length()
                    ? visibleText.substring(0, cursorPos)
                    : visibleText;
            cursorX = fontRenderer.getStringWidth(textBeforeCursor) + textX;
        }

        if (cursorPos < 0) cursorX = textX;
        else if (cursorPos > visibleText.length()) cursorX = textX + getWidth();
        else if (isCursorAtEnd) cursorX--;

        if (showCursor) {
            if (isCursorAtEnd) {
                GuiRenderHelper.drawRect(cursorX, textY - 1, cursorX + 1, textY + 1 + fontRenderer.FONT_HEIGHT, CURSOR_RECT_COLOR);
            } else {
                GuiRenderHelper.drawString(fontRenderer, "|", cursorX, textY, textColor, fontSize != RDFontSize.SMALL);
            }
        }

        if (selectionEndPos != cursorPos) {
            float selectionEndX = textX + fontRenderer.getStringWidth(visibleText.substring(0, selectionEndPos));
            drawSelectionBox(cursorX, textY - 1, selectionEndX - 1, textY + 1 + fontRenderer.FONT_HEIGHT);
        }

        GlStateManager.popMatrix();
    }

    protected void drawTextBoxLastLayer(RDGuiTickEvent<T> event) { }

    protected void drawTextBoxBackgroundLayer(RDGuiTickEvent<T> event) {
        GuiTexture texture = texturePack.getCalculatedTexture(actionState, persistentState);

        if (texture != null) {
            texture.draw(
                    event.getMc(),
                    elementShape.getX(), elementShape.getY(),
                    elementShape.getWidth(), elementShape.getHeight()
            );
        }
    }

    protected void drawTextBoxTextLayer(RDGuiTickEvent<T> event) {
        if (persistentState == null) return;

        float scale        = fontSize.getScale();
        float inverseScale = 1.0F / scale;
        if (fontSize != RDFontSize.NORMAL) {
            scale = fontSize.getScale();
            inverseScale = 1.0F / scale;
        }

        FontRenderer fontRenderer = event.getMc().fontRenderer;
        int          textColor    = colorContainer.getCalculatedColor(actionState, persistentState, 0);
        String       visibleText  = fontRenderer.trimStringToWidth(getText().substring(lineScrollOffset), getWidth());
        float        textX        = (x + width / 35f) * inverseScale;
        float        textY        = y * inverseScale + height * inverseScale / 2f * inverseScale - (fontRenderer.FONT_HEIGHT + 2) * inverseScale / 2f * inverseScale;
        int          cursorPos    = getCursorPosition() - lineScrollOffset;

        if (!visibleText.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GlStateManager.scale(scale, scale, 1.0F);
            String textBeforeCursor = cursorPos >= 0 && cursorPos <= visibleText.length()
                    ? visibleText.substring(0, cursorPos)
                    : visibleText;
            float currentX = GuiRenderHelper.drawString(fontRenderer, textBeforeCursor, textX, textY, textColor, fontSize != RDFontSize.SMALL);

            if (cursorPos >= 0 && cursorPos < visibleText.length()) {
                GuiRenderHelper.drawString(fontRenderer, visibleText.substring(cursorPos), currentX, textY, textColor, fontSize != RDFontSize.SMALL);
            }
            GlStateManager.popMatrix();
        }
    }

    private void drawSelectionBox(float startX, float startY, float endX, float endY) {
        if (startX < endX) {
            float i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            float j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > x + width) endX = x + width;
        if (startX > x + width) startX = x + width;

        Tessellator   tessellator   = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(startX, endY, 0.0D).endVertex();
        bufferbuilder.pos(endX, endY, 0.0D).endVertex();
        bufferbuilder.pos(endX, startY, 0.0D).endVertex();
        bufferbuilder.pos(startX, startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void setText(String text) {
        RDGuiEventFactory.pushTextTypedEvent(textTypedEvent, self(), Minecraft.getMinecraft(),
                moveEvent.getMouseX(), moveEvent.getMouseY(), getCursorPosition(), getSelectionEnd(), getText(), text);
        onAnyEventFire(textTypedEvent);
        if (!textTypedEvent.isCancelled()) super.setText(text);
    }

    @Override
    public void writeText(String textToWrite) {
        RDGuiEventFactory.pushTextTypedEvent(textTypedEvent, self(), Minecraft.getMinecraft(),
                moveEvent.getMouseX(), moveEvent.getMouseY(), getCursorPosition(), getSelectionEnd(),
                getText(), internalWriteText(textToWrite));
        onAnyEventFire(textTypedEvent);
        if (!textTypedEvent.isCancelled()) super.writeText(textToWrite);
    }

    @Override
    public void deleteFromCursor(int num) {
        RDGuiEventFactory.pushTextTypedEvent(textTypedEvent, self(), Minecraft.getMinecraft(),
                moveEvent.getMouseX(), moveEvent.getMouseY(), getCursorPosition(), getSelectionEnd(),
                getText(), internalDeleteFromCursor(num));
        onAnyEventFire(textTypedEvent);
        if (!textTypedEvent.isCancelled()) super.deleteFromCursor(num);
    }

    private String internalWriteText(String textToWrite) {
        String s  = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int    i  = Math.min(getCursorPosition(), getSelectionEnd());
        int    j  = Math.max(getCursorPosition(), getSelectionEnd());
        int    k  = getMaxStringLength() - getText().length() - (i - j);

        if (!getText().isEmpty()) s = s + getText().substring(0, i);

        if (k < s1.length()) s = s + s1.substring(0, k);
        else s = s + s1;

        if (!getText().isEmpty() && j < getText().length()) s = s + getText().substring(j);

        return s;
    }

    private String internalDeleteFromCursor(int num) {
        if (!getText().isEmpty()) {
            if (getSelectionEnd() != getCursorPosition()) return internalWriteText("");
            else {
                boolean flag = num < 0;
                int     i    = flag ? getCursorPosition() + num : getCursorPosition();
                int     j    = flag ? getCursorPosition() : getCursorPosition() + num;
                String  s    = "";

                if (i >= 0) s = getText().substring(0, i);

                if (j < getText().length()) s = s + getText().substring(j);

                return s;
            }
        }
        return "";
    }
}