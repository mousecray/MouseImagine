package ru.mousecray.mouseproject.api.customtype.values;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.customtype.CustomType;
import ru.mousecray.mouseproject.api.customtype.ListType;
import ru.mousecray.mouseproject.api.error.UnsupportedValException;
import ru.mousecray.mouseproject.api.log.ConsoleColor;
import ru.mousecray.mouseproject.api.log.MouseLogger;
import ru.mousecray.mouseproject.api.utils.MouseCollections;
import ru.mousecray.mouseproject.api.utils.MouseLambdas;
import ru.mousecray.mouseproject.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class SimpleListType<T extends CustomType<?>> extends ListType<T, T> {
    private SimpleListType(Class<T> valClass, @Nullable Function<String, T> valCreator) {
        super(checkValClass(valClass), valCreator);
    }

    private SimpleListType(Class<T> valClass, @Nullable Function<String, T> valCreator, List<T> values) {
        super(checkValClass(valClass), valCreator, values);
    }

    private static <T extends CustomType<?>> Class<T> checkValClass(Class<T> valClass) {
        if (ListType.class.isAssignableFrom(valClass)) {
            throw new UnsupportedValException("Enclosing ListTypes is not support");
        }
        return valClass;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            @Nonnull T... values
    ) {
        return new SimpleListType<>(
                valClass, valCreator,
                MouseCollections.mapAsList(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            List<T> values
    ) {
        return new SimpleListType<>(
                valClass, valCreator,
                MouseCollections.map(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(Class<T> valClass, List<T> values) {
        return create(valClass, null, values);
    }

    @SafeVarargs
    public static <T extends CustomType<T>> SimpleListType<T> create(Class<T> valClass, @Nonnull T... values) {
        return create(valClass, null, values);
    }

    @SuppressWarnings("unchecked") @Override
    protected <TYPE extends ListType<T, T>> TYPE createType(List<T> list) {
        return (TYPE) create(getValClass(), getValCreator(), list);
    }

    @Override public boolean containsOriginalValue(@Nullable T value) { return containsValue(value); }

    public static <T extends CustomType<T>> SimpleListType<T> copyExcludeValCreator(
            SimpleListType<T> list, Function<String, T> valCreator
    ) {
        return new SimpleListType<>(list.getValClass(), valCreator, list.getList());
    }


    public static <T extends CustomType<?>> SimpleListType<T> fromString(
            Class<T> valClass,
            @Nullable String rawString,
            @Nullable Function<String, T> valCreator,
            @Nullable MouseLogger logger
    ) {
        if (rawString == null) return create(valClass, valCreator);

        SimpleListType<T> list  = create(valClass, valCreator);
        String[]          split = rawString.split(",");
        for (String s : split) {
            String str = MouseStrings.trimWith(s, true, '\t');
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("\t")) {
                    try {
                        T val;
                        if (valCreator != null) val = valCreator.apply(str);
                        else val = CustomType.parse(valClass, str);

                        if (val != null) list.addValue(val);
                        else {
                            if (logger != null) logger.warn("ListType skipped null value '" + str + "'",
                                    "customtype", ConsoleColor.YELLOW_BG);
                        }
                    } catch (NumberFormatException e) {
                        if (logger != null) logger.warn("ListType skipped broken or unsupported value '" + str + "'",
                                "customtype", ConsoleColor.YELLOW_BG);
                    }
                }
            }
        }

        return list;
    }
}