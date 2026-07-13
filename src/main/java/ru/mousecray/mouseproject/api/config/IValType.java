package ru.mousecray.mouseproject.api.config;

import ru.mousecray.mouseproject.api.customtype.CustomValType;

import javax.annotation.Nonnull;

public interface IValType {
    @Nonnull String getDisplayName();
    @Nonnull CustomValType getValType();
}
