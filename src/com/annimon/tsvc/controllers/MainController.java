package com.annimon.tsvc.controllers;

import com.annimon.tsvc.model.TwitchVideo;
import com.annimon.tsvc.tasks.PastBroadcastsTask;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
    
    @FXML
    private VBox root;
    @FXML
    private JFXTextField tfChannel;
    @FXML
    private JFXButton btnShowBroadcasts;
    @FXML
    private JFXProgressBar progressBar;
    
    @FXML
    private void handleShowBroadcasts(ActionEvent event) {
        final String channel = tfChannel.getText();
        final Task<List<TwitchVideo>> broadcastsTask = new PastBroadcastsTask(channel);
        broadcastsTask.setOnSucceeded(e -> onBroadcastsReceived(broadcastsTask.getValue()));
        progressBar.setProgress(-1);
        progressBar.visibleProperty().bind(broadcastsTask.runningProperty());
        btnShowBroadcasts.disableProperty().bind(broadcastsTask.runningProperty());
        new Thread(broadcastsTask).start();
    }
    
    private void onBroadcastsReceived(List<TwitchVideo> videos) {
        videos.stream().forEach(System.out::println);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnShowBroadcasts.disableProperty().bind(Bindings.isEmpty(tfChannel.textProperty()));
        progressBar.prefWidthProperty().bind(root.widthProperty());
    }    
}
