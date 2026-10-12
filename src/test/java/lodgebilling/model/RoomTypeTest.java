package lodgebilling.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomTypeTest {

    @Test
    void pricesMatchTheLabSheet() {
        assertEquals(350.0, RoomType.STANDARD.getRatePerNight());
        assertEquals(550.0, RoomType.DELUXE.getRatePerNight());
        assertEquals(750.0, RoomType.FAMILY.getRatePerNight());
    }

    @Test
    void toStringIsTheFriendlyLabelUsedByTheComboBox() {
        assertEquals("Deluxe", RoomType.DELUXE.toString());
    }
}
