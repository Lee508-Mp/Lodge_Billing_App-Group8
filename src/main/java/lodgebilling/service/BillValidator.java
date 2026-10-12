package lodgebilling.service;

import lodgebilling.model.Bill;
import lodgebilling.model.BillField;
import lodgebilling.model.RoomType;

/**
 * SERVICE: turns the raw text from the form into a draft Bill, or explains
 * what is wrong. It used to be readForm() inside the app class; it moved here
 * because the rules should not depend on buttons or text fields (slides 10, 24)
 * and because a class with no JavaFX in it is easy to unit test (slide 31).
 *
 * It checks fields in order and stops at the first problem.
 */
public class BillValidator {

    public Bill toDraft(String guest, RoomType room, String nights,
                        String extras, String paid, String notes)
            throws InvalidBillInputException {

        String guestName = clean(guest);
        if (guestName.isEmpty()) {
            throw new InvalidBillInputException("Enter the guest name.", BillField.GUEST);
        }

        if (room == null) {
            throw new InvalidBillInputException("Choose a room type.", BillField.ROOM);
        }

        int nightCount;
        try {
            nightCount = Integer.parseInt(clean(nights));
        } catch (NumberFormatException e) {
            throw new InvalidBillInputException(
                    "Enter nights as a whole number, e.g. 3.", BillField.NIGHTS);
        }
        if (nightCount < 1) {
            throw new InvalidBillInputException("Nights must be at least 1.", BillField.NIGHTS);
        }

        double extrasAmount = parseMoney(extras,
                "Extras must be a number, 0 or more.", BillField.EXTRAS);
        double paidAmount = parseMoney(paid,
                "Amount paid must be a number, 0 or more.", BillField.PAID);

        // Paying MORE than the total is allowed: the Bill works out the change.
        return new Bill(Bill.DRAFT_ID, guestName, room, nightCount,
                extrasAmount, paidAmount, clean(notes));
    }

    /** Empty means 0. Text, negatives, NaN and Infinity are rejected. */
    private double parseMoney(String text, String message, BillField field)
            throws InvalidBillInputException {
        String trimmed = clean(text);
        if (trimmed.isEmpty()) {
            return 0;
        }
        try {
            double value = Double.parseDouble(trimmed);
            if (Double.isFinite(value) && value >= 0) {
                return value;
            }
        } catch (NumberFormatException e) {
            // falls through to the exception below
        }
        throw new InvalidBillInputException(message, field);
    }

    /** null-safe trim, so a missing text is treated like an empty one. */
    private String clean(String text) {
        return text == null ? "" : text.trim();
    }
}
