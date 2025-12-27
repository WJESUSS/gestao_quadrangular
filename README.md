📌 Sistema de Gestão Quadrangular

Sistema completo de gestão administrativa e ministerial de igrejas, desenvolvido em Java 21 + Spring Boot, com foco em organização por células, liderança, membros, relatórios, finanças e segurança.

Projeto estruturado com boas práticas profissionais, uso extensivo de DTOs, Spring Security com JWT, JPA/Hibernate, e preparado para execução com Docker.

🚀 Tecnologias Utilizadas
Backend

Java 21

Spring Boot

Spring Data JPA (Hibernate)

Spring Security + JWT

Spring Validation

Maven

Banco de Dados

PostgreSQL

JPA/Hibernate

Migrations automáticas (DataLoader)

Infraestrutura

Docker

Docker Compose

Arquitetura

Arquitetura em Camadas

Separação clara de responsabilidades

DTOs para Request e Response

Tratamento global de exceções

🏗️ Arquitetura do Projeto
src/main/java/com/igreja/GestaoQuadrangular
│
├── application
│   └── dto              # DTOs de entrada e saída
│
├── domain
│   ├── entity           # Entidades JPA
│   └── repository       # Repositórios
│
├── servicce             # Camada de regras de negócio
│
├── infrastructure
│   ├── security         # Configurações de segurança e JWT
│   ├── mail             # Serviços de e-mail
│   └── config           # Configurações gerais
│
├── web
│   ├── controller       # Controllers REST
│   └── exception        # Tratamento global de exceções
│
└── GestaoQuadrangularApplication.java

🔐 Segurança

Autenticação via JWT

Controle de acesso por roles

ROLE_PASTOR

ROLE_LIDER

ROLE_SECRETARIA

ROLE_TESOURARIA

Proteção de endpoints com Spring Security

Conversão personalizada de authorities (CustomJwtAuthenticationConverter)

📦 Principais Módulos
👤 Usuários & Autenticação

Login com JWT

Criação de usuários

Controle de permissões

⛪ Membros

Cadastro, atualização e arquivamento

Histórico espiritual

Transferência de membresia

Relatórios de presença e fidelidade

👥 Liderança

Cadastro de líderes

Promoção de líder para pastor

Validação de vínculo com usuário

DTOs específicos para promoção (PromoverLiderDTO, PromoverParaPastorDTO)

🧑‍💼 Pastores

Cadastro de perfil pastoral

Dashboard do pastor

Visualização de dados estratégicos

Uso de PastorResponseDTO para evitar LazyInitializationException

🏠 Células

Cadastro de células

Metas de crescimento

Multiplicação

Relatórios semanais

Presença por célula

📊 Relatórios & Dashboards

Crescimento mensal

Relatórios financeiros

Relatórios semanais

Indicadores estratégicos

💬 Comunicação

Chat interno

Mensagens por célula

👶 Secretaria

Apresentação de crianças

Escola Bíblica

Visitantes

💰 Tesouraria

Contribuições

Ofertas

Dashboard financeiro

📄 Padrão de DTOs

O projeto não expõe entidades JPA diretamente nos controllers.

✔️ Request DTO
✔️ Response DTO
✔️ Evita LazyInitializationException
✔️ API segura e estável

Exemplo:

PastorResponseDTO
PromoverLiderDTO
CriarPastorDTO

🐳 Executando com Docker
1️⃣ Subir o banco e a aplicação
docker-compose up -d

2️⃣ Acessar a API
http://localhost:8080

⚙️ Executando Localmente (sem Docker)
Pré-requisitos

Java 21

PostgreSQL

Maven

Passos
mvn clean install
mvn spring-boot:run

🧪 Testes

Estrutura preparada para testes com Spring Boot

Classe base:

GestaoQuadrangularApplicationTests

📌 Boas Práticas Aplicadas

✔️ Clean Code
✔️ DTO Pattern
✔️ Exception Handler Global
✔️ Separação de Camadas
✔️ Segurança com JWT
✔️ Código pronto para escalar

👨‍💻 Autor

Washington Santos
Desenvolvedor Java | Spring Boot | Backend
Projeto em constante evolução 🚀

📜 Licença

Este projeto está sob a licença MIT.
Sinta-se livre para estudar, adaptar e evoluir.

Se quiser, no próximo passo eu posso:

Ajustar esse README para inglês

Criar um README para portfólio profissional

Montar uma descrição perfeita para o GitHub
