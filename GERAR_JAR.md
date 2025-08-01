# 🚀 Como Gerar o JAR Executável

Este guia explica como gerar um arquivo JAR executável do Memory Math.

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6 ou superior

## 🔧 Passos para Gerar o JAR

### 1. Compilar o Projeto
```bash
mvn clean compile
```

### 2. Gerar o JAR com Dependências
```bash
mvn clean package
```

### 3. Localizar o JAR
O arquivo JAR será gerado em:
```
target/memoryMath-1.0-SNAPSHOT.jar
```

## 🎯 Executar o JAR

### Windows
```cmd
java -jar target/memoryMath-1.0-SNAPSHOT.jar
```

### Linux/Mac
```bash
java -jar target/memoryMath-1.0-SNAPSHOT.jar
```

## 📦 Distribuição

Para distribuir o jogo:

1. **Copie o JAR**: `target/memoryMath-1.0-SNAPSHOT.jar`
2. **Crie uma pasta**: `MemoryMath/`
3. **Adicione o JAR**: Coloque o JAR na pasta
4. **Crie um script de execução**:

### Windows (executar.bat)
```batch
@echo off
java -jar memoryMath-1.0-SNAPSHOT.jar
pause
```

### Linux/Mac (executar.sh)
```bash
#!/bin/bash
java -jar memoryMath-1.0-SNAPSHOT.jar
```

## 🔍 Solução de Problemas

### Erro: "No main manifest attribute"
```bash
# Regenere o JAR
mvn clean package
```

### Erro: "JavaFX runtime components are missing"
```bash
# Use o plugin JavaFX para executar
mvn clean javafx:run
```

### Erro: "Could not find or load main class"
```bash
# Verifique se o JAR foi gerado corretamente
ls -la target/memoryMath-1.0-SNAPSHOT.jar
```

## 🛠️ Configurações Avançadas

### JAR Otimizado
```bash
mvn clean package -DskipTests
```

### JAR com Debug
```bash
mvn clean package -X
```

### Verificar Conteúdo do JAR
```bash
jar -tf target/memoryMath-1.0-SNAPSHOT.jar
```

## 📁 Estrutura do JAR

O JAR contém:
- Classes compiladas do projeto
- Recursos (FXML, CSS, imagens, sons)
- Dependências (JavaFX, Gson, etc.)
- Manifest com classe principal

## 🎮 Execução Alternativa

Se o JAR não funcionar, use:
```bash
mvn clean javafx:run
```

## 📞 Suporte

Para problemas com a geração do JAR:
1. Verifique se o Java e Maven estão instalados
2. Execute `mvn clean` antes de tentar novamente
3. Verifique se não há erros de compilação
4. Consulte o README.md para mais informações

---

**Desenvolvido com ❤️ pela equipe MemoryMath** 