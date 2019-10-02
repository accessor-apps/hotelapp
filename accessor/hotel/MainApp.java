package accessor.hotel;

import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.fxml.AddGuestController;
import accessor.hotel.fxml.ErrorController;
import accessor.hotel.fxml.MainController;
import accessor.hotel.model.Guest;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    
    public static MainController mainController;
    public static AddGuestController addGuestController;
    
    private static ErrorController errorController;
    private static Stage errorStage;
    private static Stage primaryStage;
    
    public static void main(String[] args) throws SQLException {
        Paths.enterDatabase();
        DatabaseHelper.instance.getGuests(0, 5000);
        DatabaseHelper.instance.getRooms(false, -1);
        LogJournal.open();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainApp.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image("/accessor/hotel/res/icon.png"));
        primaryStage.setTitle("Accessor Hotel Manager");
        loadErrorStage();
        errorStage.initOwner(primaryStage);
        errorStage.initModality(Modality.APPLICATION_MODAL);
        Parent p = FXMLLoader.load(MainController.class.getResource("/accessor/hotel/fxml/main.fxml"));
        p.getStylesheets().add("/accessor/hotel/res/theme.css");
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(new Scene(p, screen.getWidth(), screen.getHeight(), Color.BLACK));
        
        
        Stage splashScreen = new Stage(StageStyle.TRANSPARENT);
        splashScreen.setAlwaysOnTop(true);
        splashScreen.getIcons().add(new Image("/accessor/hotel/res/icon.png"));
        splashScreen.setTitle("Accessor Hotel 2019");
        Parent sp = FXMLLoader.load(MainController.class.getResource("/accessor/hotel/fxml/splash.fxml"));
        p.getStylesheets().add("/accessor/hotel/res/theme.css");
        splashScreen.setScene(new Scene(sp, 600, 400));
        splashScreen.show();
    }
    
    public static void startApp() {
        primaryStage.show();
        changeStatusText("Приложение успешно запушено...");
        addGuestController.setGuest(null);
    }
    
    private void loadErrorStage() throws Exception {
        errorStage = new Stage();
        errorStage.initStyle(StageStyle.TRANSPARENT);
        errorStage.getIcons().add(new Image("/accessor/hotel/res/sarbonicon.png"));
        errorStage.setTitle("Accessor Hotel");
        View view = ViewLoader.load("Системная ошибка", "error");
        ((Parent)view.getView()).getStylesheets().add("/accessor/hotel/res/theme.css");

        errorController = (ErrorController) view.getController();
        errorStage.setScene(new Scene((Parent) view.getView(), Color.TRANSPARENT));
    }
    
    public static void changeStatusText(String text) {
        LogJournal.note(text);
        mainController.changeStatusText(text);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> { mainController.changeStatusText(""); });
                timer.cancel();
            }
        }, 5000);
    }
    
    public static void showError(String journalName) {
        errorController.setJournalName(journalName);
        errorStage.show();
    }
    
    public static void editGuest(Guest guest) {
        addGuestController.setGuest(guest);
        showAddGuest();
    }

    public static void showAddGuest() {
        mainController.handleChangeView("add-guest");
    }
}