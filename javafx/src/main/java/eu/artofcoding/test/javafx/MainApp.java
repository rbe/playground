package eu.artofcoding.test.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public void start(Stage stage) throws Exception {
        //
        String fxmlFile = "/fxml/hello.fxml";
//        String fxmlFile = "/fxml/Ventplan.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/."));
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        HelloController helloController = loader.getController();
        helloController.setStage(stage);
        //
        Scene scene = new Scene(rootNode, 400, 200);
        scene.getStylesheets().add("/styles/styles.css");
        stage.setTitle("Hello JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

}
