package com.annimon.tsvc.controllers;

import com.annimon.tsvc.ExceptionHandler;
import com.annimon.tsvc.MainApp;
import com.annimon.tsvc.model.TwitchVideo;
import com.annimon.tsvc.tasks.PastBroadcastsTask;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Initializable {
    
    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    
    @FXML
    private VBox root;
    @FXML
    private JFXTextField tfChannel;
    @FXML
    private JFXButton btnShowBroadcasts;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private TilePane broadcastsPane;
    
    private MainApp application;
    
    public void setApplication(MainApp application) {
        this.application = application;
    }
    
    @FXML
    private void handleShowBroadcasts(ActionEvent event) {
        final String channel = tfChannel.getText();
        final Task<List<TwitchVideo>> broadcastsTask = new PastBroadcastsTask(channel);
        broadcastsTask.setOnSucceeded(e -> onBroadcastsReceived(broadcastsTask.getValue()));
        progressBar.setProgress(-1);
        progressBar.visibleProperty().bind(broadcastsTask.runningProperty());
        progressBar.lookup(".bar").setStyle("-fx-stroke: #4CAF50;");
        btnShowBroadcasts.disableProperty().bind(broadcastsTask.runningProperty());
        new Thread(broadcastsTask).start();
    }
    
    private void onBroadcastsReceived(List<TwitchVideo> videos) {
        broadcastsPane.getChildren().clear();
        for (TwitchVideo video : videos) {
            try {
                final Node node = FXMLLoader.load(getClass().getResource("/fxml/Broadcasts_item.fxml"));
                
                final Label lblTitle = (Label) node.lookup("#lblTitle");
                lblTitle.setText(video.getTitle());
                
                final Label lblGame = (Label) node.lookup("#lblGame");
                lblGame.setText(video.getGame());
                
                final ImageView imgPreview = (ImageView) node.lookup("#imgPreview");
                asyncLoadImage(imgPreview, video.getPreviewUrl());
                
                final Label lblDate = (Label) node.lookup("#lblDate");
                lblDate.setText(DATE_FORMAT.format(video.getDate()));
                
                final Label lblDuration = (Label) node.lookup("#lblDuration");
                lblDuration.setText(video.getDuraton());
                
                final JFXButton btnDownload = (JFXButton) node.lookup("#btnDownload");
                btnDownload.setOnAction((e -> download(video)));
                
                broadcastsPane.getChildren().add(node);
            } catch (IOException ex) {
                ExceptionHandler.log(ex);
            }
        }
        
        videos.stream().forEach(System.out::println);
    }
    
    private void asyncLoadImage(ImageView imageView, String url) {
        final Task<Image> loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(url);
            }
        };
        loadImageTask.setOnSucceeded(e -> imageView.setImage(loadImageTask.getValue()));
        new Thread(loadImageTask).start();
    }
    
    private void download(TwitchVideo video) {
        final Stage downloadStage = new Stage();
        downloadStage.setTitle( String.format("%s. %s", tfChannel.getText(), video.getDescription()) );
        downloadStage.initOwner(application.getPrimaryStage());
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Download.fxml"));
            final Scene scene = new Scene(loader.load());
            final DownloadController controller = (DownloadController) loader.getController();
            controller.setVideo(video);
            downloadStage.setScene(scene);
        } catch (IOException ex) {
            ExceptionHandler.log(ex);
        }
        downloadStage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnShowBroadcasts.disableProperty().bind(Bindings.isEmpty(tfChannel.textProperty()));
        progressBar.prefWidthProperty().bind(root.widthProperty());
    }    
}
