package ru.mousecray.mouseproject.api.config.sections;

import ru.mousecray.mouseproject.api.DisplayName;
import ru.mousecray.mouseproject.api.config.ConfigParBase;
import ru.mousecray.mouseproject.api.config.ConfigSectBase;
import ru.mousecray.mouseproject.api.config.MouseConfig;
import ru.mousecray.mouseproject.api.config.pars.ConfigParDisabler;
import ru.mousecray.mouseproject.api.utils.MouseReflection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigSect extends ConfigSectBase {
    @Nullable protected final ConfigParDisabler disablePar;

    public ConfigSect(DisplayName name)                                         { this(name, (String) null); }
    public ConfigSect(DisplayName name, @Nullable String comment)               { this(name, null, comment); }
    public ConfigSect(DisplayName name, @Nullable ConfigParDisabler disablePar) { this(name, disablePar, null); }

    public ConfigSect(DisplayName name, @Nullable ConfigParDisabler disablePar, @Nullable String comment) {
        super(name, disablePar != null, comment);
        this.disablePar = disablePar;
        if (disablePar != null) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, ConfigSectBase.class, "setParent",
                    disablePar, this, hasLogger() ? getLogger() : null
            );
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, int.class, "setDeep",
                    disablePar, deep + 1, hasLogger() ? getLogger() : null
            );
            setConfig(config);
        }
    }

    @Override
    protected void setDeep(int deep) {
        super.setDeep(deep);
        if (canBeDisabled()) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, int.class, "setDeep",
                    disablePar, deep + 1, hasLogger() ? getLogger() : null
            );
        }
    }

    @Override
    protected void setConfig(MouseConfig config) {
        super.setConfig(config);
        if (canBeDisabled()) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, MouseConfig.class, "setConfig",
                    disablePar, config, hasLogger() ? getLogger() : null
            );
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        disablePar.setDisabled(disabled);
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void reset() {
        super.reset();
        if (canBeDisabled()) disablePar.reset();
    }

    @Override
    protected void reloadCache() {
        super.reloadCache();
        if (canBeDisabled()) {
            MouseReflection.invokeMethod(
                    ConfigParDisabler.class, "reloadCache",
                    disablePar, hasLogger() ? getLogger() : null
            );
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override public boolean isDisabled() { return super.isDisabled() || !disablePar.isDisabled(); }
    @Override public boolean isSupportChildren()                             { return true; }

    @Override
    public boolean isSupportChild(ConfigSectBase child) {
        return child instanceof ConfigSect || child instanceof ConfigParBase;
    }
}