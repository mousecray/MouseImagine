package ru.mousecray.mouseproject.api.error;

public class UnsupportedValException extends NumberFormatException {
    public UnsupportedValException()               { super(); }
    public UnsupportedValException(String message) { super(message); }
}