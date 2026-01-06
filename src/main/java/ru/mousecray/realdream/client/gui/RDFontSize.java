package ru.mousecray.realdream.client.gui;

public enum RDFontSize {
    NORMAL(1.0f), SMALL(1.0f), LARGE(1.5f), EXTRA_LARGE(2.0f);

    private final float vanillaScale;
    RDFontSize(float vanillaScale) { this.vanillaScale = vanillaScale; }
    public float getScale()        { return vanillaScale; }
}