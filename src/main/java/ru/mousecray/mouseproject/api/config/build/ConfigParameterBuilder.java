package ru.mousecray.mouseproject.api.config.build;

import ru.mousecray.mouseproject.api.DisplayName;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.ConfigSectBase;
import ru.mousecray.mouseproject.api.config.ConfigVal;
import ru.mousecray.mouseproject.api.config.pars.ConfigPar;
import ru.mousecray.mouseproject.api.log.ConsoleColor;
import ru.mousecray.mouseproject.api.utils.MouseReflection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigParameterBuilder<T extends Comparable<T>> extends AbstractConfigParameterBuilder<ConfigParameterBuilder<T>> {
    private ConfigVal value;

    private ConfigParameterBuilder(MouseConfigBuilder configBuilder, String path, DisplayName name) {
        super(configBuilder, path, name);
    }

    static <T extends Comparable<T>> ConfigParameterBuilder<T> create(
            MouseConfigBuilder configBuilder, DisplayName name, String path
    ) {
        return new ConfigParameterBuilder<>(
                Objects.requireNonNull(configBuilder),
                Objects.requireNonNull(path),
                Objects.requireNonNull(name)
        );
    }

    public ConfigPlusMinusBuilder<ConfigParameterBuilder<T>> createPlusMinus()           { return ConfigPlusMinusBuilder.create(this); }
    public ConfigIntegerBuilder<ConfigParameterBuilder<T>> createInteger()               { return ConfigIntegerBuilder.create(this); }
    public ConfigDecimalBuilder<ConfigParameterBuilder<T>> createDecimal()               { return ConfigDecimalBuilder.create(this); }
    public ConfigRandomQuantityBuilder<ConfigParameterBuilder<T>> createRandomQuantity() { return ConfigRandomQuantityBuilder.create(this); }
    public ConfigPercentBuilder<ConfigParameterBuilder<T>> createPercent()               { return ConfigPercentBuilder.create((this)); }
    public ConfigStringBuilder<ConfigParameterBuilder<T>> createString()                 { return ConfigStringBuilder.create(this); }

    public ConfigConditionBuilder<T, ConfigParameterBuilder<T>> createCondition(ICustomType valType, Class<T> typeClass) {
        return ConfigConditionBuilder.create(this, valType, typeClass);
    }

    public ConfigListBuilder<T, ConfigParameterBuilder<T>> createList(ICustomType listValType, Class<T> listTypeClass) {
        return ConfigListBuilder.create(this, listValType, listTypeClass);
    }

    public <T2 extends ConfigValueBuilder<ConfigParameterBuilder<T>>> T2 createValue(T2 builder) {
        Objects.requireNonNull(builder);
        if (!MouseReflection.setField(
                ConfigValueBuilder.class, AbstractConfigParameterBuilder.class,
                "parBuilder", builder, this, configBuilder.logger
        )) {
            throw new RuntimeException("Config building error");
        }
        return builder;
    }

    @Override
    protected void setValue(ConfigVal value) {
        if (this.value != null && configBuilder.logger != null) {
            configBuilder.logger.warn(
                    "ConfigValue \"" + this.value + "\" in ConfigParameter \"" + getFullName() +
                            "\" was overwritten by ConfigValue \"" + value + "\"", "Config", ConsoleColor.YELLOW_BG
            );
        }
        this.value = value;
    }

    public MouseConfigBuilder buildParameter() {
        if (value == null) throw new IllegalStateException("ConfigParameter doesn't contain a value");
        String               parentPath = path.substring(0, path.length() - 1);
        List<ConfigSectBase> list       = configBuilder.sections.get(parentPath.substring(0, parentPath.lastIndexOf('.') + 1));
        ConfigSectBase       parent     = null;
        for (ConfigSectBase base : list) {
            if (base.getName().getInternalName().equals(parentPath.substring(parentPath.lastIndexOf(".") + 1))) {
                parent = base;
            }
        }
        if (parent != null && parent.canBeDisabled() != canBeDisabled) {
            canBeDisabled = parent.canBeDisabled();
            if (configBuilder.logger != null && !configBuilder.autoDisable) {
                configBuilder.logger.warn("The Parent section of ConfigParameter \"" + getFullName() +
                        "\" has flag \"canBeDisabled\" other then the ConfigParameter value. " +
                        "The value of ConfigParameter has been changed", "Config", ConsoleColor.YELLOW_BG);
            }
        }
        configBuilder.putParameter(path, new ConfigPar(name, canBeDisabled, value, comment));
        return configBuilder;
    }
}