package lodgebilling.model;

import lodgebilling.util.Money;

/**
 * MODEL: one guest's bill. Immutable, (all fields final).
 *
 * The inputs (guest, room, nights, extras, paid) are stored.
 * The results (room charge, discount, total, balance) are CALCULATED from
 * them, never stored. That way they can never get out of sync.
 *
 * Rules (from the screen):
 *   - 5 nights or more: 5% off the ROOM charge only
 *   - Extras get no discount, and there is no tax
 */
public class Bill {

    public static final int DISCOUNT_MIN_NIGHTS = 5;
    public static final double DISCOUNT_PERCENT = 5.0;

    // A bill that has been calculated but not saved yet has this id.
    public static final String DRAFT_ID = "DRAFT";

    private final String billId;
    private final String guestName;
    private final RoomType roomType;
    private final int nights;
    private final double extras;
    private final double amountPaid;
    private final String notes;

    public Bill(String billId, String guestName, RoomType roomType,
                int nights, double extras, double amountPaid, String notes) {
        this.billId = billId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.extras = extras;
        this.amountPaid = amountPaid;
        this.notes = notes;
    }

    /**
     * An immutable object can't have its id changed, so we return a NEW
     * Bill with the same data and a different id. The service uses this
     * to turn a draft into a saved bill.
     */
    public Bill withBillId(String newId) {
        return new Bill(newId, guestName, roomType, nights, extras, amountPaid, notes);
    }

    // ---------- getters (PropertyValueFactory reads these by name) ----------

    public String getBillId() { return billId; }
    public String getGuestName() { return guestName; }
    public RoomType getRoomType() { return roomType; }
    public int getNights() { return nights; }
    public double getExtras() { return extras; }
    public double getAmountPaid() { return amountPaid; }
    public String getNotes() { return notes; }

    // ---------- calculated values ----------

    public double getRoomCharge() {
        return roomType.getRatePerNight() * nights;
    }

    public double getDiscount() {
        if (nights >= DISCOUNT_MIN_NIGHTS) {
            return getRoomCharge() * DISCOUNT_PERCENT / 100;
        }
        return 0;
    }

    public double getTotal() {
        return getRoomCharge() - getDiscount() + extras;
    }

    /** What the guest still owes. Never negative: overpaying gives 0, not -200. */
    public double getBalance() {
        return round2(Math.max(0, getTotal() - amountPaid));
    }

    /** Money to hand back when the guest paid MORE than the total. */
    public double getChange() {
        return round2(Math.max(0, amountPaid - getTotal()));
    }

    // Rounds to 2 decimals (cents) so tiny floating-point noise like
    // 0.0000000001 can't make the app think there is change to give.
    private static double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }

    /** Plain-text receipt shown by the "View receipt" button. */
    public String toReceiptText() {
        String text = """
                Guest: %s
                Room: %s (%s per night)
                Nights: %d

                Room charge: %s
                Discount (%d%%): -%s
                Extras: %s
                Total: %s

                Paid: %s
                Balance: %s
                """.formatted(
                guestName,
                roomType.getLabel(), Money.format(roomType.getRatePerNight()),
                nights,
                Money.format(getRoomCharge()),
                (int) DISCOUNT_PERCENT, Money.format(getDiscount()),
                Money.format(extras),
                Money.format(getTotal()),
                Money.format(amountPaid),
                Money.format(getBalance()));

        if (getChange() > 0) {
            text += "Change given: " + Money.format(getChange()) + "\n";
        }
        if (!notes.isEmpty()) {
            text += "\nNotes: " + notes;
        }
        return text;
    }
}
