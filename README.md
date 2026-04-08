<div align="center">

<h1>Customer Feedback Microservices Platform</h1>

<p>Uma arquitetura de microsserviços completa, robusta e observável construída com <strong>Spring Boot</strong> e <strong>Spring Cloud</strong> — simulando um ambiente real de produção para coleta e gerenciamento de feedbacks de usuários.</p>

<br/>

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

</div>

---

## Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Funcionalidades](#-funcionalidades)
- [Stack Tecnológica](#-stack-tecnológica)
- [Como Executar](#-como-executar)
- [Documentação da API](#-documentação-da-api)
- [Observabilidade](#-observabilidade)
- [Segurança](#-segurança)

---

## 🧭 Visão Geral

Este projeto implementa um **ecossistema de microsserviços** de ponta a ponta, cobrindo desde autenticação até observabilidade distribuída. Todos os serviços são **100% conteinerizados** via Docker Compose e se comunicam através de um API Gateway centralizado.

### O que o sistema oferece:

- Cadastro e autenticação de usuários com tokens **JWT**
- Criação e gerenciamento de **feedbacks** com controle de permissões
- **Gateway centralizado** com roteamento inteligente e camada de segurança
- Envio assíncrono de e-mails via **RabbitMQ**
- Stack completa de observabilidade: **Prometheus**, **Grafana** e **Zipkin**

---

## 🏗️ Arquitetura

```
                          ┌─────────────────────────────────────────────┐
                          │              CLIENTES EXTERNOS               │
                          └─────────────────────┬───────────────────────┘
                                                 │ HTTP :8080
                                                 ▼
                          ┌─────────────────────────────────────────────┐
                          │              API GATEWAY (:8080)             │
                          │   JWT Validation · Routing · Load Balance   │
                          └──────┬──────────────┬──────────────┬────────┘
                                 │              │              │
               ┌─────────────────▼──┐  ┌────────▼──────┐  ┌───▼────────────────┐
               │   USER SERVICE     │  │  AUTH SERVICE  │  │  FEEDBACK SERVICE  │
               │       (:8802)      │  │    (:8803)     │  │      (:8804)        │
               └─────────┬──────────┘  └───────────────┘  └────────┬───────────┘
                         │                                           │
               ┌──────────▼──────────┐              ┌───────────────▼──────────┐
               │  postgres-user-db   │              │  postgres-feedback-db    │
               └─────────────────────┘              └──────────────────────────┘
                                                                     │
                                                          ┌──────────▼──────────┐
                                                          │      RABBITMQ       │
                                                          └──────────┬──────────┘
                                                                     │
                                                          ┌──────────▼──────────┐
                                                          │    MAIL SERVICE     │
                                                          │  (Brevo SMTP API)   │
                                                          └─────────────────────┘

               ┌─────────────────────────────────────────────────────────────────┐
               │                     OBSERVABILIDADE                             │
               │       Prometheus (:9090) · Grafana (:3000) · Zipkin (:9411)    │
               └─────────────────────────────────────────────────────────────────┘

               ┌─────────────────────────────────────────────────────────────────┐
               │              SERVICE DISCOVERY — Eureka (:8761)                 │
               └─────────────────────────────────────────────────────────────────┘
```

### Serviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `api-gateway` | 8080 | Ponto único de entrada, autenticação JWT e roteamento |
| `eureka-server` | 8761 | Registro e descoberta de serviços |
| `user-service` | 8802 | CRUD de usuários e autenticação |
| `auth-service` | 8803 | Validação de credenciais e emissão de tokens JWT |
| `feedback-service` | 8804 | Gerenciamento de feedbacks com controle de permissões |
| `mailservice` | — | Worker assíncrono para envio de e-mails (Brevo API) |

---

## ✨ Funcionalidades

- **API Gateway Centralizado** — ponto único de entrada com `SecretHeaderFilter` que bloqueia acesso direto aos serviços internos
- **Autenticação e Autorização JWT** — fluxo completo de registro, login e controle de acesso por token
- **RBAC (Role-Based Access Control)** — separação clara entre rotas `ADMIN` e `USER`
- **Comunicação Assíncrona** — mensageria com RabbitMQ para envio de e-mails desacoplado do fluxo principal
- **Service Discovery** — registro dinâmico com Netflix Eureka
- **Observabilidade nos 3 pilares** — métricas (Prometheus + Grafana), rastreamento distribuído (Zipkin) e logs com Trace IDs (Micrometer)
- **Padronização de Respostas** — envelope de resposta consistente em todos os endpoints

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologias |
|---|---|
| **Backend** | Java 21, Spring Boot 3.3.5 |
| **Spring Cloud** | Spring Cloud Gateway, Netflix Eureka |
| **Segurança** | Spring Security, JWT |
| **Banco de Dados** | Spring Data JPA, PostgreSQL |
| **Mensageria** | Spring AMQP, RabbitMQ |
| **Observabilidade** | Actuator, Micrometer, Prometheus, Grafana, Zipkin |
| **Infraestrutura** | Docker, Docker Compose, Maven |

---

## ▶️ Como Executar

### Pré-requisitos

- [Docker](https://www.docker.com/) instalado e em execução
- [Git](https://git-scm.com/) instalado

> Nenhuma instalação adicional é necessária. Toda a orquestração é feita automaticamente pelo `docker-compose.yml`.

### Passo a passo

**1. Clone o repositório**

```bash
git clone https://github.com/brunofdev/portfolio-microservices.git
cd portfolio-microservices/servicos
```

**2. Suba todos os containers**

```bash
docker-compose up --build
```

Aguarde entre **10 e 20 minutos** na primeira execução (tempo varia conforme a velocidade da internet). Todos os serviços irão subir automaticamente.

> **Dica:** Se algum container não iniciar, abra o Docker Desktop, vá em **Containers** e ligue-os manualmente.

**3. Verifique se tudo está rodando**

Acesse o Eureka Dashboard para confirmar que todos os serviços estão registrados:

```
http://localhost:8761
```

---

## 📡 Documentação da API

**URL Base:** `http://localhost:8080` — **todas as requisições devem passar pelo API Gateway.**

---

### Rotas Públicas

#### `POST /api/users/register` — Cadastrar usuário

**Usuário comum (role `USER`):**

```json
{
  "name": "Usuario Teste",
  "userName": "dockertest",
  "password": "senhaforte123@",
  "email": "teste@email.com"
}
```

**Usuário administrador (role `ADMIN`):**

> Para fins de teste, inclua `@#$ADMIN$#@` no campo `userName` para criar um usuário com permissões de administrador.

```json
{
  "name": "Adm Paulo",
  "userName": "paulo@#$ADMIN$#@",
  "password": "Itried1997@@",
  "email": "paulo@gmail.com"
}
```

**Resposta:** `201 Created`

---

#### `POST /api/auth/login` — Autenticar usuário

```json
{
  "userName": "dockertest",
  "password": "senhaforte123@"
}
```

**Resposta:** `200 OK`

```json
{
  "status": true,
  "message": "Usuário autenticado com sucesso",
  "dados": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userResponseDTO": {
      "username": "DOCKERTEST",
      "role": "USER"
    }
  }
}
```

> Copie o `token` retornado para autenticar as próximas requisições.

---

#### `GET /api/feedback/getallfeedbacks` — Listar feedbacks (público)

Não requer autenticação.

**Resposta:** `200 OK`

---

### Rotas Protegidas

Para as rotas abaixo, adicione o header de autorização em todas as requisições:

```
Authorization: Bearer <seu_token_jwt>
```

---

#### `POST /api/feedback/create` — Criar feedback

> Permissão: `USER` ou `ADMIN`

```json
{
  "userFeedback": "Ótima experiência com o sistema!",
  "userRating": 5
}
```

**Resposta:** `201 Created`

---

#### `GET /api/users/getusers` — Listar todos os usuários

> Permissão: `ADMIN` apenas

**Resposta:** `200 OK`

---

#### `DELETE /api/feedback/deletefeedback/{id}` — Deletar feedback

> Permissão: `ADMIN` apenas

```
DELETE /api/feedback/deletefeedback/1
```

**Resposta:** `200 OK`

---

### Teste de Segurança

Este teste valida que os serviços internos **não são acessíveis diretamente**, apenas através do Gateway.

```
GET http://localhost:8802/api/users/getusers
```

**Resposta esperada:** `403 Forbidden`

```
Acesso direto nao permitido.
```

Se você receber o `403`, o `SecretHeaderFilter` está funcionando corretamente. ✅

---

## 📊 Observabilidade

Após subir todos os containers, os seguintes painéis estarão disponíveis:

| Ferramenta | URL | Credenciais |
|---|---|---|
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin` / `admin` |
| **Prometheus** | [http://localhost:9090](http://localhost:9090) | — |
| **Zipkin** | [http://localhost:9411/zipkin/](http://localhost:9411/zipkin/) | — |
| **Eureka** | [http://localhost:8761](http://localhost:8761) | — |
| **RabbitMQ** | [http://localhost:15672](http://localhost:15672) | `guest` / `guest` |

---

## 🔐 Segurança

A arquitetura implementa duas camadas de proteção:

1. **JWT no Gateway** — todas as rotas protegidas exigem um token válido, validado pelo API Gateway antes de qualquer repasse ao serviço interno.

2. **SecretHeaderFilter** — cada serviço interno verifica um header secreto injetado exclusivamente pelo Gateway. Requisições diretas às portas internas resultam em `403 Forbidden`, impossibilitando o bypass da camada de autenticação.

---

<div align="center">

Desenvolvido por **Bruno F.**

</div>
