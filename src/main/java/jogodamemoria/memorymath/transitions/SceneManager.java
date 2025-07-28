package jogodamemoria.memorymath.transitions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jogodamemoria.memorymath.Main;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void carregarCena(String nomeArquivoFxml) {
        if (primaryStage == null) {
            System.err.println("Erro: Stage principal não definido.");
            return;
        }
        try {
            URL resourceUrl = Main.class.getResource(nomeArquivoFxml);
            System.out.println("Tentando carregar o recurso: " + nomeArquivoFxml);
            System.out.println("URL encontrada: " + resourceUrl);

            if (resourceUrl == null) {
                throw new IOException("Não foi possível encontrar o recurso FXML: " + nomeArquivoFxml);
            }

            Parent newRoot = FXMLLoader.load(resourceUrl);
            Scene currentScene = primaryStage.getScene();

            if (currentScene == null) {
                primaryStage.setScene(new Scene(newRoot));
            } else {
                currentScene.setRoot(newRoot);
            }

        } catch (IOException e) {
            System.err.println("Falha ao carregar o FXML: " + nomeArquivoFxml);
            e.printStackTrace();
        }
    }
}