/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mouseproject.core.client.gui.wallet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.mousecray.mouseproject.Tags;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.MGuiScreen;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.container.*;
import ru.mousecray.mouseproject.api.client.gui.control.*;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.*;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseClickEvent;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;
import ru.mousecray.mouseproject.api.client.gui.misc.cache.GuiCacheInitiator;
import ru.mousecray.mouseproject.api.client.gui.misc.cache.GuiElementCache;
import ru.mousecray.mouseproject.api.client.gui.misc.cache.GuiGridCacheBuilder;
import ru.mousecray.mouseproject.api.utils.ref.IntRef;
import ru.mousecray.mouseproject.api.utils.ref.StringRef;
import ru.mousecray.mouseproject.core.MouseProject;
import ru.mousecray.mouseproject.core.common.economy.CoinHelper;
import ru.mousecray.mouseproject.core.common.economy.CoinValue;
import ru.mousecray.mouseproject.core.common.economy.coin.CoinType;
import ru.mousecray.mouseproject.core.common.economy.coin.NormalCoinType;
import ru.mousecray.mouseproject.core.common.economy.coin.ResourceCoinType;
import ru.mousecray.mouseproject.core.common.economy.coin.SpecificCoinType;
import ru.mousecray.mouseproject.core.common.item.wallet.IWallet;
import ru.mousecray.mouseproject.core.nbt.ItemStackWalletNBTPipeline;
import ru.mousecray.mouseproject.core.nbt.MouseProjectNBT;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class GuiScreenWallet extends MGuiScreen {
    static final  ResourceLocation           TEXTURES      = new ResourceLocation(Tags.MOD_ID, "textures/gui/wallet.png");
    static final  GuiVector                  TEXTURES_SIZE = GuiVector.of(256);
    private final EntityPlayer               player;
    private final ItemStackWalletNBTPipeline walletPipe;
    private final ItemStack                  walletStack;

    public GuiScreenWallet(EntityPlayer player, int slot) {
        super("wallet_screen", GuiVector.of(230, 200), GuiVector.of(4), MouseProject.getInstance().getLogger());
        this.player = player;
        walletStack = player.inventory.getStackInSlot(slot).copy();
        walletPipe = !(walletStack.getItem() instanceof IWallet) ? null : MouseProjectNBT.get(walletStack).getWalletPipe();

        setBackground(TEXTURES, TEXTURES_SIZE, new GuiShape(0, 0, 230, 200));
    }

    MGuiCloseButton     closeButton;
    MGuiSimpleLabel     titleLabel;
    MGuiActionButton    takeAction;
    MGuiActionButton    putAction;
    WalletSliderControl walletSlider;
    MGuiLinearPanel     controlsPanel;
    MGuiLinearPanel     controlsRow1;
    MGuiLinearPanel     controlsRow2;

    @Override
    public void initGui() {
        resetGui();
        Keyboard.enableRepeatEvents(true);
        super.initGui();

        long     maxCoinValue = 1_000_000;
        FontSize fontSize     = FontSize.NORMAL;
        setFontSize(fontSize);

        closeButton = createCachedElement("close_button")
                .construct(() -> new MGuiCloseButton(new GuiShape(0, 0, 9, 9)))
                .setFinalAction(t -> {
                    t.getStateManager().clearStates();
                    t.setOnClickListener(event -> closeGui());
                })
                .setAnchor(AnchorPos.TOP_RIGHT)
                .build();

        if (walletPipe == null) return;

        titleLabel = createCachedElement("title_label")
                .construct(() -> new MGuiSimpleLabel(MGuiString.simple(walletStack.getDisplayName()), new GuiShape(0, 0, 80, 10)))
                .setFinalAction(t -> {
                    t.setGuiString(MGuiString.simple(walletStack.getDisplayName()));
                    t.getStateManager().clearStates();
                })
                .build();

        float panelWidth  = 114f;
        float controlSize = 13.0f;
        float controlGap  = 10f;

        controlsPanel = createCachedElement("controls_panel")
                .construct(() -> new MGuiLinearPanel(new GuiShape(0, 0, panelWidth, 67), GuiOrientation.VERTICAL))
                .setOffset(GuiVector.of(0, 133))
                .setFinalAction(MGuiPanel::removeAllChildren)
                .build();

        controlsRow1 = createCachedElement("row1_panel", controlsPanel)
                .construct(() -> new MGuiLinearPanel(new GuiShape(0, 0, panelWidth, controlSize), GuiOrientation.HORIZONTAL))
                .setFinalAction(t -> {
                    t.removeAllChildren();
                    t.getStateManager().clearStates();
                })
                .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_VERTICAL)))
                .build();

        createDynamicButton("btn_+1", "+1", controlsRow1, e -> walletSlider.addValue(1));
        createDynamicButton("btn_+10", "+10", controlsRow1, e -> walletSlider.addValue(10));
        createDynamicButton("btn_+50", "+50", controlsRow1, e -> walletSlider.addValue(50));

        createSpacer("row1_spacer", controlGap, controlsRow1);

        createDynamicButton("btn_-1", "-1", controlsRow1, e -> walletSlider.addValue(-1));
        createDynamicButton("btn_-10", "-10", controlsRow1, e -> walletSlider.addValue(-10));
        createDynamicButton("btn_-50", "-50", controlsRow1, e -> walletSlider.addValue(-50));

        controlsRow2 = createCachedElement("row2_panel", controlsPanel)
                .construct(() -> new MGuiLinearPanel(new GuiShape(0, 0, panelWidth, controlSize), GuiOrientation.HORIZONTAL))
                .setFinalAction(t -> {
                    t.removeAllChildren();
                    t.getStateManager().clearStates();
                })
                .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_VERTICAL)))
                .build();

        createDynamicButton("btn_+100", "+100", controlsRow2, e -> walletSlider.addValue(100));
        createDynamicButton("btn_+500", "+500", controlsRow2, e -> walletSlider.addValue(500));
        createDynamicButton("btn_+1K", "+1K", controlsRow2, e -> walletSlider.addValue(1000));

        createSpacer("row2_spacer", controlGap, controlsRow2);

        createDynamicButton("btn_-100", "-100", controlsRow2, e -> walletSlider.addValue(-100));
        createDynamicButton("btn_-500", "-500", controlsRow2, e -> walletSlider.addValue(-500));
        createDynamicButton("btn_-1K", "-1K", controlsRow2, e -> walletSlider.addValue(-1000));

        walletSlider = createCachedElement("wallet_slider_control", controlsPanel)
                .construct(() -> new WalletSliderControl(panelWidth, 22, maxCoinValue))
                .setMargin(new GuiMargin(0, 4f, 0, 2f))
                .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL)))
                .setFinalAction(t -> {
                    t.getStateManager().clearStates();
                    t.setOnValidityChanged(isValid -> {
                        if (isValid) {
                            if (takeAction != null) takeAction.getStateManager().remove(MGuiElementState.DISABLED);
                            if (putAction != null) putAction.getStateManager().remove(MGuiElementState.DISABLED);
                        } else {
                            if (takeAction != null) takeAction.getStateManager().add(MGuiElementState.DISABLED);
                            if (putAction != null) putAction.getStateManager().add(MGuiElementState.DISABLED);
                        }
                    });
                })
                .build();

        takeAction = createCachedElement("take_action", controlsPanel)
                .construct(() -> new MGuiActionButton(GuiShape.ZERO, MGuiString.localizedGuiTag("wallet.button.take")))
                .setCreateAction(t -> {
                    t.getStateManager().add(MGuiElementState.DISABLED);
                    t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));
                })
                .setFinalAction(t -> {
                    t.getStateManager().clearStates();
                    t.setOnClickListener(event -> { /* TODO: Take action */ });
                })
                .build();

        putAction = createCachedElement("put_action", controlsPanel)
                .construct(() -> new MGuiActionButton(GuiShape.ZERO, MGuiString.localizedGuiTag("wallet.button.put")))
                .setCreateAction(t -> {
                    t.getStateManager().add(MGuiElementState.DISABLED);
                    t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));
                })
                .setFinalAction(t -> {
                    t.getStateManager().clearStates();
                    t.setOnClickListener(event -> { /* TODO: Put action */ });
                })
                .build();

        float coinW        = 14.9f;
        float coinH        = 23f;
        int   slot_count_x = 7;
        float CELL_GAP     = 6f;
        float colWidth     = slot_count_x * coinW;

        Map<Integer, List<CoinValue>> activeGroups = new HashMap<>();

        CoinValue bronzeBal = walletPipe.loadBronzeBalance();
        if (bronzeBal != null && bronzeBal.getValue() > 0) {
            EnumMap<NormalCoinType, Long> displayCoins = CoinHelper.getDisplayCoins(
                    CoinHelper.getMaxCoin(bronzeBal.getValue()), bronzeBal.getValue()
            );
            List<CoinValue> normalSlots = displayCoins.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(e -> CoinValue.create(e.getValue(), e.getKey()))
                    .collect(Collectors.toList());
            if (!normalSlots.isEmpty()) activeGroups.put(0, normalSlots);
        }

        List<CoinType> coinTypes = walletPipe.loadAllBalanceTypes();
        List<CoinValue> resourceSlots = coinTypes.stream()
                .filter(type -> type instanceof ResourceCoinType)
                .map(walletPipe::loadResourceBalance)
                .filter(cv -> cv != null && cv.getValue() > 0).collect(Collectors.toList());
        if (!resourceSlots.isEmpty()) activeGroups.put(1, resourceSlots);

        List<CoinValue> specificSlots = coinTypes.stream()
                .filter(type -> type instanceof SpecificCoinType)
                .map(walletPipe::loadSpecificBalance)
                .filter(cv -> cv != null && cv.getValue() > 0).collect(Collectors.toList());
        if (!specificSlots.isEmpty()) activeGroups.put(2, specificSlots);

        List<CoinValue> otherSlots = coinTypes.stream()
                .filter(type -> !(type instanceof NormalCoinType) && !(type instanceof ResourceCoinType) && !(type instanceof SpecificCoinType))
                .map(walletPipe::loadOtherBalance)
                .filter(cv -> cv != null && cv.getValue() > 0).collect(Collectors.toList());
        if (!otherSlots.isEmpty()) activeGroups.put(3, otherSlots);

        MGuiLinearPanel coinsPanel = GuiElementCache.INSTANCE.getOrCreateCF(
                this, "coins_anchor_content",
                () -> new MGuiLinearPanel(
                        new GuiShape(0, 0, 222, 400), GuiOrientation.HORIZONTAL
                ),
                t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL)),
                t -> {
                    t.removeAllChildren();
                    t.getStateManager().clearStates();
                }
        );

        createCachedElement("coins_container")
                .construct(() -> new MGuiSimpleScrollPanel(new GuiShape(0, 0, 222, 115)))
                .setOffset(GuiVector.of(4, 10))
                .setFinalAction(t -> {
                    t.setContent(coinsPanel);
                    t.getStateManager().clearStates();
                })
                .build();

        if (activeGroups.isEmpty()) {
            createCachedElement("empty_label", coinsPanel)
                    .construct(() -> new MGuiSimpleLabel(MGuiString.localizedGuiTag("wallet.label.empty"), new GuiShape(0, 0, 80, 10)))
                    .setOffset(GuiVector.of(2, 0))
                    .setFinalAction(t -> t.getStateManager().clearStates())
                    .build();
        } else {
            List<Map.Entry<Integer, List<CoinValue>>> groupsList = new ArrayList<>(activeGroups.entrySet());

            for (int col = 0; col < 2; col++) {
                MGuiLinearPanel columnPanel = createCachedElement("column_panel_" + col, coinsPanel)
                        .construct(() -> new MGuiLinearPanel(new GuiShape(0, 0, colWidth, 115), GuiOrientation.VERTICAL))
                        .setMargin(new GuiMargin(0, 3f, 0, 0))
                        .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL)))
                        .setFinalAction(t -> {
                            t.removeAllChildren();
                            t.getStateManager().clearStates();
                        })
                        .build();

                for (int row = 0; row < 2; row++) {
                    int idx = col * 2 + row;
                    if (idx >= groupsList.size()) break;

                    Map.Entry<Integer, List<CoinValue>> groupSlots = groupsList.get(idx);

                    StringRef groupLabelKey = new StringRef("wallet.label.");
                    switch (groupSlots.getKey()) {
                        case 0:
                            groupLabelKey.$A("normal");
                            break;
                        case 1:
                            groupLabelKey.$A("resource");
                            break;
                        case 2:
                            groupLabelKey.$A("specific");
                            break;
                        default:
                            groupLabelKey.$A("other");
                            break;
                    }

                    IntRef rowsNum = new IntRef((int) Math.ceil(groupSlots.getValue().size() / (double) slot_count_x));
                    if (rowsNum.$() == 0) rowsNum.$(1);

                    MGuiLinearPanel groupPanel = createCachedElement("group_panel_" + idx, columnPanel)
                            .construct(() -> new MGuiLinearPanel(new GuiShape(0, 0, colWidth, 0), GuiOrientation.VERTICAL))
                            .setMargin(new GuiMargin(0, 0, 8f, 8f))
                            .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL, GuiScaleType.WRAP_VERTICAL)))
                            .setFinalAction(t -> {
                                t.removeAllChildren();
                                t.getStateManager().clearStates();
                            })
                            .build();

                    MGuiAnchorPanel titlePanel = createCachedElement("title_panel_" + idx, groupPanel)
                            .construct(() -> new MGuiAnchorPanel(new GuiShape(0, 0, colWidth, 12)))
                            .setMargin(new GuiMargin(0, 3f))
                            .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL)))
                            .setFinalAction(t -> {
                                t.removeAllChildren();
                                t.getStateManager().clearStates();
                            })
                            .build();

                    createCachedElement("group_title_" + idx, titlePanel)
                            .construct(() -> new MGuiSimpleLabel(MGuiString.localizedGuiTag(groupLabelKey.$()), new GuiShape(0, 0, colWidth - 15, 10)))
                            .setAnchor(AnchorPos.MIDDLE_LEFT)
                            .setExistAction(t -> t.setGuiString(MGuiString.localizedGuiTag(groupLabelKey.$())))
                            .setFinalAction(t -> t.getStateManager().clearStates())
                            .build();

                    createCachedElement("select_all_" + idx, titlePanel)
                            .construct(() -> new MGuiSimpleCheckbox(
                                    new GuiShape(0, 0, 8, 8),
                                    MGuiString.localizedGuiTag("wallet.button.select_all"),
                                    fontRenderer
                            ))
                            .setAnchor(AnchorPos.TOP_RIGHT)
                            .setCreateAction(t -> t.setFontSize(fontSize))
                            .setFinalAction(t -> {
                                t.getStateManager().clearStates();
                                t.setOnClickListener(e -> { /* TODO: Select All action */ });
                            })
                            .build();

                    MGuiGridPanel coinsGrid = createCachedElement("coins_grid_" + idx, groupPanel)
                            .construct(() -> new MGuiGridPanel(new GuiShape(0, 0, colWidth, 0), rowsNum.$(), slot_count_x))
                            .setMargin(new GuiMargin(2, 0, 0, 0))
                            .setCreateAction(t -> {
                                t.setGaps(0, CELL_GAP);
                                t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL, GuiScaleType.WRAP_VERTICAL));

                            })
                            .setFinalAction(t -> {
                                t.setGridSize(rowsNum.$(), slot_count_x);
                                t.removeAllChildren();
                                t.getStateManager().clearStates();
                            })
                            .build();

                    int slotIndex = 0;
                    for (CoinValue coinValue : groupSlots.getValue()) {
                        if (slotIndex >= 32) break;
                        int gridRow = slotIndex / slot_count_x;
                        int gridCol = slotIndex % slot_count_x;

                        createCachedElement("coin_btn_" + idx + "_" + slotIndex, coinsGrid)
                                .construct(
                                        new GuiGridCacheBuilder<>(),
                                        () -> new WalletCoinButton(new GuiShape(0, 0, coinW, coinH), coinValue)
                                )
                                .setAnchor(AnchorPos.MIDDLE_CENTER)
                                .setGridPos(new GridPos(gridRow, gridCol))
                                .setExistAction(t -> {
                                    t.setCount(coinValue);
                                    t.setOnClickListener(e -> { /* TODO: Coin click logic */ });
                                })
                                .setFinalAction(t -> t.getStateManager().clearStates())
                                .build();
                        slotIndex++;
                    }
                }
            }
        }
        bake();
    }

    private <D extends MGuiPanel<?>> void createDynamicButton(String key, String text, @Nullable D parent, Consumer<GuiMouseClickEvent<MGuiSimpleButton>> onClick) {
        GuiCacheInitiator<MGuiPanel<?>> cachedElement = createCachedElement(key);
        if (parent != null) cachedElement.setParent(parent);
        cachedElement
                .construct(() -> new MGuiSimpleButton(new GuiShape(0, 0, 10, 13.0f), MGuiString.simple(text)))
                .setCreateAction(t -> t.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT_HORIZONTAL)))
                .setFinalAction(t -> {
                    t.getStateManager().clearStates();
                    t.setOnClickListener(onClick);
                })
                .build();
    }

    private <D extends MGuiPanel<?>> void createSpacer(String key, float width, @Nullable D parent) {
        GuiCacheInitiator<MGuiPanel<?>> cachedElement = createCachedElement(key);
        if (parent != null) cachedElement.setParent(parent);
        cachedElement
                .construct(() -> new MGuiFreePanel(new GuiShape(0, 0, width, 1)))
                .setFinalAction(t -> t.getStateManager().clearStates())
                .build();
    }

    private void sendWalletToServer() { }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
}