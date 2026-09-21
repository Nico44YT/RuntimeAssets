package nico.runtimeassets.client.api.texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Texture {
    private final TextureLayer[] layers;

    private final int width;
    private final int height;

    public Texture(TextureLayer... layers) {
        this(layers[0].getWidth(), layers[0].getHeight(), layers);
    }

    public Texture(int width, int height, TextureLayer... layers) {
        this.layers = layers;

        this.width = width;
        this.height = height;
    }

    private int[] composite(TextureLayer[] layers) {
        int[] pixels = new int[width * height];

        for (TextureLayer layer : layers) {
            layer.forEachPixel((localX, localY, color) -> {
                int x = localX + layer.getOffsetX();
                int y = localY + layer.getOffsetY();

                if (x < 0 || x >= width || y < 0 || y >= height) {
                    return;
                }

                int index = y * width + x;
                int base = pixels[index];

                pixels[index] = switch (layer.getType()) {
                    case NORMAL -> LayerType.blendNormal(base, color);
                    case ADDITIVE -> LayerType.blendAdditive(base, color);
                    case SUBTRACTIVE -> LayerType.blendSubtractive(base, color, true);
                    case SUBTRACTIVE_NO_ALPHA -> LayerType.blendSubtractive(base, color, false);
                    case MULTIPLICATIVE -> LayerType.blendMultiplicative(base, color);
                    case BLEND_MASK -> LayerType.blendMask(base, color);
                };
            });
        }

        return pixels;
    }

    public TextureLayer compose(TextureLayer... layers) {
        int[] pixels = composite(layers);

        return TextureLayer.of(
                width,
                height,
                0,
                0,
                LayerType.NORMAL,
                pixels
        );
    }

    public BakedTexture bake() {
        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = composite(layers);
        image.setRGB(0, 0, width, height, pixels, 0, width);


        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);

            return new BakedTexture(
                    width,
                    height,
                    out.toByteArray()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to bake texture", e);
        }
    }
}
