package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import jogodamemoria.memorymath.transitions.SceneManager;

/**
 * Controlador responsável pela tela de instruções do jogo.
 * Exibe as regras e como jogar o Memory Math.
 * 
 * @author MemoryMath Team
 * @version 2.0
 */
public class InstrucoesController {

    /**
     * Inicializa o controlador da tela de instruções.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Manipula o clique no botão "Começar Jogo".
     * Redireciona para a tela de configuração do jogo.
     */
    @FXML
    private void aoClicarComecarJogo() {
        SceneManager.getInstance().carregarCena("/fxml/config-view.fxml");
    }

    /**
     * Manipula o clique no botão "Voltar".
     * Retorna para o menu principal.
     */
    @FXML
    private void aoClicarVoltar() {
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }
} 