package jogodamemoria.memorymath.model;

import javafx.scene.paint.Color;

public class Card {

    public enum OperationType {
        SOMA(Color.web("#4A90E2")),
        SUBTRACAO(Color.web("#7ED321")),
        MULTIPLICACAO(Color.web("#F5A623")),
        DIVISAO(Color.web("#9013FE"));

        private final Color color;

        OperationType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private final int pairId;
    private final String value;
    private final OperationType type;

    public Card(int pairId, String value, OperationType type) {
        this.pairId = pairId;
        this.value = value;
        this.type = type;
    }

    public int getPairId() { return pairId; }
    public String getValue() { return value; }
    public OperationType getType() { return type; }
}