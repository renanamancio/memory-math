package jogodamemoria.memorymath.util;

/**
 * Exceção personalizada para erros de carregamento de recursos.
 * Lançada quando há problemas ao carregar imagens, sons ou outros recursos.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class ResourceLoadException extends Exception {
    
    /**
     * Construtor da exceção ResourceLoadException.
     * 
     * @param message Mensagem de erro descritiva
     */
    public ResourceLoadException(String message) {
        super(message);
    }
} 