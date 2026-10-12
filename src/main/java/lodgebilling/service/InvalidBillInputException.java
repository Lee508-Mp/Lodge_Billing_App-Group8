package lodgebilling.service;

import lodgebilling.model.BillField;

/**
 * A USER mistake (slide 28): the form held something that can't become a Bill.
 * It is a checked exception because the caller can recover: show the message
 * and put the cursor in the bad field. It carries the message AND the field.
 */
public class InvalidBillInputException extends Exception {

    private static final long serialVersionUID = 1L;

    private final BillField field;

    public InvalidBillInputException(String message, BillField field) {
        super(message);
        this.field = field;
    }

    public BillField getField() {
        return field;
    }
}
