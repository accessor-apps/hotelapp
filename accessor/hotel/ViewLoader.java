package accessor.hotel;

import accessor.hotel.fxml.Controller;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ViewLoader {

    private static final String location = "/accessor/hotel/fxml/";
    private static final String stylesheet = "/accessor/hotel/res/theme.css";
    
    public static View load(String title, String url) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ViewLoader.class.getResource(location + url + ".fxml"));
        Node view = loader.load();
        Controller controller = loader.getController();
        return new View(url, title, controller, view);
    }
}