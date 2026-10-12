package lodgebilling.model;

/**
 * The input fields of the billing form. The validator says WHICH field is
 * wrong using this enum, and the view decides how to point at it (focus).
 * That way the service never needs to know about JavaFX controls.
 */
public enum BillField {
    GUEST, ROOM, NIGHTS, EXTRAS, PAID
}
