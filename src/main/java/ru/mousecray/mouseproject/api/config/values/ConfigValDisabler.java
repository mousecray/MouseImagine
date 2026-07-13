package ru.mousecray.mouseproject.api.config.values;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mouseproject.api.config.utils.Constraint;
import ru.mousecray.mouseproject.api.config.utils.PredefinedValue;
import ru.mousecray.mouseproject.api.customtype.values.PlusMinusType;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigValDisabler extends ConfigValPlusMinus {
    @SafeVarargs
    public ConfigValDisabler(
            PlusMinusType defaultValue, PlusMinusType disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable PredefinedValue<PlusMinusType>... configureValues
    ) {
        super(
                defaultValue, disabledValue, constraints,
                additionalThreshold, configureValues
        );
    }

    @Override public boolean isDisabled() { return value.isFalse(); }
    public void setDisabled(boolean val)  { setValue(PlusMinusType.create(!val)); }
}