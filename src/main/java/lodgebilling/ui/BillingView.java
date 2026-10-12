package lodgebilling.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lodgebilling.model.Bill;
import lodgebilling.model.BillField;
import lodgebilling.model.RoomType;
import lodgebilling.util.Money;

import java.util.function.Function;

/**
 * VIEW ONLY: how the billing screen LOOKS .
 *
 * The build...() methods create the controls. Everything else is a small
 * method the controller can call ("read the nights text", "show this status",
 * "ask the user to confirm"). No business rules and no AuthService/BillService
 * in here. To move a button or change a label, edit this file only.
 */
public class BillingView {

    private final String currentUser;       // only shown in the toolbar

    // form
    private TextField guestField;
    private ComboBox<RoomType> roomBox;
    private TextField nightsField;
    private TextField extrasField;
    private TextField paidField;
    private TextArea notesArea;
    private Label status;
    private TableView<Bill> table;

    // things the user can click (the controller attaches the actions)
    private Button calculateButton;
    private Button saveButton;
    private Button newGuestButton;
    private Button viewReceiptButton;
    private Button deleteButton;
    private MenuItem newGuestItem;
    private MenuItem logoutItem;
    private MenuItem closeItem;
    private MenuItem aboutItem;

    public BillingView(String currentUser) {
        this.currentUser = currentUser;
    }

    /** Builds the whole screen. Call once, then give the view to a controller. */
    public Parent build() {
        BorderPane root = new BorderPane();
        root.setTop(new VBox(buildMenuBar(), buildToolBar()));
        root.setCenter(buildCenter());
        return root;
    }

    // =====================================================================
    //  BUILDING THE SCREEN
    // =====================================================================

    private MenuBar buildMenuBar() {
        newGuestItem = new MenuItem("New guest");
        logoutItem = new MenuItem("Log out");
        closeItem = new MenuItem("Close");
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newGuestItem, logoutItem, closeItem);

        aboutItem = new MenuItem("About");
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private ToolBar buildToolBar() {
        newGuestButton = new Button("New guest");
        viewReceiptButton = new Button("View receipt");
        deleteButton = new Button("Delete selected bill");

        // The spacer pushes the "Logged in as" label to the far right.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label loggedInAs = new Label("Logged in as: " + currentUser);

        return new ToolBar(newGuestButton, viewReceiptButton, deleteButton, spacer, loggedInAs);
    }

    /** Title, form + price panel, status line and the saved-bills table. */
    private VBox buildCenter() {
        Label title = new Label("Lodge Billing App");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.getStyleClass().add("title-label");     // styled in lodge.css

        HBox subtitle = new HBox(14,
                new Label("Group Assignment"),
                new Label("Group 8"),
                new Label("Mulungushi University"));

        // Form on the left, price list on the right.
        GridPane form = buildForm();
        VBox prices = buildPricePanel();
        HBox top = new HBox(24, form, prices);
        HBox.setHgrow(form, Priority.ALWAYS);

        status = new Label();
        status.setWrapText(true);
        status.setFont(Font.font("System", FontWeight.BOLD, 13));
        status.getStyleClass().add("status-label");   // styled in lodge.css

        Label savedLabel = new Label("Saved bills");
        savedLabel.setFont(Font.font("System", FontWeight.BOLD, 15));

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // Header: logo on the left, title + subtitle stacked on the right.
        VBox titleBox = new VBox(4, title, subtitle);
        HBox header = new HBox(14, Branding.createLogo(56), titleBox);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(10, header, top, status, savedLabel, table);
        center.setPadding(new Insets(14));
        return center;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        // Column 0 = labels (natural width), column 1 = controls (take the rest).
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(Region.USE_PREF_SIZE);   // never shorten a label to "Amount paid ..."
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        guestField = new TextField();
        guestField.setPromptText("e.g., Lucas Mulusa");

        // ComboBox<RoomType>: shows each type's toString() ("Deluxe").
        roomBox = new ComboBox<>();
        roomBox.getItems().addAll(RoomType.values());
        roomBox.setPromptText("Choose a room type");
        roomBox.setMaxWidth(Double.MAX_VALUE);

        nightsField = new TextField();
        nightsField.setPromptText("e.g., 3");

        extrasField = new TextField();
        extrasField.setPromptText("amount you wish to spend on other services e.g(on food, massage)");

        paidField = new TextField();
        paidField.setPromptText("0");

        notesArea = new TextArea();
        notesArea.setPromptText("Optional notes");
        notesArea.setWrapText(true);
        notesArea.setPrefRowCount(3);

        addFormRow(grid, 0, "Guest name", guestField);
        addFormRow(grid, 1, "Room type", roomBox);
        addFormRow(grid, 2, "Nights", nightsField);
        addFormRow(grid, 3, "Extras in K", extrasField);
        addFormRow(grid, 4, "Amount paid in K", paidField);
        addFormRow(grid, 5, "Notes", notesArea);

        calculateButton = new Button("Calculate");
        saveButton = new Button("Save bill");
        saveButton.setDefaultButton(true);          // Enter activates Save

        grid.add(new HBox(10, calculateButton, saveButton), 1, 6);
        return grid;
    }

