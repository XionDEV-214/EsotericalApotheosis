package com.esotericalapotheosis;

import dev.shadowsoffire.placebo.color.GradientColor;

import java.util.Locale;

public class SyncSafeGradientColor extends GradientColor {

    public SyncSafeGradientColor(int[] gradient, String id, float speed) {
        super(gradient, id, speed);
    }

    @Override
    public String serialize() {
        return String.format(Locale.ROOT, "#%06X", this.gradient[0] & 0xFFFFFF);
    }
}
