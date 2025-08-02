package jogodamemoria.memorymath.transitions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jogodamemoria.memorymath.Main;

import java.io.IOException;
import java.net.URL;

/**
 * Gerenciador de transições de cenas da aplicação Memory Math.
 * Implementa o padrão Singleton para garantir uma única instância.
 * Responsável por carregar e gerenciar as diferentes telas do jogo.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    /**
     * Construtor privado para implementar o padrão Singleton.
     */
    private SceneManager() {}

    /**
     * Obtém a instância única do SceneManager (Singleton).
     * 
     * @return Instância do SceneManager
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Define o palco principal da aplicação.
     * 
     * @param stage Palco principal da aplicação JavaFX
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Carrega uma nova cena a partir de um arquivo FXML.
     * 
     * @param nomeArquivoFxml Caminho do arquivo FXML a ser carregado
     */
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

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent newRoot = loader.load();

            if (nomeArquivoFxml.contains("game-view.fxml")) {
                System.out.println("Carregando cena do jogo - inicializando controller...");
                Object controller = loader.getController();
                if (controller instanceof jogodamemoria.memorymath.controllers.GameController) {
                    jogodamemoria.memorymath.controllers.GameController gameController = 
                        (jogodamemoria.memorymath.controllers.GameController) controller;
                    javafx.application.Platform.runLater(() -> {
                        try {
                            gameController.inicializar();
                        } catch (Exception e) {
                            System.err.println("ERRO ao inicializar GameController: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }
            
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