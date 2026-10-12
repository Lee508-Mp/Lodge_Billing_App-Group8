package lodgebilling.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Slide 31: domain tests. Bill has no JavaFX in it, so it needs no window. */
class BillTest {

    private static final double CENTS = 0.001;

    /** Small helper so each test reads as one clear scenario. */
    private Bill bill(RoomType room, int nights, double extras, double paid) {
        return new Bill(Bill.DRAFT_ID, "lucas Mulusa", room, nights, extras, paid, "");
    }

    @Test
    @DisplayName("The lecture exercise: Standard, 2 nights, extras 100, paid 1000")
    void overpaymentExercise() {
        Bill b = bill(RoomType.STANDARD, 2, 100, 1000);

        assertEquals(700, b.getRoomCharge(), CENTS);   // 350 x 2
        assertEquals(0, b.getDiscount(), CENTS);       // under 5 nights
        assertEquals(800, b.getTotal(), CENTS);        // 700 + 100
        assertEquals(0, b.getBalance(), CENTS);        // fully paid
        assertEquals(200, b.getChange(), CENTS);       // 1000 - 800
    }

    @Test
    void noDiscountAtFourNights() {
        Bill b = bill(RoomType.DELUXE, 4, 0, 0);
        assertEquals(0, b.getDiscount(), CENTS);
        assertEquals(2200, b.getTotal(), CENTS);       // 550 x 4
    }

    @Test
    void discountStartsAtFiveNights() {
        Bill b = bill(RoomType.DELUXE, 5, 0, 0);
        assertEquals(137.5, b.getDiscount(), CENTS);   // 5% of 2750
        assertEquals(2612.5, b.getTotal(), CENTS);
    }

    @Test
    void discountAppliesToRoomOnlyNotExtras() {
        Bill b = bill(RoomType.FAMILY, 6, 200, 0);
        assertEquals(225, b.getDiscount(), CENTS);     // 5% of 4500, NOT of 4700
        assertEquals(4475, b.getTotal(), CENTS);       // 4500 - 225 + 200
    }

    @Test
    void balanceIsWhatIsStillOwed() {
        Bill b = bill(RoomType.STANDARD, 2, 0, 300);
        assertEquals(400, b.getBalance(), CENTS);      // 700 - 300
        assertEquals(0, b.getChange(), CENTS);
    }

    @Test
    @DisplayName("Overpaying gives balance 0, never a negative balance")
    void balanceNeverNegative() {
        Bill b = bill(RoomType.STANDARD, 1, 0, 1000);
        assertEquals(0, b.getBalance(), CENTS);
        assertEquals(650, b.getChange(), CENTS);
    }

    @Test
    void exactPaymentHasNoBalanceAndNoChange() {
        Bill b = bill(RoomType.STANDARD, 1, 0, 350);
        assertEquals(0, b.getBalance(), CENTS);
        assertEquals(0, b.getChange(), CENTS);
    }

    @Test
    @DisplayName("Floating-point noise must not create fake change")
    void tinyRoundingErrorIsIgnored() {
        // 0.1 + 0.2 is 0.30000000000000004 in double arithmetic.
        Bill b = bill(RoomType.STANDARD, 1, 0.1 + 0.2, 350.3);
        assertEquals(0, b.getChange(), 0.0);
        assertEquals(0, b.getBalance(), 0.0);
    }

    @Test
    @DisplayName("withBillId returns a NEW Bill and leaves the original alone")
    void withBillIdKeepsBillImmutable() {
        Bill draft = bill(RoomType.DELUXE, 3, 50, 100);
        Bill saved = draft.withBillId("B007");

        assertNotSame(draft, saved);
        assertEquals(Bill.DRAFT_ID, draft.getBillId());   // original unchanged
        assertEquals("B007", saved.getBillId());
        assertEquals(draft.getGuestName(), saved.getGuestName());
        assertEquals(draft.getTotal(), saved.getTotal(), CENTS);
    }

    @Test
    void receiptShowsChangeLineOnlyWhenThereIsChange() {
        String withChange = bill(RoomType.STANDARD, 1, 0, 400).toReceiptText();
        String noChange = bill(RoomType.STANDARD, 1, 0, 100).toReceiptText();

        assertTrue(withChange.contains("Change given: K 50.00"));
        assertFalse(noChange.contains("Change given"));
    }

    @Test
    void receiptShowsNotesOnlyWhenPresent() {
        Bill withNotes = new Bill("B001", "Mary", RoomType.STANDARD, 1, 0, 0, "Late arrival");
        assertTrue(withNotes.toReceiptText().contains("Notes: Late arrival"));
        assertFalse(bill(RoomType.STANDARD, 1, 0, 0).toReceiptText().contains("Notes:"));
    }
}
