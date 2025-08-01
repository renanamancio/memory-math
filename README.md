# Memory Math - Jogo de Memória Matemática


Um jogo educativo de memória que combina matemática com diversão! Encontre pares de cartas que formem operações matemáticas válidas.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.13.0-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/Licenza-Commons_Clause%20%2B%20MIT-red)](LICENSE.md)

<p align="center">
  <img width="350" height="350" alt="logoJogo" src="https://github.com/user-attachments/assets/d813046b-c188-4113-b2ea-07871c8c2761" />
</p>


## 🎮 Características

- **Sistema de Pontuação**: +10 pontos por acerto, -3 pontos por erro
- **Modos de Jogo**: PvP (Jogador vs Jogador) e PvE (Jogador vs IA)
- **Dificuldades da IA**: Fácil, Médio e Difícil
- **Operações Matemáticas**: Soma, Subtração, Multiplicação e Divisão

- **Interface Responsiva**: Otimizada para desktop
- **Sons e Música**: Efeitos sonoros e música de fundo

## 🚀 Como Jogar

1. **Objetivo**: Encontre pares de cartas que formem operações matemáticas válidas
2. **Primeira Carta**: Clique em uma carta do grid de operações
3. **Segunda Carta**: Clique em uma carta do grid de resultados
4. **Pontuação**: 
   - Acertar um par: +5 pontos
   - Errar um par: -1 ponto
5. **Vitória**: Quem tiver mais pontos no final vence!

## 🛠️ Requisitos

- Java 21 ou superior
- Maven 3.6 ou superior

## 📦 Instalação e Execução

### Opção 1: Executar via Maven
```bash
# Clone o repositório
git clone https://github.com/seu-usuario/memoryMath.git
cd memoryMath

# Compile e execute
mvn clean javafx:run
```

### Opção 2: Gerar JAR Executável

Para gerar um arquivo JAR executável:

```bash
# Compile o projeto
mvn clean compile

# Gere o JAR com todas as dependências
mvn clean package

# O JAR será gerado em: target/memoryMath-1.0-SNAPSHOT.jar
```

### Executar o JAR
```bash
# Execute o JAR gerado
java -jar target/memoryMath-1.0-SNAPSHOT.jar
```

## 📁 Estrutura do Projeto

```
memoryMath/
├── src/main/java/jogodamemoria/memorymath/
│   ├── controllers/          # Controladores das telas
│   ├── model/               # Modelos de dados
│   ├── transitions/         # Gerenciador de transições
│   ├── util/               # Utilitários (áudio, etc.)
│   ├── AIPlayer.java       # Lógica da IA
│   ├── GameManager.java    # Gerenciador do jogo
│   ├── HumanPlayer.java    # Jogador humano
│   ├── Main.java           # Classe principal
│   └── Player.java         # Classe base do jogador
├── src/main/resources/
│   ├── css/                # Estilos CSS
│   ├── fxml/               # Arquivos FXML das telas
│   ├── images/             # Imagens do jogo
│   └── sounds/             # Arquivos de áudio
└── pom.xml                 # Configuração Maven
```

### 📁 Arquivos Criados/Modificados

- ✅ `instrucoes-view.fxml` - Tela de instruções
- ✅ `InstrucoesController.java` - Controlador das instruções
- ✅ `GERAR_JAR.md` - Instruções para JAR
- ✅ `run.bat` e `run.sh` - Scripts de execução
- ✅ Atualizado `pom.xml` com plugin Maven Shade
- ✅ Melhorado `README.md` com documentação completa

## 🎯 Funcionalidades Implementadas

### ✅ Correções Realizadas
- **Som de Derrota**: Corrigido para tocar quando a IA vence
- **Sistema de Pontuação**: Implementado (+10/-3 pontos)
- **Tela de Instruções**: Criada com regras completas
- **Otimização Desktop**: Interface redimensionada para desktop
- **Limpeza de Código**: Removidos métodos não utilizados
=======
1. **Clone o repositório**
   ```bash
   git clone https://github.com/renanamancio/memory-math
   cd memory-Math
   ```
>>>>>>> 9a51e4ab1c22d5290e05f072656cb4e33dfdf64f

### 🆕 Novas Funcionalidades
- **Responsividade**: Melhor adaptação para diferentes telas
- **Melhorias de UX**: Feedback visual e sonoro aprimorado

## 🎨 Interface

- **Design Moderno**: Interface limpa e intuitiva
- **Responsiva**: Adapta-se a diferentes tamanhos de tela
- **Feedback Visual**: Animações e efeitos visuais
- **Acessibilidade**: Cores contrastantes e textos legíveis

## 🔧 Configuração

### Desenvolvimento
```bash
# Instalar dependências
mvn dependency:resolve

# Executar testes (se houver)
mvn test

# Gerar documentação
mvn javadoc:javadoc
```

### Produção
```bash
# Gerar JAR otimizado
mvn clean package -DskipTests

# Executar JAR
java -jar target/memoryMath-1.0-SNAPSHOT.jar
```



## 🎵 Áudio

- **Música de Fundo**: Ambiente relaxante
- **Efeitos Sonoros**: 
  - Revelar carta
  - Acertar par
  - Errar par
  - Vitória
  - Derrota

## 🚀 Melhorias de Performance

- **Cache de Imagens**: Carregamento otimizado
- **Gerenciamento de Memória**: Liberação adequada de recursos
- **Animações Suaves**: Transições fluidas
- **Código Limpo**: Remoção de métodos não utilizados

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE.md` para mais detalhes.

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou sugestões, abra uma issue no repositório.

---

**Desenvolvido com ❤️ pela equipe MemoryMath** 
=======
### Padrões de Código
- Siga as convenções de nomenclatura Java
- Adicione comentários Javadoc para métodos públicos
- Mantenha a cobertura de testes acima de 80%
- Use nomes descritivos para variáveis e métodos

## 📝 Licença
Este projeto permite colaboração aberta, mas proíbe redistribuição, sublicenciamento e uso comercial.  
Leia o arquivo [LICENSE](LICENSE.md) para detalhes.

## 👥 Autores

- **Renan Henrique Batista Amancio** - *Desenvolvimento inicial* - [GitHub](https://github.com/renanamancio)

## 🙏 Agradecimentos

- JavaFX Community
- Maven Community
- Contribuidores e testadores do projeto

## 📞 Suporte

Se você encontrar algum problema ou tiver sugestões:

1. Verifique se há uma [issue](https://github.com/renanamancio/memory-math/issues) relacionada
2. Crie uma nova issue com detalhes do problema
3. Para dúvidas gerais, abra uma [discussion](https://github.com/renanamancio/memory-math/discussions)

---

**Divirta-se jogando Memory Math! 🎮🧮** 