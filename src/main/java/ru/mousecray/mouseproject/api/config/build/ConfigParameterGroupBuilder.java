package ru.mousecray.mouseproject.api.config.build;

import ru.mousecray.mouseproject.api.DisplayName;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.config.ConfigSectBase;
import ru.mousecray.mouseproject.api.config.pars.ConfigParGroup;
import ru.mousecray.mouseproject.api.config.utils.ConfigDictionary;
import ru.mousecray.mouseproject.api.log.ConsoleColor;
import ru.mousecray.mouseproject.api.utils.MouseReflection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConfigParameterGroupBuilder<T extends Comparable<T>> {
    final MouseConfigBuilder configBuilder;
    final String             path;
    final DisplayName        name;
    boolean canBeDisabled;
    @Nullable String         comment;
    final     ICustomType    valType;
    final     Class<T>       typeClass;
    private   ConfigParGroup group;

    private ConfigParameterGroupBuilder(
            MouseConfigBuilder configBuilder, String path, DisplayName name,
            ICustomType valType, Class<T> typeClass
    ) {
        this.configBuilder = configBuilder;
        this.path = path;
        this.name = name;
        this.valType = valType;
        this.typeClass = typeClass;
    }

    public static <T extends Comparable<T>> ConfigParameterGroupBuilder<T> create(
            MouseConfigBuilder configBuilder, String path, DisplayName name, ICustomType valType, Class<T> typeClass
    ) {
        return new ConfigParameterGroupBuilder<>(
                Objects.requireNonNull(configBuilder),
                Objects.requireNonNull(path),
                Objects.requireNonNull(name),
                Objects.requireNonNull(valType),
                Objects.requireNonNull(typeClass)
        );
    }

    public ConfigParameterGroupQuantityConditionBuilder<T> createConditionGroup() {
        return ConfigParameterGroupQuantityConditionBuilder.create(this);
    }

    public <T2 extends ConfigCustomParGroupBuilder<T, ConfigParameterGroupBuilder<T>>> T2 createGroup(T2 builder) {
        Objects.requireNonNull(builder);
        if (!MouseReflection.setField(
                ConfigCustomParGroupBuilder.class, ConfigParameterGroupBuilder.class,
                "parBuilder", builder, this, configBuilder.logger
        )) {
            throw new RuntimeException("Config building error");
        }
        return builder;
    }

    void setGroup(ConfigParGroup group) {
        if (this.group != null && configBuilder.logger != null) {
            configBuilder.logger.warn(
                    "Value \"" + this.group.getFullInternalName() + "\" in ConfigParGroup \"" + getFullName() +
                            "\" was overwritten by ConfigParGroup \"" + this.group + "\"", "Config", ConsoleColor.YELLOW_BG
            );
        }
        this.group = group;
    }

    public MouseConfigBuilder buildParameterGroup() {
        if (group == null) throw new IllegalStateException("ConfigParameterGroup doesn't contain parameters");
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
                configBuilder.logger.warn("The Parent section of ConfigParameterGroup \"" + getFullName() +
                        "\" has flag \"canBeDisabled\" other then the ConfigParameterGroup value. " +
                        "The value of ConfigParameterGroup has been changed", "Config", ConsoleColor.YELLOW_BG);
            }
        }
        configBuilder.putParameter(path, group);
        return configBuilder;
    }

    @Nullable ConfigDictionary getDictionary() { return configBuilder.dictionary; }
    boolean hasDictionary()                    { return configBuilder.dictionary != null; }
    String getFullName()                       { return path + name.getInternalName(); }
}