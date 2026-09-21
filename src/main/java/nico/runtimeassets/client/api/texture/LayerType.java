package nico.runtimeassets.client.api.texture;

public enum LayerType {
    NORMAL,
    ADDITIVE,
    SUBTRACTIVE,
    SUBTRACTIVE_NO_ALPHA,
    MULTIPLICATIVE,
    BLEND_MASK;

    public static int blendNormal(int base, int layer) {
        int alpha = (layer >>> 24) & 0xFF;

        if (alpha == 255) {
            return layer;
        }

        if (alpha == 0) {
            return base;
        }

        return alphaBlend(base, layer);
    }

    public static int blendAdditive(int base, int layer) {
        int a = Math.min(255,
                ((base >>> 24) & 0xFF) + ((layer >>> 24) & 0xFF));

        int r = Math.min(255,
                ((base >>> 16) & 0xFF) + ((layer >>> 16) & 0xFF));

        int g = Math.min(255,
                ((base >>> 8) & 0xFF) + ((layer >>> 8) & 0xFF));

        int b = Math.min(255,
                (base & 0xFF) + (layer & 0xFF));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendSubtractive(int base, int layer, boolean includeAlpha) {
        int a = Math.max(0,
                ((base >>> 24) & 0xFF) - ((layer >>> 24) & 0xFF));

        int r = Math.max(0,
                ((base >>> 16) & 0xFF) - ((layer >>> 16) & 0xFF));

        int g = Math.max(0,
                ((base >>> 8) & 0xFF) - ((layer >>> 8) & 0xFF));

        int b = Math.max(0,
                (base & 0xFF) - (layer & 0xFF));

        if (includeAlpha) {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        return (base & 0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendMultiplicative(int base, int layer) {
        int a = multiply(
                (base >>> 24) & 0xFF,
                (layer >>> 24) & 0xFF
        );

        int r = multiply(
                (base >>> 16) & 0xFF,
                (layer >>> 16) & 0xFF
        );

        int g = multiply(
                (base >>> 8) & 0xFF,
                (layer >>> 8) & 0xFF
        );

        int b = multiply(
                base & 0xFF,
                layer & 0xFF
        );

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendMask(int base, int mask) {
        int baseAlpha = (base >>> 24) & 0xFF;
        int maskAlpha = (mask >>> 24) & 0xFF;

        int alpha = multiply(baseAlpha, maskAlpha);

        return (base & 0x00FFFFFF) | (alpha << 24);
    }

    public static int alphaBlend(int base, int layer) {
        int layerAlpha = (layer >>> 24) & 0xFF;
        int baseAlpha = (base >>> 24) & 0xFF;

        int inverseAlpha = 255 - layerAlpha;

        int alpha = layerAlpha + multiply(baseAlpha, inverseAlpha);

        if (alpha == 0) {
            return 0;
        }

        int r = (
                ((layer >>> 16) & 0xFF) * layerAlpha
                        + ((base >>> 16) & 0xFF) * baseAlpha * inverseAlpha / 255
        ) / alpha;

        int g = (
                ((layer >>> 8) & 0xFF) * layerAlpha
                        + ((base >>> 8) & 0xFF) * baseAlpha * inverseAlpha / 255
        ) / alpha;

        int b = (
                (layer & 0xFF) * layerAlpha
                        + (base & 0xFF) * baseAlpha * inverseAlpha / 255
        ) / alpha;

        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    public static int multiply(int a, int b) {
        return (a * b + 127) / 255;
    }
}
