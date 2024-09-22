# Project - Elo

Project - Elo, um aplicativo gestor de bibliotecas com recomendação de livros

## Requisitos

- Docker

## Configuração do Ambiente

Certifique-se de ter o Docker instalado na sua máquina.

### Passos para Executar

1. Clone o repositório:

```
git clone https://github.com/CharlesChaclim/project-elo.git
cd project-elo
```

2. Execute o Docker Compose para iniciar o backend (Java Spring Boot) e o banco de dados PostgreSQL:

```
docker-compose up --build
```

3. Após a inicialização, você poderá acessar a documentação (Swagger) em [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## Tecnologias Utilizadas

- Java 17
- PostgreSQL

## Premissas do Sistema

No sistema Project - Elo, algumas premissas são estabelecidas para facilitar o uso e teste do aplicativo:

- **Conexão do banco:**
  - Usuário: project
  - Senha: root

- **Usuário ADMIN:**
  - Nome de usuário: ADMIN
  - Senha: 123
  - Função: Este usuário tem privilégios administrativos completos no sistema.

- **Categorias:**
  - Existem pre-categorias cadastradas no sitemas