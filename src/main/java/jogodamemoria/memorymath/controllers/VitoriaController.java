package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import jogodamemoria.memorymath.GameManager;
import jogodamemoria.memorymath.Player;
import jogodamemoria.memorymath.transitions.SceneManager;
import jogodamemoria.memorymath.AIPlayer;
import jogodamemoria.memorymath.util.AudioManager;

public class VitoriaController {

    @FXML private Label vencedorLabel;
    @FXML private Label placarFinalLabel;

    @FXML
    public void initialize() {
        // O som já foi tocado no GameController baseado no resultado
        
        GameManager gm = GameManager.getInstance();
        Player vencedor = gm.getVencedor();
        GameManager.GameMode modo = gm.getGameMode();
        String mensagem = "";
        if (vencedor == null) {
            mensagem = "Empate!";
        } else if (modo == GameManager.GameMode.PVP) {
            mensagem = vencedor.getName() + " venceu!";
        } else if (modo == GameManager.GameMode.PVE) {
            if (vencedor instanceof AIPlayer) {
                mensagem = "Você perdeu";
            } else {
                mensagem = "Você venceu";
            }
        }
        vencedorLabel.setText(mensagem);

        String placar = gm.getPlayer1().getName() + ": " + gm.getPlayer1().getScore() +
                "  |  " +
                gm.getPlayer2().getName() + ": " + gm.getPlayer2().getScore();
        placarFinalLabel.setText(placar);
    }

    @FXML
    private void aoClicarJogarNovamente() {
        AudioManager.getInstance().retomarMusicaFundo();
        jogodamemoria.memorymath.transitions.SceneManager.getInstance().carregarCena("/fxml/config-view.fxml");
    }

    @FXML
    private void aoClicarMenuPrincipal() {
        AudioManager.getInstance().retomarMusicaFundo();
        jogodamemoria.memorymath.transitions.SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }

    @FXML
    private void aoClicarEncerrar() {
        System.exit(0);
    }
}