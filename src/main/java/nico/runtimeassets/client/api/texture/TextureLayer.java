package nico.runtimeassets.client.api.texture;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;

public final class TextureLayer {
    private final int width;
    private final int height;
    private final int[] data;

    private int offsetX;
    private int offsetY;
    private LayerType layerType;

    private TextureLayer(int width, int height, int[] data) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Invalid texture dimensions");

        if (data.length != width * height)
            throw new IllegalArgumentException("Invalid data length");

        this.width = width;
        this.height = height;
        this.data = data;

        this.layerType = LayerType.NORMAL;
    }

    public static TextureLayer of(int width, int height) {
        return new TextureLayer(width, height, new int[width * height]);
    }

    public static TextureLayer of(Identifier textureId) {
        if (!TextureGetter.check(textureId)) {
            throw new IllegalArgumentException(
                    "Texture not found: " + textureId
            );
        }

        try (NativeImage image = TextureGetter.get(textureId)) {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] data = new int[width * height];

            switch (image.getFormat()) {
                case RGBA -> {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int abgr = image.getColor(x, y);
                            data[x + y * width] = ABGR_to_ARGB(abgr);
                        }
                    }
                }

                case RGB -> {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int abgr = image.getColor(x, y);

                            data[x + y * width] = (0xFF << 24) | ABGR_to_ARGB(abgr);
                        }
                    }
                }

                default -> throw new IllegalArgumentException(
                        "Unsupported image format: " + image.getFormat()
                );
            }

            return new TextureLayer(width, height, data);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load texture: " + textureId, e
            );
        }
    }

    public static TextureLayer of(int width, int height, int offsetX, int offsetY, LayerType layerType, int[] pixels) {
        return new TextureLayer(width, height, pixels).move(offsetX, offsetY).setType(layerType);
    }

    public TextureLayer setColor(int x, int y, int argb) {
        checkBounds(x, y);
        data[x + y * width] = argb;
        return this;
    }

    public int getColor(int x, int y) {
        checkBounds(x, y);
        return data[x + y * width];
    }

    public TextureLayer fill(int x0, int y0, int x1, int y1, int argb) {
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                data[x + y * width] = argb;
            }
        }

        return this;
    }

    public TextureLayer multiply(int argb) {
        return this.multiply(0, 0, this.width, this.height, argb);
    }

    public TextureLayer multiply(int x0, int y0, int x1, int y1, int argb) {
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                data[x + y * width] = LayerType.blendMultiplicative(data[x + y * width], argb);
            }
        }

        return this;
    }

    public TextureLayer invert() {
        return this.invert(0, 0, this.width, this.height);
    }

    public TextureLayer invert(int x0, int y0, int x1, int y1) {
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                int color = data[x + y * width];

                int r = 0xFF - (color >> 16 & 0xFF);
                int g = 0xFF - (color >> 8 & 0xFF);
                int b = 0xFF - (color & 0xFF);

                data[x + y * width] = color & 0xFF_00_00_00 | r << 16 | g << 8 | b;
            }
        }

        return this;
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(
                    "Coordinate outside texture: " + x + ", " + y
            );
        }
    }

    public TextureLayer move(int x, int y) {
        this.offsetX += x;
        this.offsetY += y;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getData() {
        return data;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public LayerType getType() {
        return this.layerType;
    }

    public TextureLayer setType(LayerType layerType) {
        this.layerType = layerType;
        return this;
    }

    private static int ABGR_to_ARGB(int abgr) {
        int alpha = abgr >> 24;
        int red = abgr & 0xFF;
        int green = (abgr >> 8) & 0xFF;
        int blue = abgr >> 16 & 0xFF;

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    /**
     *
     * @param consumer x, y, argb
     */
    public void forEachPixel(TriConsumer<Integer, Integer, Integer> consumer) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = getColor(x, y);

                consumer.accept(x, y, argb);
            }
        }
    }

    public TextureLayer copy() {
        return new TextureLayer(this.width, this.height, this.data).move(this.offsetX, this.offsetY).setType(this.layerType);
    }
}