    /** Adds "label | control" as one row and links them for accessibility. */
    private void addFormRow(GridPane grid, int row, String labelText, Control control) {
        Label label = new Label(labelText);
        label.setLabelFor(control);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    /** Right-hand panel: prices come from the RoomType enum, rules from Bill. */
    private VBox buildPricePanel() {
        Label heading = new Label("Practice room prices");
        heading.setFont(Font.font("System", FontWeight.BOLD, 15));

        VBox panel = new VBox(8, heading);
        for (RoomType type : RoomType.values()) {
            panel.getChildren().add(new Label(
                    type.getLabel() + ": " + Money.format(type.getRatePerNight()) + " per night"));
        }

        panel.getChildren().add(ruleLabel(
                Bill.DISCOUNT_MIN_NIGHTS + " nights or more: "
                        + (int) Bill.DISCOUNT_PERCENT + "% off the room charge."));
        panel.getChildren().add(ruleLabel("Extras get no discount. No tax in this lab."));
        panel.getChildren().add(ruleLabel("Saved bills last only until you close the app."));

        panel.setPrefWidth(250);
        panel.setMinWidth(250);
        return panel;
    }

    private Label ruleLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private TableView<Bill> buildTable() {
        TableView<Bill> t = new TableView<>();
        t.setPlaceholder(new Label("No saved bills yet."));

        // Plain getters: PropertyValueFactory finds getBillId(), getGuestName()...
        TableColumn<Bill, String> idCol = new TableColumn<>("Bill");
        idCol.setCellValueFactory(new PropertyValueFactory<>("billId"));
        idCol.setPrefWidth(80);

        TableColumn<Bill, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        guestCol.setPrefWidth(180);

        TableColumn<Bill, RoomType> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomCol.setPrefWidth(110);

        TableColumn<Bill, Integer> nightsCol = new TableColumn<>("Nights");
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("nights"));
        nightsCol.setPrefWidth(80);

        t.getColumns().add(idCol);
        t.getColumns().add(guestCol);
        t.getColumns().add(roomCol);
        t.getColumns().add(nightsCol);
        t.getColumns().add(moneyColumn("Total", Bill::getTotal));
        t.getColumns().add(moneyColumn("Paid", Bill::getAmountPaid));
        t.getColumns().add(moneyColumn("Balance", Bill::getBalance));
        t.getColumns().add(moneyColumn("Change", Bill::getChange));
        return t;
    }

