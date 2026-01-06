package ru.mousecray.realdream.client.gui.impl.wallet;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.client.gui.RDFontSize;
import ru.mousecray.realdream.client.gui.RDGuiLabel;
import ru.mousecray.realdream.client.gui.RDGuiScreen;
import ru.mousecray.realdream.client.gui.dim.GuiShape;
import ru.mousecray.realdream.client.gui.event.RDGuiTextTypedEvent;
import ru.mousecray.realdream.client.gui.impl.*;
import ru.mousecray.realdream.client.gui.state.GuiButtonPersistentState;
import ru.mousecray.realdream.common.economy.CoinHelper;
import ru.mousecray.realdream.common.economy.CoinValue;
import ru.mousecray.realdream.common.economy.coin.CoinType;
import ru.mousecray.realdream.common.economy.coin.NormalCoinType;
import ru.mousecray.realdream.common.economy.coin.ResourceCoinType;
import ru.mousecray.realdream.common.economy.coin.SpecificCoinType;
import ru.mousecray.realdream.common.item.wallet.IWallet;
import ru.mousecray.realdream.nbt.ItemStackWalletNBTPipeline;
import ru.mousecray.realdream.nbt.RealDreamNBT;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class GuiScreenWallet extends RDGuiScreen {
    static final  ResourceLocation           TEXTURES      = new ResourceLocation(Tags.MOD_ID, "textures/gui/wallet.png");
    static final  GuiVector                  TEXTURES_SIZE = new GuiVector(256, 256);
    private final EntityPlayer               player;
    private final ItemStackWalletNBTPipeline walletPipe;
    private final ItemStack                  walletStack;

    public GuiScreenWallet(EntityPlayer player, int slot) {
        super(new GuiVector(230, 200), new GuiVector(4, 4));
        this.player = player;
        walletStack = player.inventory.getStackInSlot(slot).copy();
        walletPipe = !(walletStack.getItem() instanceof IWallet) ? null : RealDreamNBT.get(walletStack).getWalletPipe();

        setBackground(TEXTURES, TEXTURES_SIZE, new GuiShape(0, 0, 230, 200));
    }

    @Override
    public void initGui() {
        super.initGui();
        resetGui();
        Keyboard.enableRepeatEvents(true);

        long maxCoinValue = 1_000_000;

        float      posX1            = guiContentShape.getX();
        float      posY1            = guiContentShape.getY();
        float      sizeX            = guiContentShape.getWidth();
        float      sizeY            = guiContentShape.getHeight();
        float      posX2            = posX1 + sizeX;
        float      posY2            = posY1 + sizeY;
        float      scaledFontHeight = getComponentY(9) - 9;
        RDFontSize fontSize;
        if (scaledFontHeight < 0) fontSize = RDFontSize.SMALL;
        else if (scaledFontHeight > 7) fontSize = RDFontSize.EXTRA_LARGE;
        else if (scaledFontHeight > 2.85) fontSize = RDFontSize.LARGE;
        else fontSize = RDFontSize.NORMAL;

        float buttonCloseY = getComponentY(9);

        //noinspection SuspiciousNameCombination
        addButton(new RDGuiCloseButton(
                new GuiShape(posX2 - buttonCloseY, posY1, buttonCloseY, buttonCloseY),
                TEXTURES, TEXTURES_SIZE, new GuiShape(95, 200, 9, 9), fontSize,
                event -> closeGui()
        ));

        //Заглушка, проверяем, открывать ли гуи
        if (walletPipe == null) return;

        addLabel(new RDGuiLabel(walletStack.getDisplayName(), fontRenderer,
                new GuiShape(posX1, posY1, getComponentY(80), 10),
                14737632, fontSize
        ));

        float    buttonTakePutYSize       = getComponentY(13.0f);
        float    buttonTakePutXSize       = getComponentX(17.2f);
        float    buttonTakePutYPos        = getComponentY(133f);
        GuiShape tileTextureShape         = new GuiShape(80, 200, 10, 10);
        float    tileFirstYPos            = posY1 + buttonTakePutYPos;
        float    tileSecondYPos           = posY1 + buttonTakePutYPos + buttonTakePutYSize;
        float    buttonTakePutActionYPos  = getComponentY(177f);
        float    buttonTakePutActionY2Pos = getComponentY(188f);
        float    buttonTakePutGap         = getComponentX(10);

        RDGuiActionButton takeAction = new RDGuiActionButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.take"),
                new GuiShape(
                        posX1, posY1 + buttonTakePutActionYPos,
                        getComponentX(113.8f), getComponentY(12)
                ),
                TEXTURES, TEXTURES_SIZE,
                new GuiShape(0, 200, 80, 10),
                fontSize,
                event -> {

                }
        );
        takeAction.applyState(GuiButtonPersistentState.DISABLED);
        RDGuiActionButton putAction = new RDGuiActionButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.put"),
                new GuiShape(
                        posX1, posY1 + buttonTakePutActionY2Pos,
                        getComponentX(113.8f), getComponentY(12)
                ),
                TEXTURES, TEXTURES_SIZE,
                new GuiShape(0, 200, 80, 10),
                fontSize,
                event -> {

                }
        );
        putAction.applyState(GuiButtonPersistentState.DISABLED);
        Consumer<RDGuiTextTypedEvent<RDGuiNumberField>> fieldEventTake = event -> {
            String newText = event.getNewText();
            if (newText == null || newText.length() > 19) {
                event.setCancelled(true);
                takeAction.applyState(GuiButtonPersistentState.DISABLED);
                putAction.applyState(GuiButtonPersistentState.DISABLED);
                return;
            }
            if (newText.trim().isEmpty()) {
                takeAction.applyState(GuiButtonPersistentState.DISABLED);
                putAction.applyState(GuiButtonPersistentState.DISABLED);
                return;
            }
            long l;
            try {
                l = Long.parseLong(newText);
            } catch (NumberFormatException ignore) {
                event.setCancelled(true);
                takeAction.applyState(GuiButtonPersistentState.DISABLED);
                putAction.applyState(GuiButtonPersistentState.DISABLED);
                return;
            }
            if (l <= 0) {
                event.setCancelled(true);
                takeAction.applyState(GuiButtonPersistentState.DISABLED);
                putAction.applyState(GuiButtonPersistentState.DISABLED);
                return;
            }

            takeAction.applyState(GuiButtonPersistentState.NORMAL);
            putAction.applyState(GuiButtonPersistentState.NORMAL);
        };
        RDGuiNumberField fieldTakePut = new RDGuiNumberField(fontRenderer, I18n.format("gui." + Tags.MOD_ID + ".wallet.text_field.take_put_count"),
                new GuiShape(
                        posX1, posY1 + buttonTakePutYPos + buttonTakePutYSize * 2,
                        getComponentX(113.5f), buttonTakePutYSize * 1.2f
                ),
                TEXTURES, TEXTURES_SIZE, new GuiShape(104, 200, 80, 10), fontSize, fieldEventTake
        );
        addButton(new RDGuiDefaultButton("+1",
                new GuiShape(posX1, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 1, 1))
        ));
        addButton(new RDGuiDefaultButton("+10",
                new GuiShape(posX1 + buttonTakePutXSize, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 10, 1))
        ));
        addButton(new RDGuiDefaultButton("+50",
                new GuiShape(posX1 + buttonTakePutXSize * 2, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 50, 1))
        ));
        addButton(new RDGuiDefaultButton("-1",
                new GuiShape(posX1 + buttonTakePutXSize * 3 + buttonTakePutGap, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 1, 1))
        ));
        addButton(new RDGuiDefaultButton("-10",
                new GuiShape(posX1 + buttonTakePutXSize * 4 + buttonTakePutGap, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 10, 1))
        ));
        addButton(new RDGuiDefaultButton("-50",
                new GuiShape(posX1 + buttonTakePutXSize * 5 + buttonTakePutGap, tileFirstYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 50, 1))
        ));
        addButton(new RDGuiDefaultButton("+100",
                new GuiShape(posX1, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 100, 1))
        ));
        addButton(new RDGuiDefaultButton("+500",
                new GuiShape(posX1 + buttonTakePutXSize, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 500, 1))
        ));
        addButton(new RDGuiDefaultButton("+1K",
                new GuiShape(posX1 + buttonTakePutXSize * 2, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 1000, 1))
        ));
        addButton(new RDGuiDefaultButton("-100",
                new GuiShape(posX1 + buttonTakePutXSize * 3 + buttonTakePutGap, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 100, 1))
        ));
        addButton(new RDGuiDefaultButton("-500",
                new GuiShape(posX1 + buttonTakePutXSize * 4 + buttonTakePutGap, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 500, 1))
        ));
        addButton(new RDGuiDefaultButton("-1K",
                new GuiShape(posX1 + buttonTakePutXSize * 5 + buttonTakePutGap, tileSecondYPos, buttonTakePutXSize, buttonTakePutYSize),
                TEXTURES, TEXTURES_SIZE, tileTextureShape, fontSize,
                event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 1000, 1))
        ));
        addTextField(fieldTakePut);
        addSlider(new RDGuiSlider(
                new GuiShape(
                        posX1, posY1 + buttonTakePutYPos + buttonTakePutYSize * 2 + buttonTakePutYSize * 1.2f - getComponentY(9),
                        getComponentX(113.8f), getComponentY(11)
                ),
                new GuiShape(
                        posX1, posY1 + buttonTakePutYPos + buttonTakePutYSize * 2 + buttonTakePutYSize * 1.2f - getComponentY(9),
                        getComponentX(6.68f), getComponentY(11)
                ),
                TEXTURES, TEXTURES_SIZE,
                new GuiShape(90, 200, 5, 7),
                0, 100,
                event -> {
                    RDGuiSlider obj = event.getObj();
                    if (obj != null) {
                        int i = obj.getValue();
                        fieldTakePut.setNumberText(i == 0 ? 1 : i * maxCoinValue / 100);
                    }
                },
                event -> {
                    RDGuiSlider obj = event.getObj();
                    if (obj != null) {
                        int i = obj.getValue();
                        fieldTakePut.setNumberText(i == 0 ? 1 : i * maxCoinValue / 100);
                    }
                }
        ));
        addButton(takeAction);
        addButton(putAction);

        float W            = getComponentX(14.9f);
        float H            = getComponentY(23);
        int   slot_count_x = 7;
        int   slot_count_y = 2;
        float ROW_WIDTH    = slot_count_x * W;
        float GROUP_HEIGHT = slot_count_y * H;
        float COLUMN_GAP   = getComponentX(5f);
        float baseX        = posX1 + getComponentX(2);
        float baseY        = posY1 + getComponentY(12);

        Map<Integer, List<CoinValue>> activeGroups = new HashMap<>();

        CoinValue       bronzeBal   = walletPipe.loadBronzeBalance();
        List<CoinValue> normalSlots = new ArrayList<>();
        if (bronzeBal != null && bronzeBal.getValue() > 0) {
            EnumMap<NormalCoinType, Long> displayCoins = CoinHelper.getDisplayCoins(
                    CoinHelper.getMaxCoin(bronzeBal.getValue()),
                    bronzeBal.getValue()
            );
            normalSlots = displayCoins.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(e -> CoinValue.create(e.getValue(), e.getKey()))
                    .collect(Collectors.toList());
        }
        if (!normalSlots.isEmpty()) activeGroups.put(0, normalSlots);

        List<CoinType> coinTypes = walletPipe.loadAllBalanceTypes();
        List<CoinValue> resourceSlots = coinTypes.stream()
                .filter(type -> type instanceof ResourceCoinType)
                .map(walletPipe::loadResourceBalance)
                .filter(cv -> cv != null && cv.getValue() > 0)
                .collect(Collectors.toList());
        if (!resourceSlots.isEmpty()) activeGroups.put(1, resourceSlots);

        List<CoinValue> specificSlots = coinTypes.stream()
                .filter(type -> type instanceof SpecificCoinType)
                .map(walletPipe::loadSpecificBalance)
                .filter(cv -> cv != null && cv.getValue() > 0)
                .collect(Collectors.toList());
        if (!specificSlots.isEmpty()) activeGroups.put(2, specificSlots);

        List<CoinValue> otherSlots = coinTypes.stream()
                .filter(type -> !(type instanceof NormalCoinType) &&
                        !(type instanceof ResourceCoinType) &&
                        !(type instanceof SpecificCoinType))
                .map(walletPipe::loadOtherBalance)
                .filter(cv -> cv != null && cv.getValue() > 0)
                .collect(Collectors.toList());
        if (!otherSlots.isEmpty()) activeGroups.put(3, otherSlots);

        if (activeGroups.isEmpty()) {
            addLabel(new RDGuiLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.empty"), fontRenderer,
                    new GuiShape(baseX, baseY, getComponentY(80), 10),
                    14737632, fontSize));
        }

        int groupIndex = 0;
        for (Map.Entry<Integer, List<CoinValue>> groupSlots : activeGroups.entrySet()) {
            int column      = groupIndex / slot_count_y;
            int rowInColumn = groupIndex % slot_count_y;

            float groupStartX = baseX + column * (ROW_WIDTH + COLUMN_GAP);
            float groupStartY = baseY + rowInColumn * GROUP_HEIGHT;

            if (groupSlots.getKey() % 2 != 0) groupStartY += getComponentY(12);
            if (column > 0) groupStartX += getComponentX(13);

            switch (groupSlots.getKey()) {
                case 0:
                    addLabel(new RDGuiLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.normal"), fontRenderer,
                            new GuiShape(groupStartX, groupStartY, getComponentY(80), 10),
                            14737632, fontSize));
                    addButton(new RDGuiCheckButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.select_all"), fontRenderer,
                            new GuiShape(groupStartX + ROW_WIDTH - getComponentX(8), groupStartY, getComponentX(8), getComponentX(8)),
                            TEXTURES, TEXTURES_SIZE,
                            new GuiShape(240, 0, 8, 8), fontSize,
                            event -> {

                            }
                    ));
                    break;
                case 1:
                    addLabel(new RDGuiLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.resource"), fontRenderer,
                            new GuiShape(groupStartX, groupStartY, getComponentY(80), getComponentX(10)),
                            14737632, fontSize));
                    addButton(new RDGuiCheckButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.select_all"), fontRenderer,
                            new GuiShape(groupStartX + ROW_WIDTH - getComponentX(8), groupStartY, getComponentX(8), getComponentX(8)),
                            TEXTURES, TEXTURES_SIZE,
                            new GuiShape(240, 0, 8, 8), fontSize,
                            event -> {

                            }
                    ));
                    break;
                case 2:
                    addLabel(new RDGuiLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.specific"), fontRenderer,
                            new GuiShape(groupStartX, groupStartY, getComponentY(80), 10),
                            14737632, fontSize));
                    addButton(new RDGuiCheckButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.select_all"), fontRenderer,
                            new GuiShape(groupStartX + ROW_WIDTH - getComponentX(8), groupStartY, getComponentX(8), getComponentX(8)),
                            TEXTURES, TEXTURES_SIZE,
                            new GuiShape(240, 0, 8, 8), fontSize,
                            event -> {

                            }
                    ));
                    break;
                case 3:
                    addLabel(new RDGuiLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.other"), fontRenderer,
                            new GuiShape(groupStartX, groupStartY, getComponentY(80), 10),
                            14737632, fontSize));
                    addButton(new RDGuiCheckButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.select_all"), fontRenderer,
                            new GuiShape(groupStartX + ROW_WIDTH - getComponentX(8), groupStartY, getComponentX(8), getComponentX(8)),
                            TEXTURES, TEXTURES_SIZE,
                            new GuiShape(240, 0, 8, 8), fontSize,
                            event -> {

                            }
                    ));
                    break;
            }
            groupStartY += getComponentY(11);
            groupStartX += getComponentX(2);

            int             maxSlots     = Math.min(32, groupSlots.getValue().size());
            List<CoinValue> limitedSlots = groupSlots.getValue().subList(0, maxSlots);

            int slotIndex = 0;  //Счётчик слотов в группе
            for (CoinValue coinValue : limitedSlots) {
                int row      = slotIndex / slot_count_x;
                int colInRow = slotIndex % slot_count_x;

                float currentX = groupStartX + colInRow * W;
                float currentY = groupStartY + row * H;

                addButton(new WalletCoinButton(
                        new GuiShape(currentX, currentY, W, H),
                        fontSize, coinValue,
                        event -> { }
                ));

                slotIndex++;
            }

            groupIndex++;
        }
    }

    private void sendWalletToServer() {

    }

    @Override public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
}