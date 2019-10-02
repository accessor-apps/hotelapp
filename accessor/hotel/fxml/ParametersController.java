package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.MainApp;
import accessor.hotel.Paths;
import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.model.Journal;
import accessor.hotel.model.Preference;
import accessor.hotel.model.Service;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class ParametersController implements Controller {

    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    
    @FXML
    private TextField roomPriceField;
    @FXML
    private TextField roomCountField;
    @FXML
    private ListView<Service> serviceList;
    @FXML
    private TextField serviceNameFiled;
    @FXML
    private ListView<Journal> journalList;
    @FXML
    private TextArea journalTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void update() {
        try {
            Preference preference = DatabaseHelper.instance.getPreference();
            roomPriceField.setText(decimalFormat.format(preference.roomPrice));
            roomCountField.setText(Integer.toString(preference.roomCount));
            updateServices();
            
            journalList.getItems().setAll(LogJournal.loadJournals());
            journalList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    journalTextArea.setText(newValue.getJournalText());
                }
                else {
                    journalTextArea.setText("(журнал не выбран)");
                }
            });
            
        } catch (SQLException ex) {
            LogJournal.error(ex);
        }
    }
    
    @FXML
    private void acceptAction(ActionEvent event) {
        try {
            Preference preference = new Preference();
            preference.roomPrice = decimalFormat.parse(roomPriceField.getText()).doubleValue();
            preference.roomCount = Integer.parseInt(roomCountField.getText());
            DatabaseHelper.instance.savePreference(preference);
            MainApp.changeStatusText("Параметры обновлены");
        } catch (SQLException | ParseException ex) {
            LogJournal.error(ex);
        }
    }

    @FXML
    private void addServiceAction(ActionEvent event) {
        String serviceName = serviceNameFiled.getText().trim();
        if (serviceName.equals("")) return;
        try {
            Service service = new Service();
            service.name = serviceName;
            DatabaseHelper.instance.addService(service);
            updateServices();
            serviceNameFiled.clear();
            MainApp.changeStatusText("Сервис '" + serviceName + "' успешно добавлен!");
        } catch (SQLException ex) {
            LogJournal.error(ex);
        }
    }

    @FXML
    private void removeServiceAction(ActionEvent event) {
        Service service = serviceList.getSelectionModel().getSelectedItem();
        if (service != null) {
            try {
                DatabaseHelper.instance.removeService(service);
                updateServices();
            MainApp.changeStatusText("Сервис '" + service.name + "' успешно удален!");
            } catch (SQLException ex) {
                LogJournal.error(ex);
            }
        }
    }
    
    private void updateServices() throws SQLException {
        serviceList.getItems().setAll(DatabaseHelper.instance.getServices());
    }

    @FXML
    private void recoveryAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку сохранения");
        File dir = directoryChooser.showDialog(roomPriceField.getScene().getWindow());
        if (dir != null) {
            try {
                File savePath = new File(dir, "HotelRecovery");
                savePath.mkdirs();
                
                // export database
                File dbFile = new File(Paths.DATABASE_PATH);
                Files.copy(dbFile.toPath(), new File(savePath, dbFile.getName()).toPath());
                
                // export logs
                File[] logs = new File(Paths.LOG_PATH).listFiles((File pathname) -> {
                    return pathname.isFile() && (pathname.getName().endsWith(".lst") || pathname.getName().endsWith(".log"));
                });

                for (File log : logs) {
                    Files.copy(log.toPath(), new File(savePath, log.getName()).toPath());
                }
            } catch (IOException ex) {
                LogJournal.error(ex);
            }
        }
    }
}
