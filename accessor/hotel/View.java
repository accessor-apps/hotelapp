package accessor.hotel;

import accessor.hotel.fxml.Controller;
import javafx.scene.Node;

public class View {
    
    private final String title;
    private final String id;
    private final Controller controller;
    private final Node view;

    public View(String id, String title, Controller controller, Node view) {
        this.id = id;
        this.title = title;
        this.controller = controller;
        this.view = view;
    }

    public Controller getController() {
        return controller;
    }

    public Node getView() {
        return view;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }
}
