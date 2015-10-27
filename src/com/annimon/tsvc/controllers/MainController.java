package com.annimon.tsvc.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainController implements Initializable {
    
    @FXML
    private JFXTextField tfChannel;
    @FXML
    private JFXButton btnShowBroadcasts;
    
    @FXML
    private void handleShowBroadcasts(ActionEvent event) {
        final String channel = tfChannel.getText();
        
        System.out.println(channel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnShowBroadcasts.disableProperty().bind(Bindings.isEmpty(tfChannel.textProperty()));
        
    }    
}
