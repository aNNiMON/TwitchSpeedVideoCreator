package com.annimon.tsvc.controllers;

import com.annimon.tsvc.model.TwitchVideo;
import com.annimon.tsvc.tasks.PlaylistTask;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aNNiMON
 */
public class DownloadController implements Initializable {
    
    @FXML
    private VBox root;
    
    @FXML
    private Label lblResultInfo;
    
    @FXML
    private JFXSlider slSpeed;
    
    @FXML
    private JFXButton btnSaveTo;
    
    @FXML
    private JFXComboBox<Label> cbFormats;
    
    @FXML
    private JFXCheckBox cbAudio;
    
    @FXML
    private JFXButton btnDownload;
    
    @FXML
    private TextArea taStatus;
    
    @FXML
    private JFXProgressBar progressBar;
    
    private TwitchVideo video;

    public TwitchVideo getVideo() {
        return video;
    }

    public void setVideo(TwitchVideo video) {
        this.video = video;
    }
    
    @FXML
    private void handleDownload(ActionEvent event) {
        final String vodId = Integer.toString(video.getId());
        final Path playlistPath = Paths.get(vodId + ".m3u8");

        final PlaylistTask task = new PlaylistTask(vodId, playlistPath);
        progressBar.setProgress(-1);
        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.lookup(".bar").setStyle("-fx-stroke: #FFEB3B;");
        new Thread(task).start();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFormats.getItems().addAll(new Label("mp4 (best)"), new Label("avi"), new Label("ts"));
    } 
}
