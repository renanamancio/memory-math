package jogodamemoria.memorymath.model;

import javafx.scene.paint.Color;

/**
 * Classe que representa uma carta do jogo Memory Math.
 * Define a estrutura de uma carta com seu tipo de operação e valor.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class Card {

    /**
     * Enum que define os tipos de operações matemáticas disponíveis no jogo.
     * Cada tipo possui uma cor específica para identificação visual.
     */
    public enum OperationType {
        SOMA(Color.web("#4A90E2")),
        SUBTRACAO(Color.web("#7ED321")),
        MULTIPLICACAO(Color.web("#F5A623")),
        DIVISAO(Color.web("#9013FE"));

        private final Color color;

        /**
         * Construtor do enum OperationType.
         * 
         * @param color Cor associada ao tipo de operação
         */
        OperationType(Color color) {
            this.color = color;
        }

        /**
         * Obtém a cor associada ao tipo de operação.
         * 
         * @return Cor do tipo de operação
         */
        public Color getColor() {
            return color;
        }
    }

    private final int pairId;
    private final String value;
    private final OperationType type;

    /**
     * Construtor da classe Card.
     * 
     * @param pairId ID do par ao qual a carta pertence
     * @param value Valor da carta (expressão ou resultado)
     * @param type Tipo de operação da carta
     */
    public Card(int pairId, String value, OperationType type) {
        this.pairId = pairId;
        this.value = value;
        this.type = type;
    }

    /**
     * Obtém o ID do par ao qual a carta pertence.
     * 
     * @return ID do par
     */
    public int getPairId() { return pairId; }
    
    /**
     * Obtém o valor da carta.
     * 
     * @return Valor da carta
     */
    public String getValue() { return value; }
    
    /**
     * Obtém o tipo de operação da carta.
     * 
     * @return Tipo de operação
     */
    public OperationType getType() { return type; }
}