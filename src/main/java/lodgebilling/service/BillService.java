package lodgebilling.service;

import lodgebilling.model.Bill;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * SERVICE: owns the saved bills and hands out bill numbers (B001, B002...).
 *
 * The ObservableList is the same one the TableView is attached to, so
 * add/remove here updates the table by itself (slide 12). Never replace it
 * with a new list.
 *
 * Data is in memory only: "Saved bills last only until you close the app."
 */
public class BillService {

    private final ObservableList<Bill> bills = FXCollections.observableArrayList();

    // Only goes up, so a deleted bill's number is never reused.
    // (That is why a table can show B002 and B003 but no B001.)
    private int nextNumber = 1;

    public ObservableList<Bill> getBills() {
        return bills;
    }

    /** Gives the draft a real bill number and stores it. */
    public Bill addBill(Bill draft) {
        String id = String.format("B%03d", nextNumber);
        nextNumber++;
        Bill saved = draft.withBillId(id);
        bills.add(saved);
        return saved;
    }

    public void removeBill(Bill bill) {
        bills.remove(bill);
    }
}
