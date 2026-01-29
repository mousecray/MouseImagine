package ru.mousecray.realdream.client.gui.impl.wallet;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.mousecray.realdream.Tags;
import ru.mousecray.realdream.client.gui.GuiTexturePack;
import ru.mousecray.realdream.client.gui.RDFontSize;
import ru.mousecray.realdream.client.gui.RDGuiScreen;
import ru.mousecray.realdream.client.gui.container.RDGuiBasicPanel;
import ru.mousecray.realdream.client.gui.container.RDGuiBasicScrollPanel;
import ru.mousecray.realdream.client.gui.dim.*;
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

        RDFontSize fontSize = RDFontSize.NORMAL;

        // Close Button
        addButton(new RDGuiCloseButton(
                new GuiShape(0, 0, 9, 9),
                TEXTURES, TEXTURES_SIZE, new GuiShape(95, 200, 9, 9), fontSize,
                event -> closeGui()
        ), null, AnchorPosition.TOP_RIGHT, new GuiVector(0, 0));

        //Заглушка, проверяем, открывать ли гуи
        if (walletPipe == null) return;

        // Title
        addLabel(new RDGuiStaticLabel(walletStack.getDisplayName(), fontRenderer,
                new GuiShape(0, 0, 80, 10),
                14737632, fontSize
        ), null, AnchorPosition.TOP_LEFT, new GuiVector(0, 0));

        float buttonTakePutYSize = 13.0f;
        float buttonTakePutXSize = 17.2f;
        float buttonTakePutGap   = 10f;

        RDGuiActionButton takeAction = new RDGuiActionButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.take"),
                new GuiShape(0, 0, 113.8f, 12),
                TEXTURES, TEXTURES_SIZE, new GuiShape(0, 200, 80, 10), fontSize,
                event -> { }
        );
        takeAction.applyState(GuiButtonPersistentState.DISABLED);

        RDGuiActionButton putAction = new RDGuiActionButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.put"),
                new GuiShape(0, 0, 113.8f, 12),
                TEXTURES, TEXTURES_SIZE, new GuiShape(0, 200, 80, 10), fontSize,
                event -> { }
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
                new GuiShape(0, 0, 113.5f, buttonTakePutYSize * 1.2f),
                TEXTURES, TEXTURES_SIZE, new GuiShape(104, 200, 80, 10), fontSize, fieldEventTake
        );

        // Панель управления (левая часть)
        RDGuiBasicPanel controls = new RDGuiBasicPanel(new GuiShape(0, 0, 114, 67));
        controls.setLayoutType(LayoutType.LINEAR_VERTICAL);
        addPanel(controls, null, AnchorPosition.TOP_LEFT, new GuiVector(0, 133));

        // Ряд кнопок +1, +10, +50, -1, -10, -50
        RDGuiBasicPanel row1 = new RDGuiBasicPanel(new GuiShape(0, 0, 114, buttonTakePutYSize));
        row1.setLayoutType(LayoutType.LINEAR_HORIZONTAL);
        controls.addChild(row1, null, null, null);

        row1.addChild(new RDGuiDefaultButton("+1", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 1, 1))), null, null, null);
        row1.addChild(new RDGuiDefaultButton("+10", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 10, 1))), null, null, null);
        row1.addChild(new RDGuiDefaultButton("+50", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 50, 1))), null, null, null);
        row1.addChild(new RDGuiBasicPanel(new GuiShape(0, 0, buttonTakePutGap, 1)), null, null, null); // Spacer
        row1.addChild(new RDGuiDefaultButton("-1", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 1, 1))), null, null, null);
        row1.addChild(new RDGuiDefaultButton("-10", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 10, 1))), null, null, null);
        row1.addChild(new RDGuiDefaultButton("-50", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 50, 1))), null, null, null);

        // Ряд кнопок +100, +500, +1K, -100, -500, -1K
        RDGuiBasicPanel row2 = new RDGuiBasicPanel(new GuiShape(0, 0, 114, buttonTakePutYSize));
        row2.setLayoutType(LayoutType.LINEAR_HORIZONTAL);
        controls.addChild(row2, null, null, null);

        row2.addChild(new RDGuiDefaultButton("+100", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 100, 1))), null, null, null);
        row2.addChild(new RDGuiDefaultButton("+500", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 500, 1))), null, null, null);
        row2.addChild(new RDGuiDefaultButton("+1K", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() + 1000, 1))), null, null, null);
        row2.addChild(new RDGuiBasicPanel(new GuiShape(0, 0, buttonTakePutGap, 1)), null, null, null); // Spacer
        row2.addChild(new RDGuiDefaultButton("-100", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 100, 1))), null, null, null);
        row2.addChild(new RDGuiDefaultButton("-500", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 500, 1))), null, null, null);
        row2.addChild(new RDGuiDefaultButton("-1K", new GuiShape(0, 0, buttonTakePutXSize, buttonTakePutYSize), TEXTURES, TEXTURES_SIZE, new GuiShape(80, 200, 10, 10), fontSize, event -> fieldTakePut.setNumberText(Math.max(fieldTakePut.getNumberText() - 1000, 1))), null, null, null);

        // Поле и слайдер
        RDGuiBasicPanel fieldSliderStack = new RDGuiBasicPanel(new GuiShape(0, 0, 114, 18));
        fieldSliderStack.setLayoutType(LayoutType.FREE);
        controls.addChild(fieldSliderStack, null, null, null);

        fieldSliderStack.addChild(fieldTakePut, null, AnchorPosition.TOP_LEFT, null);

        class WalletSlider extends RDGuiSlider<WalletSlider> {
            public WalletSlider() {
                super(new GuiShape(0, 0, 113.8f, 11),
                        GuiTexturePack.Builder.create(TEXTURES, TEXTURES_SIZE, new GuiVector(0, 200), new GuiVector(80, 10)).build(),
                        GuiTexturePack.Builder.create(TEXTURES, TEXTURES_SIZE, new GuiVector(90, 200), new GuiVector(5, 7)).build(),
                        new GuiVector(6.68f, 11),
                        0, 100,
                        false);
            }
        }
        fieldSliderStack.addChild(new WalletSlider().onChange(value -> fieldTakePut.setNumberText(value == 0 ? 1 : (long) value * maxCoinValue / 100)),
                null, AnchorPosition.TOP_LEFT, new GuiVector(0, buttonTakePutYSize * 1.2f - 9));

        controls.addChild(takeAction, null, null, null);
        controls.addChild(putAction, null, null, null);

        float W            = 14.9f;
        float H            = 23f;
        int   slot_count_x = 7;
        float ROW_WIDTH    = slot_count_x * W;
        float baseX        = 2;
        float baseY        = 12;
        float COLUMN_GAP   = 5f;

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

        // Контейнер для монет (правая часть)
        RDGuiBasicScrollPanel coinsContainer = new RDGuiBasicScrollPanel(new GuiShape(0, 0, 115, 188));
        coinsContainer.setLayoutType(LayoutType.FREE); // Используем FREE чтобы вручную расставить колонки
        addPanel(coinsContainer, null, AnchorPosition.TOP_RIGHT, new GuiVector(0, 12));

        if (activeGroups.isEmpty()) {
            coinsContainer.addChild(new RDGuiStaticLabel(I18n.format("gui." + Tags.MOD_ID + ".wallet.label.empty"), fontRenderer,
                    new GuiShape(0, 0, 80, 10), 14737632, fontSize), null, AnchorPosition.TOP_LEFT, new GuiVector(baseX, 0));
        } else {
            List<Map.Entry<Integer, List<CoinValue>>> groupsList = new ArrayList<>(activeGroups.entrySet());

            for (int col = 0; col < 2; col++) {
                float           colX        = col * (ROW_WIDTH + COLUMN_GAP + 5);
                RDGuiBasicPanel columnPanel = new RDGuiBasicPanel(new GuiShape(colX, 0, ROW_WIDTH + 5, 188));
                columnPanel.setLayoutType(LayoutType.LINEAR_VERTICAL);
                coinsContainer.addChild(columnPanel, null, null, null);

                for (int i = 0; i < 2; i++) {
                    int idx = col * 2 + i;
                    if (idx >= groupsList.size()) break;

                    Map.Entry<Integer, List<CoinValue>> groupSlots = groupsList.get(idx);

                    String groupLabelKey;
                    switch (groupSlots.getKey()) {
                        case 0:
                            groupLabelKey = "gui." + Tags.MOD_ID + ".wallet.label.normal";
                            break;
                        case 1:
                            groupLabelKey = "gui." + Tags.MOD_ID + ".wallet.label.resource";
                            break;
                        case 2:
                            groupLabelKey = "gui." + Tags.MOD_ID + ".wallet.label.specific";
                            break;
                        default:
                            groupLabelKey = "gui." + Tags.MOD_ID + ".wallet.label.other";
                            break;
                    }

                    int   rows   = (int) Math.ceil(groupSlots.getValue().size() / (double) slot_count_x);
                    float groupH = 12 + rows * H;

                    RDGuiBasicPanel groupPanel = new RDGuiBasicPanel(new GuiShape(0, 0, ROW_WIDTH + 2, groupH));
                    groupPanel.setLayoutType(LayoutType.FREE);
                    columnPanel.addChild(groupPanel, new GuiMargin(0, 0, 0, 10), null, null);

                    groupPanel.addChild(new RDGuiStaticLabel(I18n.format(groupLabelKey), fontRenderer,
                            new GuiShape(0, 0, ROW_WIDTH - 10, 10), 14737632, fontSize), null, AnchorPosition.TOP_LEFT, null);

                    groupPanel.addChild(new RDGuiCheckButton(I18n.format("gui." + Tags.MOD_ID + ".wallet.button.select_all"), fontRenderer,
                                    new GuiShape(0, 0, 8, 8), TEXTURES, TEXTURES_SIZE, new GuiShape(240, 0, 8, 8), fontSize, e -> { }),
                            null, AnchorPosition.TOP_RIGHT, null);

                    int slotIndex = 0;
                    for (CoinValue coinValue : groupSlots.getValue()) {
                        if (slotIndex >= 32) break;
                        int row      = slotIndex / slot_count_x;
                        int colInRow = slotIndex % slot_count_x;

                        groupPanel.addChild(new WalletCoinButton(new GuiShape(colInRow * W, 11 + row * H, W, H), fontSize, coinValue, e -> { }),
                                null, AnchorPosition.TOP_LEFT, null);
                        slotIndex++;
                    }
                }
            }
        }
    }

    private void sendWalletToServer() {

    }

    @Override public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
}
