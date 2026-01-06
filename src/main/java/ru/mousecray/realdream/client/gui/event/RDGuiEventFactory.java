package ru.mousecray.realdream.client.gui.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.realdream.client.gui.MoveDirection;
import ru.mousecray.realdream.client.gui.RDGuiElement;
import ru.mousecray.realdream.client.gui.SoundSourceType;

@SideOnly(Side.CLIENT)
public class RDGuiEventFactory {
    public static <T extends RDGuiElement<T>> void pushMouseClickEvent(RDGuiMouseClickEvent<T> event, T obj, Minecraft mc, int x, int y) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
    }

    public static <T extends RDGuiElement<T>> void pushMouseMoveEvent(RDGuiMouseMoveEvent<T> event, T obj, Minecraft mc, int x, int y, MoveDirection moveDirection) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
        event.setMoveDirection(moveDirection);
    }

    public static <T extends RDGuiElement<T>> void pushMouseDragEvent(RDGuiMouseDragEvent<T> event, T obj, Minecraft mc, int x, int y, MoveDirection moveDirection, int diffX, int diffY, int tickDown) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
        event.setMoveDirection(moveDirection);
        event.setDiffX(diffX);
        event.setDiffY(diffY);
        event.setTickDown(tickDown);
    }

    public static <T extends RDGuiElement<T>> void pushTickEvent(RDGuiTickEvent<T> event, T obj, Minecraft mc, int x, int y, float partialTick) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
        event.setPartialTick(partialTick);
    }

    public static <T extends RDGuiElement<T>> void pushTextTypedEvent(RDGuiTextTypedEvent<T> event, T obj, Minecraft mc, int x, int y, int cursorPos, int selectionEnd, String oldText, String newText) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
        event.setCursorPos(cursorPos);
        event.setSelectionEnd(selectionEnd);
        event.setOldText(oldText);
        event.setNewText(newText);
    }

    public static <T extends RDGuiElement<T>> void pushSoundEvent(RDGuiSoundEvent<T> event, T obj, Minecraft mc, int x, int y, SoundHandler handler, SoundEvent sound, SoundSourceType source) {
        event.setCancelled(false);
        event.setObj(obj);
        event.setMc(mc);
        event.setMouseX(x);
        event.setMouseY(y);
        event.setSound(sound);
        event.setSource(source);
        event.setHandler(handler);
    }
}