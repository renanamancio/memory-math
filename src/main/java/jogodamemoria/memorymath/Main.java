package jogodamemoria.memorymath;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jogodamemoria.memorymath.transitions.SceneManager;

import java.io.InputStream;
import java.io.IOException;

/**
 * Classe principal da aplicação Memory Math.
 * Responsável por inicializar a aplicação JavaFX e configurar a janela principal.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class Main extends Application {
    
    /**
     * Método principal que inicializa a aplicação JavaFX.
     * Configura a janela principal, carrega o ícone e inicia a primeira cena.
     * 
     * @param primaryStage O palco principal da aplicação JavaFX
     * @throws IOException Se houver erro ao carregar recursos
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        SceneManager.getInstance().setPrimaryStage(primaryStage);
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");

        Image icone = new Image(Main.class.getResourceAsStream("/images/logoJogo.png"));
        primaryStage.getIcons().add(icone);

        primaryStage.setTitle("Memory Math - Jogo de Memória Matemática");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Método principal que inicia a aplicação.
     * 
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}