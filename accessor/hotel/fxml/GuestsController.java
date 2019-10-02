package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.MainApp;
import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.data.GuestCount;
import accessor.hotel.model.Guest;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GuestsController implements Controller {

    @FXML
    private TableView<Guest> table;
    @FXML
    private TableColumn<Guest, String> numberColumn;
    @FXML
    private TableColumn<Guest, String> nameColumn;
    @FXML
    private TableColumn<Guest, String> phoneColumn;
    @FXML
    private TableColumn<Guest, Integer> roomColumn;
    @FXML
    private TableColumn<Guest, String> fromColumn;
    @FXML
    private TableColumn<Guest, String> serviceColumn;
    @FXML
    private TableColumn<Guest, LocalDateTime> arrivalColumn;
    @FXML
    private TableColumn<Guest, LocalDateTime> departureColumn;
    @FXML
    private TableColumn<Guest, String> amountColumn;
    @FXML
    private TableColumn<Guest, String> statusColumn;
    @FXML
    private Label totalGuestCount;
    @FXML
    private Label currentGuestCount;
    @FXML
    private Label arriveGuestCount;
    @FXML
    private Label currentPageLabel;

    private int maxItems = 0;
    private int currentPage = 0;
    private int pageCount = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

	table.heightProperty().addListener((observable, oldValue, newValue) -> {
	    int temp_max = (newValue.intValue() / 40);
	    if (temp_max == maxItems) {
		return;
	    }
	    if (temp_max * 40 > newValue.intValue()) {
		temp_max--;
	    }
	    maxItems = temp_max;
            update();
	});
    }    

    @Override
    public void update() {
        try {
            table.getItems().setAll(DatabaseHelper.instance.getGuests(currentPage, maxItems));
            GuestCount guestsCount = DatabaseHelper.instance.getGuestsCount();
            totalGuestCount.setText(Integer.toString(guestsCount.totalCount));
            currentGuestCount.setText(Integer.toString(guestsCount.currentCount));
            arriveGuestCount.setText(Integer.toString(guestsCount.comingCount));
            currentPageLabel.setText((currentPage + 1) + " / " + (pageCount+1));
        } catch (SQLException ex) {
            LogJournal.error(ex);
        }
    }

    @FXML
    private void toFreeAction(ActionEvent event) {
    }

    @FXML
    private void setAsComing(ActionEvent event) {
        updateGuestStatus(0);
    }

    @FXML
    private void setAsHere(ActionEvent event) {
        updateGuestStatus(1);
    }

    @FXML
    private void setAsGone(ActionEvent event) {
        updateGuestStatus(2);
    }
    
    @FXML
    private void editDataAction(ActionEvent event) {
        Guest guest = table.getSelectionModel().getSelectedItem();
        if (guest != null) {
            MainApp.editGuest(guest);
        }
    }

    @FXML
    private void removeGuestAction(ActionEvent event) {
        Guest guest = table.getSelectionModel().getSelectedItem();
        if (guest != null) {
            try {
                DatabaseHelper.instance.removeGuest(guest);
                update();
            } catch (SQLException ex) {
                LogJournal.error(ex);
            }
        }
    }

    private void updateGuestStatus(int status) {
        Guest guest = table.getSelectionModel().getSelectedItem();
        if (guest != null) {
            if (guest.statusId == 2) {
                MainApp.changeStatusText("Посетитель '" + guest.name + "' убыл. Нельзя изменить данные!");
                return;
            }
            try {
                DatabaseHelper.instance.setGuestStatus(guest, status);
                update();
            } catch (SQLException ex) {
                LogJournal.error(ex);
            }
        }
    }
    
    @FXML
    private void prevPageAction(ActionEvent event) {
	currentPage--;
	if (currentPage < 0) {
	    currentPage = 0;
	    return;
	}
        update();
    }

    @FXML
    private void nextPageAction(ActionEvent event) {

        try {
            int count = DatabaseHelper.instance.getGuestTotalCount();
            int pages = count / maxItems;
            if (count - pages * maxItems > 0) {
                pages++;
            }
            pageCount = pages;
            
            currentPage++;
            if (currentPage > pages - 1) {
                currentPage = pages - 1;
                return;
            }
            update();
        } catch (SQLException ex) {
            LogJournal.error(ex);
        }
    }
}