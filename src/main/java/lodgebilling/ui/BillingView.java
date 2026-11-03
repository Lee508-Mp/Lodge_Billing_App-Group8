package lodgebilling.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import lodgebilling.model.Bill;
import lodgebilling.model.BillField;
import lodgebilling.model.RoomType;
import lodgebilling.util.Money;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class BillingView {

    private final String currentUser;

    private TextField guestField;
    private ComboBox<RoomType> roomBox;
    private TextField nightsField;
    private TextField extrasField;
    private TextField paidField;
    private TextArea notesArea;
    private Label status;
    private TableView<Bill> table;

    private ImageView roomImage;
    private Label roomPlaceholder;
    private Label roomTitle;
    private Label roomRate;
    private VBox roomFeatures;

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

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setTop(new VBox(buildMenuBar(), buildToolBar()));
        root.setCenter(buildCenter());
        return root;
    }

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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label loggedInAs = new Label("Logged in as: " + currentUser);

        return new ToolBar(
                newGuestButton,
                viewReceiptButton,
                deleteButton,
                spacer,
                loggedInAs
        );
    }

    private VBox buildCenter() {
        Label title = new Label("Parkview Lodge");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.getStyleClass().add("title-label");

        HBox subtitle = new HBox(
                14,
                new Label("Group Assignment"),
                new Label("Group 8"),
                new Label("Mulungushi University")
        );

        GridPane form = buildForm();
        VBox prices = buildPricePanel();
        VBox roomCard = buildRoomCard();

        HBox top = new HBox(24, form, prices, roomCard);
        HBox.setHgrow(form, Priority.ALWAYS);

        status = new Label();
        status.setWrapText(true);
        status.setFont(Font.font("System", FontWeight.BOLD, 13));
        status.getStyleClass().add("status-label");

        Label savedLabel = new Label("Saved bills");
        savedLabel.setFont(Font.font("System", FontWeight.BOLD, 15));

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox titleBox = new VBox(4, title, subtitle);

        HBox header = new HBox(14, Branding.createLogo(56), titleBox);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(
                10,
                header,
                top,
                status,
                savedLabel,
                table
        );

        center.setPadding(new Insets(14));
        return center;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(Region.USE_PREF_SIZE);

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        guestField = new TextField();
        guestField.setPromptText("e.g., Lucas Mulusa");

        roomBox = new ComboBox<>();
        roomBox.getItems().addAll(RoomType.values());
        roomBox.setPromptText("Choose a room type");
        roomBox.setMaxWidth(Double.MAX_VALUE);

        roomBox.valueProperty().addListener(
                (obs, oldType, newType) -> showRoomPreview(newType)
        );

        nightsField = new TextField();
        nightsField.setPromptText("e.g., 3");

        extrasField = new TextField();
        extrasField.setPromptText(
                "amount you wish to spend on other services e.g(200)"
        );

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
        saveButton.setDefaultButton(true);

        grid.add(new HBox(10, calculateButton, saveButton), 1, 6);

        return grid;
    }

    private void addFormRow(
            GridPane grid,
            int row,
            String labelText,
            Control control
    ) {
        Label label = new Label(labelText);
        label.setLabelFor(control);

        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private VBox buildPricePanel() {
        Label heading = new Label("Practice room prices");
        heading.setFont(Font.font("System", FontWeight.BOLD, 15));

        VBox panel = new VBox(8, heading);

        for (RoomType type : RoomType.values()) {
            panel.getChildren().add(
                    new Label(
                            type.getLabel() + ": "
                                    + Money.format(type.getRatePerNight())
                                    + " per night"
                    )
            );
        }

        panel.getChildren().add(
                ruleLabel(
                        Bill.DISCOUNT_MIN_NIGHTS + " nights or more: "
                                + (int) Bill.DISCOUNT_PERCENT
                                + "% off the room charge."
                )
        );

        panel.getChildren().add(
                ruleLabel("Extras get no discount. No tax in this lab.")
        );

        panel.getChildren().add(
                ruleLabel("Saved bills last only until you close the app.")
        );

        panel.setPrefWidth(250);
        panel.setMinWidth(250);

        return panel;
    }

    private VBox buildRoomCard() {
        roomImage = new ImageView();
        roomImage.setFitWidth(236);
        roomImage.setFitHeight(133);
        roomImage.setPreserveRatio(true);
        roomImage.setSmooth(true);

        roomPlaceholder = new Label();
        roomPlaceholder.setWrapText(true);
        roomPlaceholder.setMaxWidth(200);
        roomPlaceholder.getStyleClass().add("room-placeholder");

        StackPane imageBox = new StackPane(roomImage, roomPlaceholder);
        imageBox.setMinSize(236, 133);
        imageBox.setPrefSize(236, 133);
        imageBox.setMaxSize(236, 133);
        imageBox.getStyleClass().add("room-image-box");

        roomTitle = new Label();
        roomTitle.getStyleClass().add("room-title");

        roomRate = new Label();
        roomRate.getStyleClass().add("room-rate");

        roomFeatures = new VBox(3);
        roomFeatures.setMinHeight(110);

        VBox card = new VBox(
                8,
                imageBox,
                roomTitle,
                roomRate,
                roomFeatures
        );

        card.setPadding(new Insets(12));
        card.setMinWidth(260);
        card.setPrefWidth(260);
        card.setMaxWidth(260);
        card.getStyleClass().add("room-card");

        showRoomPreview(null);
        return card;
    }

    private void showRoomPreview(RoomType type) {
        roomFeatures.getChildren().clear();

        if (type == null) {
            roomImage.setImage(null);
            roomPlaceholder.setText(
                    "Choose a room type to see its photo and qualities."
            );
            roomTitle.setText("Room preview");
            roomRate.setText("");
            return;
        }

        Image photo = Pictures.room(type);
        roomImage.setImage(photo);

        roomPlaceholder.setText(
                photo == null
                        ? "No photo found for " + type.getLabel()
                        : ""
        );

        roomTitle.setText(type.getLabel() + " room");
        roomRate.setText(
                Money.format(type.getRatePerNight()) + " per night"
        );

        for (String feature : type.getFeatures()) {
            Label line = new Label("\u2022 " + feature);
            line.setWrapText(true);
            line.getStyleClass().add("room-feature");
            roomFeatures.getChildren().add(line);
        }
    }

    private Label ruleLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private TableView<Bill> buildTable() {
        TableView<Bill> t = new TableView<>();
        t.setPlaceholder(new Label("No saved bills yet."));

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

    private TableColumn<Bill, String> moneyColumn(
            String title,
            Function<Bill, Double> getter
    ) {
        TableColumn<Bill, String> column = new TableColumn<>(title);

        column.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        Money.format(getter.apply(cell.getValue()))
                )
        );

        column.setPrefWidth(110);
        return column;
    }

    public void onCalculate(Runnable action) {
        calculateButton.setOnAction(e -> action.run());
    }

    public void onSave(Runnable action) {
        saveButton.setOnAction(e -> action.run());
    }

    public void onViewReceipt(Runnable action) {
        viewReceiptButton.setOnAction(e -> action.run());
    }

    public void onDeleteBill(Runnable action) {
        deleteButton.setOnAction(e -> action.run());
    }

    public void onAbout(Runnable action) {
        aboutItem.setOnAction(e -> action.run());
    }

    public void onLogout(Runnable action) {
        logoutItem.setOnAction(e -> action.run());
    }

    public void onClose(Runnable action) {
        closeItem.setOnAction(e -> action.run());
    }

    public void onNewGuest(Runnable action) {
        newGuestItem.setOnAction(e -> action.run());
        newGuestButton.setOnAction(e -> action.run());
    }

    public String getGuestName() {
        return guestField.getText();
    }

    public RoomType getRoomType() {
        return roomBox.getValue();
    }

    public String getNights() {
        return nightsField.getText();
    }

    public String getExtras() {
        return extrasField.getText();
    }

    public String getAmountPaid() {
        return paidField.getText();
    }

    public String getNotes() {
        return notesArea.getText();
    }

    public void setStatus(String text) {
        status.setText(text);
    }

    public void setBills(ObservableList<Bill> bills) {
        table.setItems(bills);
    }

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

    public void focusField(BillField field) {
        switch (field) {
            case GUEST -> guestField.requestFocus();
            case ROOM -> roomBox.requestFocus();
            case NIGHTS -> nightsField.requestFocus();
            case EXTRAS -> extrasField.requestFocus();
            case PAID -> paidField.requestFocus();
        }
    }

    public void showReceipt(Bill bill) {

        if (bill == null) {
            return;
        }

        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle(
                "Parkview Lodge | Receipt " + bill.getBillId()
        );

        String date = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy  |  HH:mm")
        );

        String paymentStatus;

        if (bill.getBalance() > 0 && bill.getAmountPaid() > 0) {
            paymentStatus = "PARTIALLY PAID";
        } else if (bill.getBalance() > 0) {
            paymentStatus = "UNPAID";
        } else {
            paymentStatus = "FULLY PAID";
        }

        String servedBy = bill.getServedBy() == null
                || bill.getServedBy().isBlank()
                ? "Not specified"
                : bill.getServedBy();

        String notes = bill.getNotes() == null
                || bill.getNotes().isBlank()
                ? "None"
                : bill.getNotes();

        String receiptText = """
                
                    ==============================================
                              PARKVIEW LODGE
                         COMFORT  •  STAY  •  RELAX
                    ==============================================
                
                              GUEST RECEIPT
                
                    Receipt Number : %s
                    Date           : %s
                
                    ----------------------------------------------
                              GUEST INFORMATION
                    ----------------------------------------------
                
                    Guest Name     : %s
                    Room Type      : %s
                    Nights Stayed  : %d
                    Served By      : %s
                
                    ----------------------------------------------
                              CHARGES SUMMARY
                    ----------------------------------------------
                
                    Room Charge    : %s
                    Discount       : -%s
                    Extras         : %s
                
                    ----------------------------------------------
                    TOTAL          : %s
                    ----------------------------------------------
                
                    Amount Paid    : %s
                    Balance Due    : %s
                    Change         : %s
                
                    Payment Status : %s
                
                    ----------------------------------------------
                              ADDITIONAL INFORMATION
                    ----------------------------------------------
                
                    Notes:
                    %s
                
                    ==============================================
                
                      THANK YOU FOR CHOOSING PARKVIEW LODGE!
                
                          WE HOPE TO SEE YOU AGAIN.
                
                    ==============================================
                """.formatted(
                bill.getBillId(),
                date,
                bill.getGuestName(),
                bill.getRoomType().getLabel(),
                bill.getNights(),
                servedBy,
                Money.format(bill.getRoomCharge()),
                Money.format(bill.getDiscount()),
                Money.format(bill.getExtras()),
                Money.format(bill.getTotal()),
                Money.format(bill.getAmountPaid()),
                Money.format(bill.getBalance()),
                Money.format(bill.getChange()),
                paymentStatus,
                notes
        );

        TextArea receiptArea = new TextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setWrapText(false);
        receiptArea.setFont(Font.font("Monospaced", 11));
        receiptArea.setMaxWidth(Double.MAX_VALUE);
        receiptArea.setMaxHeight(Double.MAX_VALUE);

        receiptArea.setStyle(
                "-fx-control-inner-background: white;"
                + "-fx-text-fill: #12345A;"
                + "-fx-border-color: transparent;"
                + "-fx-background-color: white;"
                + "-fx-background-radius: 0;"
        );

        VBox receiptContent = new VBox(receiptArea);
        receiptContent.setPadding(Insets.EMPTY);
        receiptContent.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        VBox.setVgrow(receiptArea, Priority.ALWAYS);

        javafx.scene.control.ScrollPane scrollPane =
                new javafx.scene.control.ScrollPane(receiptContent);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        scrollPane.setVbarPolicy(
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background: white;"
                + "-fx-background-color: white;"
                + "-fx-border-color: transparent;"
        );

        Button doneButton = new Button("Done");
        doneButton.setDefaultButton(true);
        doneButton.setCancelButton(true);

        doneButton.setStyle(
                "-fx-background-color: #087E8B;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12px;"
                + "-fx-background-radius: 6px;"
                + "-fx-padding: 8px 25px;"
                + "-fx-cursor: hand;"
        );

        doneButton.setOnAction(event -> receiptStage.close());

        HBox buttonBox = new HBox(doneButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox page = new VBox(
                scrollPane,
                buttonBox
        );

        page.setSpacing(0);
        page.setPadding(new Insets(0));
        page.setStyle("-fx-background-color: white;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // REDUCED RECEIPT WIDTH
        Scene scene = new Scene(page, 460, 650);

        receiptStage.setScene(scene);
        receiptStage.setMinWidth(420);
        receiptStage.setMinHeight(450);
        receiptStage.setResizable(true);

        receiptStage.showAndWait();
    }

    public boolean confirmDelete(Bill bill) {
        ButtonType delete = new ButtonType("Delete");

        Alert ask = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete bill " + bill.getBillId()
                        + " for " + bill.getGuestName() + "?",
                delete,
                ButtonType.CANCEL
        );

        ask.setHeaderText("Confirm deletion");
        Theme.apply(ask);

        return ask.showAndWait().orElse(ButtonType.CANCEL) == delete;
    }

    public void showAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);

        about.setTitle("About");
        about.setHeaderText("Parkview Lodge");

        about.setContentText(
                "This Lodge Billing app was made and designed "
                        + "by group 8 members.\n"
                        + "Saved bills last only until you close the app."
        );

        Theme.apply(about);
        about.showAndWait();
    }
}