package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.MainApp;
import accessor.hotel.Util;
import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.model.Guest;
import accessor.hotel.model.Room;
import accessor.hotel.model.Service;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddGuestController implements Controller {

    @FXML
    private Label currentPrice;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField countryField;
    @FXML
    private ComboBox<Service> serviceList;
    @FXML
    private ComboBox<Room> roomList;
    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private TextArea extraNote;
    @FXML
    private Label title;
    @FXML
    private Button saveButton;
    @FXML
    private CheckBox arriveCheckBox;
   
    private Guest guest;
    
    private final SimpleDoubleProperty currentPriceProperty = new SimpleDoubleProperty();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private boolean dontSave = false;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        MainApp.addGuestController = this;
        
        currentPriceProperty.addListener((observable, oldValue, newValue) -> {
            calculatePrice();
        });
        
        arrivalDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calculatePrice();
            }
        });
        departureDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calculatePrice();
            }
        });
    }

    @Override
    public void update() {
        try {
            double currentPriceValue = DatabaseHelper.instance.getPreference().roomPrice;
            currentPriceProperty.setValue(currentPriceValue);
            if (guest == null) {
                clearFields();
            } else {
                loadFields();
            }
        }
        catch(SQLException e) {
            LogJournal.error(e);
        }
    }

    @FXML
    private void saveAction(ActionEvent event) {
        if (dontSave || nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty() || countryField.getText().trim().isEmpty()) {
            MainApp.changeStatusText("Заполните все поля!");
            return;
        }
        
        try {
            Guest newGuest = new Guest();
            newGuest.id = -1;
            newGuest.name = nameField.getText().trim();
            newGuest.phone = phoneField.getText().trim();
            newGuest.from = countryField.getText();
            newGuest.roomId = roomList.getSelectionModel().getSelectedItem().id;
            newGuest.serviceId = serviceList.getSelectionModel().getSelectedItem().id;
            newGuest.price = currentPriceProperty.doubleValue();
            newGuest.arrivalDate = LocalDateTime.of(arrivalDatePicker.getValue(), LocalTime.now());
            newGuest.departureDate = LocalDateTime.of(departureDatePicker.getValue(), LocalTime.now());
            newGuest.note = extraNote.getText();
            newGuest.statusId = arriveCheckBox.isSelected() ? 1 : 0;
            if (guest != null) {
                newGuest.id = guest.id;
                if (guest.statusId == 2) {
                    newGuest.statusId = 2;
                }
                newGuest.arrivalDate = guest.arrivalDate;
                newGuest.departureDate = guest.departureDate;
                DatabaseHelper.instance.updateGuest(guest, newGuest);
            } else {
                DatabaseHelper.instance.addGuest(newGuest);
            }
            MainApp.mainController.handleChangeView("guests");
            MainApp.changeStatusText("Данные посетителя '" + newGuest.name + " успешно " + (guest == null ? "добавлены" : "обновлены"));
            guest = null;
        }
        catch(SQLException e) {
            LogJournal.error(e);
        }
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        MainApp.mainController.handleChangeView("guests");
        MainApp.changeStatusText("Редактирование отменено");
        guest = null;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    private void loadCombos() throws SQLException {
        int additionRoomId = guest != null ? guest.roomId : -1;
        serviceList.getItems().setAll(DatabaseHelper.instance.getServices());
        serviceList.getSelectionModel().select(0);
        roomList.getItems().setAll(DatabaseHelper.instance.getRooms(true, additionRoomId));
        roomList.getSelectionModel().select(0);
    }
    
    private void loadFields() throws SQLException {
        loadCombos();
        currentPriceProperty.setValue(guest.price);
        nameField.setText(guest.name);
        phoneField.setText(guest.phone);
        countryField.setText(guest.from);
        arrivalDatePicker.setValue(LocalDate.now());
        departureDatePicker.setValue(LocalDate.now());
        serviceList.getSelectionModel().select(0);
        roomList.getSelectionModel().select(0);
        arriveCheckBox.setSelected(guest.statusId != 0);
        extraNote.setText(guest.note);
        title.setText("Редактирование данных");
        
        for (Service service : serviceList.getItems()) {
            if (service.id == guest.serviceId) {
                serviceList.getSelectionModel().select(service);
            }
        }
        for (Room room : roomList.getItems()) {
            if (room.id == guest.roomId) {
                roomList.getSelectionModel().select(room);
            }
        }
    }

    private void clearFields() throws SQLException {
        loadCombos();
        nameField.setText("");
        phoneField.setText("");
        countryField.setText("");
        arrivalDatePicker.setValue(LocalDate.now());
        departureDatePicker.setValue(LocalDate.now());
        serviceList.getSelectionModel().select(0);
        roomList.getSelectionModel().select(0);
        extraNote.setText("");
        title.setText("Новый посетитель");
    }

    private void calculatePrice() {
        LocalDate arrivalDate = arrivalDatePicker.getValue();
        LocalDate departureDate = departureDatePicker.getValue();
        Double price = currentPriceProperty.getValue();
        
        if ((arrivalDate == null) || (departureDate == null)) {
            return;
        }
        
        if ((arrivalDate.isAfter(departureDate))) {
            MainApp.changeStatusText("Неправильная дата убытия (Дата прибытия < Дата убытия)");
            dontSave = true;
            return;
        }
        else {
            dontSave = false;
        }
        
        LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, LocalTime.now());
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, LocalTime.now());
        int days = Util.differenceOfDays(arrivalDateTime, departureDateTime);
        if (days == 0) days = 1;
        currentPrice.setText(decimalFormat.format(days * price) + " сум");
    }
}
