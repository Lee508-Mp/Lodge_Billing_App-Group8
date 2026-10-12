package lodgebilling.controller;

import lodgebilling.model.Bill;
import lodgebilling.service.BillService;
import lodgebilling.service.BillValidator;
import lodgebilling.service.InvalidBillInputException;
import lodgebilling.ui.BillingView;
import lodgebilling.util.Money;

/**
 * CONTROLLER: reacts to the billing screen's buttons.
 *
 * Each handle...() method reads like a story: read the form, call the
 * service, show the result. Thin on purpose: the rules live in Bill,
 * BillValidator and BillService. To change WHAT a button does, edit this
 * file only. To change how the screen LOOKS, edit BillingView.
 */
public class BillingController {

    private final BillingView view;
    private final BillService service;
    private final BillValidator validator;

    public BillingController(BillingView view, BillService service, BillValidator validator,
                             Runnable onLogout, Runnable onClose) {
        this.view = view;
        this.service = service;
        this.validator = validator;

        // The table shows the same list the service updates.
        view.setBills(service.getBills());

        // Connect every control to a handler.
        view.onCalculate(this::handleCalculate);
        view.onSave(this::handleSaveBill);
        view.onNewGuest(this::handleNewGuest);
        view.onViewReceipt(this::handleViewReceipt);
        view.onDeleteBill(this::handleDeleteBill);
        view.onAbout(view::showAbout);

        // Leaving the screen is the app's job (it owns the Stage).
        view.onLogout(onLogout);
        view.onClose(onClose);
    }

    /**
     * Asks the validator for a draft Bill. On a user mistake it shows the
     * message, moves focus to the bad field and returns null. It never clears
     * the form, so the user keeps what they typed.
     */
    private Bill readForm() {
        try {
            return validator.toDraft(
                    view.getGuestName(), view.getRoomType(), view.getNights(),
                    view.getExtras(), view.getAmountPaid(), view.getNotes());
        } catch (InvalidBillInputException e) {
            view.setStatus(e.getMessage());
            view.focusField(e.getField());
            return null;
        }
    }

    /** Shows the working without saving anything. */
    private void handleCalculate() {
        Bill draft = readForm();
        if (draft == null) {
            return;
        }
        String text = "Room: " + Money.format(draft.getRoomCharge())
                + "   Discount: " + Money.format(draft.getDiscount())
                + "   Extras: " + Money.format(draft.getExtras())
                + "   Total: " + Money.format(draft.getTotal())
                + "   Balance: " + Money.format(draft.getBalance());
        if (draft.getChange() > 0) {
            text += "   Change: " + Money.format(draft.getChange());
        }
        view.setStatus(text);
    }

    /** Validate, then store. The form keeps its values; "New guest" clears it. */
    private void handleSaveBill() {
        Bill draft = readForm();
        if (draft == null) {
            return;
        }
        Bill saved = service.addBill(draft);
        if (saved.getChange() > 0) {
            view.setStatus("Saved " + saved.getBillId()
                    + ". Fully paid. Change to give: " + Money.format(saved.getChange()));
        } else {
            view.setStatus("Saved " + saved.getBillId()
                    + ". Balance: " + Money.format(saved.getBalance()));
        }
    }

    private void handleNewGuest() {
        view.clearForm();
    }

    private void handleViewReceipt() {
        Bill selected = view.getSelectedBill();
        if (selected == null) {
            view.setStatus("Select a bill first.");
            return;
        }
        view.showReceipt(selected);
    }

    /** Check selection, confirm, then delete. Cancel or closing keeps the bill. */
    private void handleDeleteBill() {
        Bill selected = view.getSelectedBill();
        if (selected == null) {
            view.setStatus("Select a bill first.");
            return;
        }
        if (view.confirmDelete(selected)) {
            service.removeBill(selected);
            view.setStatus("Deleted " + selected.getBillId() + ".");
        } else {
            view.setStatus("Deletion cancelled.");
        }
    }
}
