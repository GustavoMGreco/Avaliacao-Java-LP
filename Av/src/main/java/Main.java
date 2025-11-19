import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.StageManager;

import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent listaRoot = FXMLLoader.load(getClass().getResource("/gui/main.fxml"));

        stage.setScene(new Scene(listaRoot));
        stage.setTitle("Mães que oram pelos filhos");
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();
        StageManager.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
