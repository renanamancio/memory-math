package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jogodamemoria.memorymath.util.AudioManager;

/**
 * Controlador para configurações de áudio do jogo.
 * Permite ao usuário controlar volume e ativar/desativar sons.
 */
public class ConfigAudioController {

    @FXML private CheckBox checkBoxSom;
    @FXML private CheckBox checkBoxMusica;
    @FXML private Slider sliderVolumeMusica;
    @FXML private Slider sliderVolumeEfeitos;
    @FXML private Label labelVolumeMusica;
    @FXML private Label labelVolumeEfeitos;
    @FXML private VBox containerConfigAudio;

    private AudioManager audioManager;

    @FXML
    public void initialize() {
        audioManager = AudioManager.getInstance();
        configurarInterface();
        configurarEventos();
        carregarConfiguracoes();
    }

    /**
     * Configura a interface inicial.
     */
    private void configurarInterface() {
        sliderVolumeMusica.setMin(0.0);
        sliderVolumeMusica.setMax(1.0);
        sliderVolumeMusica.setValue(audioManager.getVolumeMusica());
        
        sliderVolumeEfeitos.setMin(0.0);
        sliderVolumeEfeitos.setMax(1.0);
        sliderVolumeEfeitos.setValue(audioManager.getVolumeEfeitos());

        checkBoxSom.setSelected(audioManager.isSomAtivado());
        checkBoxMusica.setSelected(audioManager.isMusicaAtivada());

        atualizarLabelsVolume();
    }

    /**
     * Configura os eventos dos controles.
     */
    private void configurarEventos() {
        checkBoxSom.setOnAction(event -> {
            audioManager.setSomAtivado(checkBoxSom.isSelected());
        });

        checkBoxMusica.setOnAction(event -> {
            audioManager.setMusicaAtivada(checkBoxMusica.isSelected());
        });

        sliderVolumeMusica.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioManager.setVolumeMusica(newValue.doubleValue());
            atualizarLabelsVolume();
        });

        sliderVolumeEfeitos.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioManager.setVolumeEfeitos(newValue.doubleValue());
            atualizarLabelsVolume();
        });
    }

    /**
     * Carrega as configurações salvas.
     */
    private void carregarConfiguracoes() {
        checkBoxSom.setSelected(true);
        checkBoxMusica.setSelected(true);
        sliderVolumeMusica.setValue(0.3);
        sliderVolumeEfeitos.setValue(0.7);
    }

    /**
     * Atualiza os labels de volume.
     */
    private void atualizarLabelsVolume() {
        int volumeMusicaPercent = (int) (sliderVolumeMusica.getValue() * 100);
        int volumeEfeitosPercent = (int) (sliderVolumeEfeitos.getValue() * 100);
        
        labelVolumeMusica.setText("Volume Música: " + volumeMusicaPercent + "%");
        labelVolumeEfeitos.setText("Volume Efeitos: " + volumeEfeitosPercent + "%");
    }

    /**
     * Testa o som de efeitos.
     */
    @FXML
    private void testarSomEfeitos() {
        audioManager.tocarSomRevelarCarta();
    }

    /**
     * Testa a música de fundo.
     */
    @FXML
    private void testarMusicaFundo() {
        if (!audioManager.isMusicaAtivada()) {
            audioManager.setMusicaAtivada(true);
            audioManager.iniciarMusicaFundo();
        }
    }

    /**
     * Salva as configurações.
     */
    @FXML
    private void salvarConfiguracoes() {
        System.out.println("Configurações de áudio salvas!");
    }
} 