    /** A column that shows a double from the Bill as "K 123.45". */
    private TableColumn<Bill, String> moneyColumn(String title, Function<Bill, Double> getter) {
        TableColumn<Bill, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
                new SimpleStringProperty(Money.format(getter.apply(cell.getValue()))));
        column.setPrefWidth(110);
        return column;
    }

    // =====================================================================
    //  WHAT THE CONTROLLER MAY ASK THE VIEW TO DO
    // =====================================================================

    // ---------- attach actions (the controller decides WHAT happens) ----------

    public void onCalculate(Runnable action) { calculateButton.setOnAction(e -> action.run()); }
    public void onSave(Runnable action)      { saveButton.setOnAction(e -> action.run()); }
    public void onViewReceipt(Runnable a)    { viewReceiptButton.setOnAction(e -> a.run()); }
    public void onDeleteBill(Runnable a)     { deleteButton.setOnAction(e -> a.run()); }
    public void onAbout(Runnable action)     { aboutItem.setOnAction(e -> action.run()); }
    public void onLogout(Runnable action)    { logoutItem.setOnAction(e -> action.run()); }
    public void onClose(Runnable action)     { closeItem.setOnAction(e -> action.run()); }

    /** "New guest" exists twice (menu and toolbar), so both get the same action. */
    public void onNewGuest(Runnable action) {
        newGuestItem.setOnAction(e -> action.run());
        newGuestButton.setOnAction(e -> action.run());
    }

    // ---------- read the form (raw text; the validator judges it) ----------

    public String getGuestName() { return guestField.getText(); }
    public RoomType getRoomType() { return roomBox.getValue(); }   // null = nothing chosen
    public String getNights()    { return nightsField.getText(); }
    public String getExtras()    { return extrasField.getText(); }
    public String getAmountPaid() { return paidField.getText(); }
    public String getNotes()     { return notesArea.getText(); }

    // ---------- change what is shown ----------

    public void setStatus(String text) {
        status.setText(text);
    }

    /** The same ObservableList the service updates, so the table refreshes itself. */
    public void setBills(ObservableList<Bill> bills) {
        table.setItems(bills);
    }

    /** The selected row, or null if nothing is selected. */
    public Bill getSelectedBill() {
        return table.getSelectionModel().getSelectedItem();
    }

    public void clearForm() {
        guestField.clear();
        roomBox.setValue(null);
        nightsField.clear();
        extrasField.clear();
        paidField.clear();
        notesArea.clear();
        status.setText("");
        guestField.requestFocus();
    }

    public void focusFirstField() {
        guestField.requestFocus();
    }

    /** Puts the cursor in the field the validator complained about. */
    public void focusField(BillField field) {
        switch (field) {
            case GUEST -> guestField.requestFocus();
            case ROOM -> roomBox.requestFocus();
            case NIGHTS -> nightsField.requestFocus();
            case EXTRAS -> extrasField.requestFocus();
            case PAID -> paidField.requestFocus();
        }
    }

    // ---------- dialogs (presentation, so they live in the view) ----------

    public void showReceipt(Bill bill) {
        Alert receipt = new Alert(Alert.AlertType.INFORMATION);
        receipt.setTitle("Receipt");
        receipt.setHeaderText("Receipt " + bill.getBillId());
        receipt.setContentText(bill.toReceiptText());
        receipt.showAndWait();
    }

    /** Returns true only if the user pressed Delete. Cancel or closing = false. */
    public boolean confirmDelete(Bill bill) {
        ButtonType delete = new ButtonType("Delete");
        Alert ask = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete bill " + bill.getBillId() + " for " + bill.getGuestName() + "?",
                delete, ButtonType.CANCEL);
        ask.setHeaderText("Confirm deletion");
        return ask.showAndWait().orElse(ButtonType.CANCEL) == delete;
    }

    public void showAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Lodge Billing App");
        about.setContentText("This Lodge Billing app was made and designed by group 8 members.\n"
                + "Saved bills last only until you close the app.");
        about.showAndWait();
    }
}
