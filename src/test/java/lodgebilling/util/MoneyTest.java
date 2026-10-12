package lodgebilling.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTest {

    @Test
    void formatsWithKAndTwoDecimals() {
        assertEquals("K 2812.50", Money.format(2812.5));
        assertEquals("K 0.00", Money.format(0));
    }

    @Test
    void roundsToCents() {
        assertEquals("K 10.01", Money.format(10.005001));
    }

    @Test
    void alwaysUsesADotEvenOnACommaLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);       // Germans write 1,5
            assertEquals("K 1.50", Money.format(1.5));
        } finally {
            Locale.setDefault(original);             // never leak into other tests
        }
    }
}
