package com.annimon.tsvc.controllers;

import com.annimon.tsvc.MainApp;
import com.annimon.tsvc.Notification;
import com.annimon.tsvc.Util;
import com.annimon.tsvc.model.FFmpegOptions;
import com.annimon.tsvc.model.TwitchVideo;
import com.annimon.tsvc.tasks.FFmpegCheckingTask;
import com.annimon.tsvc.tasks.FFmpegTask;
import com.annimon.tsvc.tasks.PlaylistTask;
import com.annimon.tsvc.tasks.TaskJoiner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;

/**
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
    
    @FXML
    private VBox notificationBox;

    @FXML
    private Label notificationLabel;
    
    private TwitchVideo video;
    private DirectoryChooser directoryChooser;
    private TaskJoiner task;
    
    private MainApp application;
    
    public void setApplication(MainApp application) {
        this.application = application;
    }

    public TwitchVideo getVideo() {
        return video;
    }

    public void setVideo(TwitchVideo video) {
        this.video = video;
        onSpeedSliderChanged();
    }
    
    @FXML
    private void handleSaveTo(ActionEvent event) {
        final File dir = directoryChooser.showDialog(application.getPrimaryStage());
        if (dir != null) {
            directoryChooser.setInitialDirectory(dir);
            btnSaveTo.setText(dir.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleDownload(ActionEvent event) {
        // Cancel task if it runs
        if (task != null && task.isRunning()) {
            task.cancel();
            return;
        }
        
        final String vodId = video.getId();
        final Path playlistPath = Paths.get(vodId + ".m3u8");
        task = new TaskJoiner(
                new PlaylistTask(vodId, playlistPath),
                new FFmpegTask(buildFFmpegOptions(playlistPath))
                        .setResultLength(getResultDuration())
        );
        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.progressProperty().unbind();
        progressBar.setProgress(-1);
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.lookup(".bar").setStyle("-fx-stroke: #6441A5;");
        task.messageProperty().addListener(e -> {
            taStatus.setText(taStatus.getText() + "\n" + task.getMessage());
            taStatus.setScrollTop(Double.MAX_VALUE);
        });
        task.runningProperty().addListener(e -> btnDownload.setText(task.isRunning() ? "Cancel" : "Download"));
        task.setOnSucceeded(e -> Notification.success("Operation successfully complete!"));
        new Thread(task).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Notification.init(notificationBox, notificationLabel);
        
        final String userDir = System.getProperty("user.dir");
        btnSaveTo.setText(userDir);
        checkFFmpegExists(userDir);
        
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(userDir));
        directoryChooser.setTitle("Select folder to save");
        
        cbFormats.getItems().addAll(new Label("mp4"), new Label("avi"), new Label("ts"));
        cbFormats.setValue(cbFormats.getItems().get(0));
        
        progressBar.setProgress(-1);
        progressBar.prefWidthProperty().bind(root.widthProperty());
        
        slSpeed.valueProperty().addListener(e -> onSpeedSliderChanged());
    }

    private void checkFFmpegExists(final String userDir) {
        final FFmpegCheckingTask checkFFmpegExists = new FFmpegCheckingTask();
        checkFFmpegExists.setOnSucceeded(event -> {
            if (checkFFmpegExists.getValue()) return;
            
            taStatus.setWrapText(true);
            taStatus.setText("For download and process video files you need ffmpeg"
                    + " and place executable in your PATH or in " + userDir + " folder. "
                    + "Then reopen this window.");
            btnDownload.setText("Download ffmpeg");
            btnDownload.setOnAction(e -> application.getHostServices()
                    .showDocument("https://www.ffmpeg.org/download.html"));
        });
        new Thread(checkFFmpegExists).start();
    }
    
    public void onCloseRequest(WindowEvent event) {
        if (task != null && task.isRunning()) {
            Notification.error("Task is running. Cancel it or wait until it done to close this window.");
            event.consume();
        }
    }
    
    private void onSpeedSliderChanged() {
        final double value = getSliderSpeedValue();
        lblResultInfo.setText(calculateResultLength(value));
        // Audio enabled only for value = 1
        cbAudio.setDisable(value > 1);
        if (value > 1) cbAudio.setSelected(false);
    }

    private String calculateResultLength(double speedFactor) {
        if (video == null) return String.format("%sx", new DecimalFormat("#.##").format(speedFactor));
        
        return String.format("%sx %s (pts = %s)",
                new DecimalFormat("#.##").format(speedFactor),
                Util.duration(getResultDuration()),
                new DecimalFormat("#.#####", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
                        .format(getSpeedFactor()));
    }
    
    private FFmpegOptions buildFFmpegOptions(final Path playlistPath) {
        final FFmpegOptions options = new FFmpegOptions();
        options.setInput(playlistPath.toString());
        options.setSpeedFactor(getSpeedFactor());
        options.setIsAudio(cbAudio.isSelected());
        options.setOutput(getFormat());
        return options;
    }
    
    private double getSliderSpeedValue() {
        final double exp = Math.exp(slSpeed.getValue() / 2);
        if (exp < 2) return exp;
        final int[] steps = {5, 10, 20, 50, 100};
        for (int step : steps) {
            if (exp < step) return round(exp, step / 100f);
        }
        return round(exp, 2);
    }
    
    public static double round(double input, double step) {
        return Math.round(input / step) * step;
    }
    
    private int getResultDuration() {
        return (int)(video.getLength() / getSliderSpeedValue());
    }
    
    private double getSpeedFactor() {
        return 1d / getSliderSpeedValue();
    }
    
    private String getFormat() {
        final String path = btnSaveTo.getText();
        final String filename = video.getId();
        final String ext = cbFormats.getValue().getText();
        return String.format("%s/%s.%s", path, filename, ext);
    }
}
