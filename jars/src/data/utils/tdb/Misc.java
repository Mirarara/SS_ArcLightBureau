package data.utils.tdb;

public class Misc {
    public static String getDigitValue(float value) {
        return getDigitValue(value, 1);
    }

    public static String getDigitValue(float value, int digit) {
        if (Math.abs((float) Math.round(value) - value) < 0.0001f) {
            return String.format("%d", Math.round(value));
        } else {
            return String.format("%." + digit + "f", value);
        }
    }
}
