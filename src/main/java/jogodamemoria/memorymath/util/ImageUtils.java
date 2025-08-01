package jogodamemoria.memorymath.util;

import javafx.scene.image.Image;
import jogodamemoria.memorymath.util.ResourceLoadException;

/**
 * Classe utilitária para carregamento de imagens na aplicação.
 * Fornece métodos para carregar imagens de forma segura com tratamento de erros.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class ImageUtils {
    
    /**
     * Carrega uma imagem a partir do caminho especificado.
     * 
     * @param caminho Caminho da imagem no classpath
     * @return Objeto Image carregado
     * @throws ResourceLoadException Se houver erro ao carregar a imagem
     */
    public static Image carregarImagem(String caminho) throws ResourceLoadException {
        try {
            Image img = new Image(ImageUtils.class.getResourceAsStream(caminho));
            if (img.isError()) {
                throw new ResourceLoadException("Erro ao carregar imagem: " + caminho);
            }
            return img;
        } catch (Exception e) {
            throw new ResourceLoadException("Erro ao carregar imagem: " + caminho + " - " + e.getMessage());
        }
    }
} 