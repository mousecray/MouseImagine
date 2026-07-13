package ru.mousecray.mouseproject.api.config.pars;

import ru.mousecray.mouseproject.api.DisplayName;
import ru.mousecray.mouseproject.api.VariableValue;
import ru.mousecray.mouseproject.api.config.ConfigVal;
import ru.mousecray.mouseproject.api.config.specific.ConfigLocaleType;
import ru.mousecray.mouseproject.api.config.values.ConfigValDisabler;
import ru.mousecray.mouseproject.api.customtype.values.PlusMinusType;
import ru.mousecray.mouseproject.api.utils.MouseReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigParDisabler extends ConfigPar<PlusMinusType> {
    private final boolean useDefaultName;

    public ConfigParDisabler(DisplayName name, boolean useDefaultName, ConfigValDisabler value, @Nullable String comment) {
        super(name, true, value, comment);
        this.useDefaultName = useDefaultName;
    }

    @Nonnull @Override
    public DisplayName getName() {
        return useDefaultName && hasConfig()
                ? new DisplayName(
                super.getName().getInternalName(),
                getConfig().getDictionary().getLocaleForLocale(ConfigLocaleType.DISABLE_PAR))
                : super.getName();
    }

    protected int setDisabledRaw(@Nullable String value) {
        ConfigValDisabler val = ((ConfigValDisabler) getConfigVal());
        VariableValue<Integer> var = MouseReflection.invokeMethod(
                ConfigVal.class, String.class, int.class, "setValueRaw",
                val, value, hasLogger() ? getLogger() : null
        );
        int res = var.isPresent() ? var.getValue() : -1;
        isDisabled = val.isDisabled();
        return res;
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        ((ConfigValDisabler) getConfigVal()).setDisabled(disabled);
    }

    @Override public boolean isDisabled() { return isDisabled || getConfigVal().isDisabled(); }

    public ConfigValDisabler getValue()   { return (ConfigValDisabler) getConfigVal(); }

    @Override
    public void reset() {
        super.reset();
        ConfigValDisabler val = (ConfigValDisabler) getConfigVal();
        val.reset();
        isDisabled = val.isDisabled();
        MouseReflection.invokeMethod(
                ConfigVal.class, "reloadCache",
                val, hasLogger() ? getLogger() : null
        );
    }

    @Override
    protected void reloadCache() {
        super.reloadCache();
        MouseReflection.invokeMethod(
                ConfigVal.class, "reloadCache",
                getConfigVal(), hasLogger() ? getLogger() : null
        );
    }
}