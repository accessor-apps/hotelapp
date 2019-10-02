
package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.MainApp;
import accessor.hotel.View;
import accessor.hotel.ViewLoader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private ListView<View> menuList;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private StackPane content;
    @FXML
    private Label currentStateLabel;
    @FXML
    private HBox stateTextContainer;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        MainApp.mainController = this;
        
        minimizeButton.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setIconified(true);
        });
        closeButton.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) root.getScene().getWindow();
            Platform.exit();
            System.exit(0);
        });
        
        
        try {
            menuList.getItems().add(ViewLoader.load("Новый посетитель", "add-guest"));
            menuList.getItems().add(ViewLoader.load("Список посетителей", "guests"));
            menuList.getItems().add(ViewLoader.load("Управление комнатами", "rooms"));
            menuList.getItems().add(ViewLoader.load("Параметры", "parameters"));
            menuList.getItems().add(ViewLoader.load("О приложении", "about"));
            
            menuList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                View view = menuList.getSelectionModel().getSelectedItem();
                if (view != null) {
                    changeView(view);
                }
            });
            menuList.getSelectionModel().select(1);
        } catch (IOException ex) {
            LogJournal.error(ex);
        }
    }
    
    private void changeView(View v) {
        v.getController().update();
        content.getChildren().setAll(v.getView());
    }

    public void handleChangeView(String id) {
        for (View v : menuList.getItems()) {
            if (v.getId().equals(id)) {
                menuList.getSelectionModel().select(v);
            }
        }
    }
    
    public void changeStatusText(String text) {
        stateTextContainer.setVisible(!text.isEmpty());
        currentStateLabel.setText(text);
    }
}
