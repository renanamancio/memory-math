package jogodamemoria.memorymath.util;

import javafx.scene.control.Alert;

/**
 * Classe utilitária para exibição de alertas e diálogos na interface gráfica.
 * Fornece métodos estáticos para mostrar diferentes tipos de alertas.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class AlertUtils {
    
    /**
     * Exibe um alerta de erro na interface gráfica.
     * 
     * @param titulo Título do alerta
     * @param mensagem Mensagem de erro a ser exibida
     */
    public static void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Exibe um alerta de aviso na interface gráfica.
     * 
     * @param titulo Título do alerta
     * @param mensagem Mensagem de aviso a ser exibida
     */
    public static void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 