/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accessor.hotel.fxml;

import accessor.hotel.LogJournal;
import accessor.hotel.Util;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AboutController implements Controller {


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void update() {
    }

    @FXML
    private void gotoRepo(ActionEvent event) {
        try {
            URI uri = new URL("https://github.com/accessor-apps/hotelapp").toURI();
            Util.openWebpage(uri);
        } catch (URISyntaxException | MalformedURLException ex) {
            LogJournal.error(ex);
        }
}
    
}
