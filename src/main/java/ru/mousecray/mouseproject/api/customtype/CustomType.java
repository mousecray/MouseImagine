package ru.mousecray.mouseproject.api.customtype;


import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class CustomType<T extends CustomType<T>> implements Comparable<T> {
    private final CustomValType       valType;
    private final CustomCast<T>       customCast       = createCastPipeline();
    private final CustomArithmetic<T> customArithmetic = createArithmeticPipeline();
    private final CustomLogic<T>      customLogic      = createLogicPipeline();
    private final CustomBitwise<T>    customBitwise    = createBitwisePipeline();

    public final CustomCast<T> getCastPipeline()                     { return customCast; }
    public final CustomArithmetic<T> getArithmeticPipeline()         { return customArithmetic; }
    public final CustomLogic<T> getLogicPipeline()                   { return customLogic; }
    public final CustomBitwise<T> getBitwisePipeline()               { return customBitwise; }

    protected CustomType(CustomValType valType)                      { this.valType = Objects.requireNonNull(valType); }
    protected CustomType()                                           { valType = CustomValType.UNDEFINED; }

    protected CustomCast<T> createCastPipeline()                     { return CustomCast.DEFAULT(); }
    protected CustomArithmetic<T> createArithmeticPipeline()         { return CustomArithmetic.DEFAULT(); }
    protected CustomLogic<T> createLogicPipeline()                   { return CustomLogic.DEFAULT(); }
    protected CustomBitwise<T> createBitwisePipeline()               { return CustomBitwise.DEFAULT(); }

    public abstract ListType<?, ?> asListType();
    public abstract LogicalType<?> asLogicalType();
    public abstract NumberType<?> asNumberType();
    public abstract OtherType<?> asOtherType();

    @SuppressWarnings("unchecked") protected Class<T> getTypeClass() { return (Class<T>) getClass(); }

    protected static final Map<Class<? extends CustomType<?>>, Function<String, ? extends CustomType<?>>> storage = new HashMap<>();

    @SuppressWarnings("unchecked") @Nullable
    public static <T> T parse(Class<T> clazz, @Nullable String val) {
        Function<String, ? extends CustomType<?>> result = storage.get(clazz);
        if (result != null) {
            try {
                return (T) result.apply(val);
            } catch (ClassCastException ignore) { }
        }
        return null;
    }
}