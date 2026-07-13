package ru.mousecray.mouseproject.api.error;

public class ValueFormatException extends NumberFormatException {
    public ValueFormatException(String message) { super(message); }
    public ValueFormatException()               { super(); }
}