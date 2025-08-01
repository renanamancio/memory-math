package jogodamemoria.memorymath;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jogodamemoria.memorymath.transitions.SceneManager;

import java.io.InputStream;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        SceneManager.getInstance().setPrimaryStage(primaryStage);
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
//        Image icone = new Image(Main.class.getResourceAsStream("/image/logoJogo.png"));
//        primaryStage.getIcons().add(icone);
        primaryStage.setTitle("Memory Math - Jogo de Memória Matemática");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}