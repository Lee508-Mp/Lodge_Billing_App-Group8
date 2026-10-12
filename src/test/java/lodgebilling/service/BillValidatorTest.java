package lodgebilling.service;

import org.junit.jupiter.api.Test;
import lodgebilling.model.Bill;
import lodgebilling.model.BillField;
import lodgebilling.model.RoomType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The form rules, tested with plain Strings. Before the refactor these rules
 * were buried in the JavaFX class and could only be tried by clicking.
 */
class BillValidatorTest {

    private final BillValidator validator = new BillValidator();

    /** Runs the validator and returns the exception it must throw. */
    private InvalidBillInputException rejected(String guest, RoomType room, String nights,
                                               String extras, String paid) {
        return assertThrows(InvalidBillInputException.class,
                () -> validator.toDraft(guest, room, nights, extras, paid, ""));
    }

    @Test
    void validInputBuildsADraftBill() throws Exception {
        Bill b = validator.toDraft("  Mary Banda ", RoomType.DELUXE, " 3 ", "50", "100", " VIP ");

        assertEquals(Bill.DRAFT_ID, b.getBillId());
        assertEquals("Mary Banda", b.getGuestName());   // trimmed
        assertEquals(RoomType.DELUXE, b.getRoomType());
        assertEquals(3, b.getNights());
        assertEquals(50, b.getExtras());
        assertEquals(100, b.getAmountPaid());
        assertEquals("VIP", b.getNotes());
    }

    @Test
    void emptyExtrasAndPaidMeanZero() throws Exception {
        Bill b = validator.toDraft("Ann", RoomType.STANDARD, "1", "", "   ", null);

        assertEquals(0, b.getExtras());
        assertEquals(0, b.getAmountPaid());
        assertEquals("", b.getNotes());                 // null notes become ""
    }

    @Test
    void overpaymentIsAllowed() throws Exception {
        Bill b = validator.toDraft("Ann", RoomType.STANDARD, "1", "0", "9999", "");
        assertTrue(b.getChange() > 0);
    }

    @Test
    void blankGuestIsRejected() {
        var e = rejected("   ", RoomType.STANDARD, "1", "0", "0");
        assertEquals(BillField.GUEST, e.getField());
        assertEquals("Enter the guest name.", e.getMessage());
    }

    @Test
    void missingRoomIsRejected() {
        assertEquals(BillField.ROOM, rejected("Ann", null, "1", "0", "0").getField());
    }

    @Test
    void nightsMustBeAWholeNumber() {
        for (String bad : new String[]{"", "abc", "2.5", "1e1"}) {
            assertEquals(BillField.NIGHTS,
                    rejected("Ann", RoomType.STANDARD, bad, "0", "0").getField(),
                    "should reject nights = \"" + bad + "\"");
        }
    }

    @Test
    void nightsMustBeAtLeastOne() {
        assertEquals(BillField.NIGHTS, rejected("Ann", RoomType.STANDARD, "0", "0", "0").getField());
        assertEquals(BillField.NIGHTS, rejected("Ann", RoomType.STANDARD, "-2", "0", "0").getField());
    }

    @Test
    void extrasRejectTextNegativesNaNAndInfinity() {
        for (String bad : new String[]{"abc", "-1", "NaN", "Infinity"}) {
            assertEquals(BillField.EXTRAS,
                    rejected("Ann", RoomType.STANDARD, "1", bad, "0").getField(),
                    "should reject extras = \"" + bad + "\"");
        }
    }

    @Test
    void paidRejectsTextNegativesNaNAndInfinity() {
        for (String bad : new String[]{"abc", "-1", "NaN", "Infinity"}) {
            assertEquals(BillField.PAID,
                    rejected("Ann", RoomType.STANDARD, "1", "0", bad).getField(),
                    "should reject paid = \"" + bad + "\"");
        }
    }

    @Test
    void reportsTheFirstProblemInFormOrder() {
        // Everything is wrong, but guest comes first on the form.
        assertEquals(BillField.GUEST, rejected("", null, "x", "x", "x").getField());
        // Guest fixed: now the room is the first problem.
        assertEquals(BillField.ROOM, rejected("Ann", null, "x", "x", "x").getField());
    }
}
