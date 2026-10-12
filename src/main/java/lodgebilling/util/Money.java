package lodgebilling.util;

import java.util.Locale;

/**
 * One place that decides how money looks on screen: "K 2812.50".
 * Locale.US forces a dot as the decimal separator, so the output is the same
 * on every Windows language setting (and matches what the user types).
 */
public final class Money {

    private Money() {
        // utility class: nobody should create a Money object
    }

    public static String format(double amount) {
        return String.format(Locale.US, "K %.2f", amount);
    }
}
