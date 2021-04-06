package com.pseuco.np20.validator;

/**
 * Should be thrown if the padding is insufficient.
 */
public class InsufficientPaddingException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int padding;

    /**
     * Creates an insufficient padding exception.
     *
     * @param padding The padding that is insufficient.
     */
    public InsufficientPaddingException(int padding) {
        super("padding of " + padding + " is insufficient");
        this.padding = padding;
    }

    public int getPadding() {
        return this.padding;
    }
}