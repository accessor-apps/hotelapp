package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.MainApp;
import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.model.Room;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoomsController implements Controller {

    @FXML
    private TableView<Room> table;
    @FXML
    private TableColumn<Room, Integer> numberColumn;
    @FXML
    private TableColumn<Room, String> guestColumn;
    @FXML
    private TableColumn<Room, String> fromColumn;
    @FXML
    private TableColumn<Room, String> arrivalColumn;
    @FXML
    private TableColumn<Room, String> departureColumn;
    @FXML
    private TableColumn<Room, String> amountColumn;
    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private Button openCloseButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
	guestColumn.setCellValueFactory(new PropertyValueFactory<>("guest"));
	fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
	arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
	departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
	amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
	statusColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                openCloseButton.setDisable(true);
                return;
            }
            if (newValue.isClosed == 1) {
                openCloseButton.setText("Открыть комнату");
            }
            else {
                openCloseButton.setText("Закрыть комнату");
            }
            openCloseButton.setDisable(newValue.guestId != -1);
        });
    }    

    @Override
    public void update() {
        try {
            table.getItems().setAll(DatabaseHelper.instance.getRooms(false, -1));
        } catch (SQLException ex) {
            LogJournal.error(ex);
        }
    }

    @FXML
    private void toFreeAction(ActionEvent event) {
        Room room = table.getSelectionModel().getSelectedItem();
        if (room != null) {
            try {
                DatabaseHelper.instance.freeRoom(room.id);
                update();
            } catch (SQLException ex) {
                LogJournal.error(ex);
            }
        }
    }

    @FXML
    private void openCloseRoomAction(ActionEvent event) {
        Room room = table.getSelectionModel().getSelectedItem();
        if (room != null && room.guestId == -1) {
            try {
                DatabaseHelper.instance.openCloseRoom(room.id, room.isClosed != 1);
                update();
                MainApp.changeStatusText("Комната #" + room.number + (room.isClosed == 1 ? " открыта" : " закрыта"));
            } catch (SQLException ex) {
                LogJournal.error(ex);
            }
        }
    }
}
