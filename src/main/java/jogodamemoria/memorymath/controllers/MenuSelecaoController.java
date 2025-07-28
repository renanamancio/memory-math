package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jogodamemoria.memorymath.GameManager;
import jogodamemoria.memorymath.transitions.SceneManager;
import jogodamemoria.memorymath.util.AudioManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuSelecaoController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AudioManager.getInstance().iniciarMusicaFundo();
    }

    @FXML
    private void aoClicarJogar() {
        SceneManager.getInstance().carregarCena("/fxml/selecao-modo-view.fxml");
    }

    @FXML
    private void aoClicarPvP() {
        GameManager.getInstance().setGameMode(GameManager.GameMode.PVP);
        SceneManager.getInstance().carregarCena("/fxml/config-view.fxml");
    }

    @FXML
    private void aoClicarPvE() {
        GameManager.getInstance().setGameMode(GameManager.GameMode.PVE);
        SceneManager.getInstance().carregarCena("/fxml/config-view.fxml");
    }

    @FXML
    private void voltarMenu() {
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }
}