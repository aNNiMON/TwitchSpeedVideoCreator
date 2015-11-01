package com.annimon.tsvc;

import com.annimon.tsvc.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author aNNiMON
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("/styles/Styles.css");
        
        MainController controller = (MainController) loader.getController();
        controller.setApplication(this);
        
        stage.setTitle("Twitch SpeedVideo Creator");
        stage.setScene(scene);
        stage.show();
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
