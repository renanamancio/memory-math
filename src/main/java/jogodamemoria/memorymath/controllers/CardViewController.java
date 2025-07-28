package jogodamemoria.memorymath.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import jogodamemoria.memorymath.model.Card;

public class CardViewController {

    @FXML private StackPane cardFacePane;
    @FXML private ImageView cardBackImage;
    @FXML private ImageView cardFaceBackgroundImage;
    @FXML private Label cardValueLabel;

    private Card dadosCarta;
    private boolean cartaVirada = false;
    private boolean cartaEncontrada = false;

    private static final Image IMG_VERSO = new Image(CardViewController.class.getResourceAsStream("/images/verso-carta.png"));
    private static final Image IMG_SOMA = new Image(CardViewController.class.getResourceAsStream("/images/azulCerebro.png"));
    private static final Image IMG_SUBTRACAO = new Image(CardViewController.class.getResourceAsStream("/images/verdeLogico.jpg"));
    private static final Image IMG_MULTIPLICACAO = new Image(CardViewController.class.getResourceAsStream("/images/amareloEstimulo.jpg"));
    private static final Image IMG_DIVISAO = new Image(CardViewController.class.getResourceAsStream("/images/roxoDesafio.png"));

    @FXML
    public void initialize() {
        cardBackImage.setImage(IMG_VERSO);
        cardFacePane.setVisible(false);
    }

    public void definirDados(Card dadosCarta) {
        this.dadosCarta = dadosCarta;
        cardValueLabel.setText(dadosCarta.getValue());

        switch (dadosCarta.getType()) {
            case SOMA:
                cardFaceBackgroundImage.setImage(IMG_SOMA);
                break;
            case SUBTRACAO:
                cardFaceBackgroundImage.setImage(IMG_SUBTRACAO);
                break;
            case MULTIPLICACAO:
                cardFaceBackgroundImage.setImage(IMG_MULTIPLICACAO);
                break;
            case DIVISAO:
                cardFaceBackgroundImage.setImage(IMG_DIVISAO);
                break;
        }
    }

    public void virarParaCima() {
        if (cartaVirada || cartaEncontrada) return;
        cartaVirada = true;

        FadeTransition ftOut = new FadeTransition(Duration.millis(200), cardBackImage);
        ftOut.setFromValue(1.0);
        ftOut.setToValue(0.0);

        ftOut.setOnFinished(event -> {
            cardFacePane.setVisible(true);
        });
        ftOut.play();
    }

    /**
     * Executa a animação para virar a carta para baixo (mostrar o verso).
     */
    public void virarParaBaixo() {
        if (!cartaVirada || cartaEncontrada) return;
        cartaVirada = false;

        FadeTransition ftOut = new FadeTransition(Duration.millis(200), cardFacePane);
        ftOut.setFromValue(1.0);
        ftOut.setToValue(0.0);

        ftOut.setOnFinished(event -> {
            cardBackImage.setVisible(true);
        });
        ftOut.play();
    }

    public void definirEncontrada() {
        this.cartaEncontrada = true;
        this.cardFacePane.getParent().setOpacity(0.65);
    }

    public Card obterDadosCarta() { return dadosCarta; }
    public boolean cartaEstaVirada() { return cartaVirada; }
    public boolean cartaEstaEncontrada() { return cartaEncontrada; }
}