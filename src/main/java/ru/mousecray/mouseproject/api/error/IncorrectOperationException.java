package ru.mousecray.mouseproject.api.error;

public class IncorrectOperationException extends RuntimeException {
    public IncorrectOperationException(String message) { super(message); }
}