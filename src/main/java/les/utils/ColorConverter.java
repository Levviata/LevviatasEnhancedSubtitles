package les.utils;

public class ColorConverter {
    public static int colorToDecimalWithAlpha(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
    public static int colorToDecimal(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }
}
