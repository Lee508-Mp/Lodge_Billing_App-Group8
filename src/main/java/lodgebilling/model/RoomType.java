package lodgebilling.model;

/**
 * WHY an enum? The lodge has a fixed set of room types, so a ComboBox of
 * RoomType gives "Known choices" (slide 6) and the compiler stops typos like
 * "Delux". The price lives here, in ONE place: the bill maths and the
 * "Practice room prices" panel both read it from this enum.
 */
public enum RoomType {

    STANDARD("Standard", 350.00),
    DELUXE("Deluxe", 550.00),
    FAMILY("Family", 750.00);

    private final String label;
    private final double ratePerNight;

    RoomType(String label, double ratePerNight) {
        this.label = label;
        this.ratePerNight = ratePerNight;
    }

    public String getLabel() {
        return label;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    // ComboBox and TableView show an object using toString(),
    // so this makes them display "Deluxe" instead of "DELUXE".
    @Override
    public String toString() {
        return label;
    }
}
