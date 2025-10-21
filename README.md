#  PLATAFORMA DE MICROSSERVIÇOS PARA FEEDBACK DE CLIENTES

---

## 🧭 VISÃO GERAL

Este projeto apresenta uma **arquitetura de microsserviços completa, robusta e observável**, desenvolvida com **Spring Boot** e **Spring Cloud**.  
O sistema simula um ambiente real de produção voltado para **coleta e gerenciamento de feedbacks de usuários**, oferecendo:

- Cadastro e autenticação de usuários com geração de tokens **JWT**  
- Criação e gerenciamento de **feedbacks**  
- **Gateway centralizado** com regras de segurança e roteamento inteligente  
- Envio assíncrono de e-mails utilizando **RabbitMQ**  
- Controle de acesso baseado em **perfis de permissão (Roles)**  

Toda a arquitetura é **100% conteinerizada** via **Docker Compose**, com uma **stack completa de observabilidade** composta por **Prometheus**, **Grafana** e **Zipkin**.

---

## ⚙️ PRINCIPAIS FUNCIONALIDADES

- 🧩 **API Gateway Centralizado** — ponto único de entrada para todo o tráfego externo.  
- 🔐 **Autenticação e Autorização JWT** — fluxo completo de registro, login e controle de acesso.  
- 🧑‍💼 **Segurança baseada em Roles** — separação entre rotas `ADMIN` e `USER`.  
- 📨 **Comunicação Assíncrona** — mensageria com **RabbitMQ** para envio de e-mails desacoplado.  
- 🌐 **Descoberta de Serviços** — registro dinâmico com **Netflix Eureka**.  
- 📊 **Observabilidade Completa (3 pilares)**:  
  - **Métricas:** Prometheus + Grafana  
  - **Rastreamento:** Zipkin  
  - **Logs:** Micrometer + Trace IDs  
- 🛡️ **Resiliência e Segurança:** impede acesso direto aos serviços internos e valida entidades antes da persistência.
- Padronização de respostas de todos os endpoints.

---

## 🏗️ ARQUITETURA DO SISTEMA

O sistema é executado em um **ecossistema de múltiplos contêineres Docker interconectados**, conforme estrutura abaixo:

### 🔧 Serviços Principais
1. **api-gateway** → Roteamento, autenticação JWT, roles e balanceamento de carga.  
2. **eureka-server** → Descoberta e registro de serviços.  
3. **user-service** → CRUD de usuários e autenticação.  
4. **auth-service** → Validação de credenciais e emissão de tokens JWT.  
5. **feedback-service** → Gerenciamento de feedbacks (com validação de usuários e permissões).  
6. **mailservice** → Worker assíncrono responsável por envio de e-mails via API externa (Brevo).

### 🗄️ Infraestrutura de Suporte
7. **postgres-user-db** → Banco de dados do `user-service`.  
8. **postgres-feedback-db** → Banco de dados do `feedback-service`.  
9. **rabbitmq** → Broker de mensagens assíncronas.  

### 🧠 Observabilidade
10. **prometheus** → Coleta e armazena métricas.  
11. **grafana** → Visualização e dashboards.  
12. **zipkin** → Rastreamento distribuído entre serviços.  

> 💡 Toda a configuração de rede, variáveis de ambiente e dependências já está definida no `docker-compose.yml`.

---

## 🧰 TECNOLOGIAS UTILIZADAS

### 🖥️ Backend
- Java 21  
- Spring Boot 3.3.5  

### ☁️ Spring Cloud
- Spring Cloud Gateway  
- Netflix Eureka  

### 🔒 Segurança
- Spring Security  
- JWT (JSON Web Token)  

### 💾 Banco de Dados
- Spring Data JPA  
- PostgreSQL  

### 📨 Mensageria
- Spring AMQP  
- RabbitMQ  

### 📈 Observabilidade
- Spring Boot Actuator  
- Micrometer  
- Prometheus  
- Grafana  
- Zipkin  

### 🐳 Infraestrutura
- Docker & Docker Compose  
- Maven  

---

## ▶️ COMO EXECUTAR O PROJETO

### ✅ Requisitos
- Docker instalado e ativo  
- IDE (recomendado: IntelliJ IDEA)  
- Git instalado  

> ⚡ Tudo é orquestrado automaticamente pelo arquivo `docker-compose.yml`.

---

### 1️⃣ Clonar o Repositório
Abra um terminal e execute:  

1.1 git clone https://github.com/brunofdev/portfolio-microservices.git  
1.2 cd .\portfolio-microservices\  
1.3 cd servicos  

### 2️⃣ Construir o Ambiente (Build)   
**PRECISA TER O DOCKER INSTALADO**  

Já estando dentro da pasta "servicos" no terminal que estiver utilizando, rode:  

2.1  docker-compose up --build 

Somente este comando rode, e a mágica acontecera, pode demorar alguns minutos,  
maximo de 20 minutos para que tudo esteja funcionado (De acordo com a internet que você está utilizando)

### obs: as vezes é necessario ligar manualmente os containers na aba "containers" do Docker Hub  

----------------------------------------------------------------  
  
Documentação de Testes da API (Ambiente Local)  -> Somente após todos containers docker estar rodando.
Este guia descreve como testar os endpoints da arquitetura de microsserviços rodando localmente via Docker Compose.  
  
URL Base de Todas as Requisições: http://localhost:8080 (Todas as chamadas devem ser feitas para o API Gateway)  
  
# TESTES  
----------------------------------------------------------------  
## 1. Rotas Públicas (Não exigem autenticação)  
### 1.1. Cadastrar Novo Usuário  
Cria um novo usuário   
Obs: Para criar um usuario com permissão de admin, adicione na requisição dentro do campo "name" o valor @#$ADMIN$#@, por exemplo:  

Método post para URL: http://localhost:8080/api/users/register  
Body:  
{  
  "name": "Adm Paulo",  
  "userName": "teste@#$ADMIN$#@", <--- Isso é uma regra que implementei para facilitar a criação de um usuário com permissão de Administrador, Apenas para teste.  
  "password": "Itried1997@@",   
  "email": "dwq@gmail.com"  
}  

Usuario com permissão  User:  
Método: POST  

URL: http://localhost:8080/api/users/register  
  
Corpo (Body) -> raw -> JSON:  
  
JSON  
  
{  
  "name": "Usuario Teste Docker",  
  "userName": "dockertest",  
  "password": "senhaforte123@",  
  "email": "teste@email.com"  
}  
Resposta Esperada: 201 Created  
  
----------------------------------------------------------------  
### 1.2. Fazer Login  
Autentica um usuário e retorna um token JWT.  
  
Método: POST  
  
URL: http://localhost:8080/api/auth/login  
  
Corpo (Body) -> raw -> JSON:  
  
-- JSON  
  
{  
  "userName": "dockertest",  
  "password": "senhaforte123@"  
}  
Resposta Esperada: 200 OK (Contendo o token e a role).  
A resposta contém um JWT, que será utilizado em requisições que exigem autenticação  
  
-- Exemplo de retorno da Api:  
  
{  
    "status": true,  
    "message": "Usuário autenticado com sucesso",  
    "dados": {  
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6IlRFU1RFRSIsImlhdCI6MTc2MTA2NDA3OSwiZXhwIjoxNzYxMDY0OTc5fQ.JDLBXulCFF3YE2HL7iXSOQFKO5jrT0Tat0dALt7K-Kk",  
        "userResponseDTO": {  
            "username": "TESTEE",  
            "role": "USER"  
        }  
    }  
}  
  
  
----------------------------------------------------------------  
  
Ação: Copie o token da resposta para usar nos testes seguintes.  
  
### 1.3. Listar Todos os Feedbacks  
Busca a lista pública de feedbacks.  
  
Método: GET  
  
URL: http://localhost:8080/api/feedback/getallfeedbacks  
  
Resposta Esperada: 200 OK  
  
- Não é exigido JWT aqui nesta requisição.  
----------------------------------------------------------------  
2. Rotas Protegidas (Exigem Autenticação)  
Para todas as requisições abaixo, vá até a aba "Authorization" no Postman, selecione "Bearer Token" e cole o JWT obtido no login.  
----------------------------------------------------------------  
  
### 2.1. Criar um Novo Feedback (Permissão: USER ou ADMIN)  
Método: POST  
  
URL: http://localhost:8080/api/feedback/create  
  
Corpo (Body) -> raw -> JSON:   
  
JSON  
  
{  
  "userFeedback": "Testando o fluxo de feedback no Docker!",  
  "userRating": 5  
}  
Resposta Esperada: 201 Created.  
----------------------------------------------------------------  
  
### 2.2. Listar Todos os Usuários (Permissão: ADMIN)  
Nota: Você deve estar logado com um usuário que tenha a role "ADMIN".   
  
  
Método: GET  
  
URL: http://localhost:8080/api/users/getusers  
  
Resposta Esperada: 200 OK (com a lista de usuários).  
  
### 2.3. Deletar um Feedback (Permissão: ADMIN)  
Nota: Você deve estar logado com um usuário que tenha a role "ADMIN".  
    
Método: DELETE  
  
URL: http://localhost:8080/api/feedback/deletefeedback/{id} (Ex: http://localhost:8080/api/feedback/deletefeedback/1)  
  
Resposta Esperada: 200 OK.   
  
### 3. Teste de Segurança (Acesso Direto Negado)  
Este teste deve falhar. Ele prova que seus microsserviços internos estão protegidos pelo SecretHeaderFilter e não podem ser acessados diretamente.  
  
### 3.1. Tentativa de Acesso Direto ao user-service  
Método: GET  
  
URL: http://localhost:8802/api/users/getusers (Note que estamos usando a porta direta do user-service, não a porta 8080 do Gateway).  
  
Resposta Esperada: 403 Forbidden  
  
Corpo da Resposta (Esperado): Acesso direto nao permitido.  
  
Se você receber o erro 403, seu filtro de segurança está funcionando perfeitamente!  

----------------------------------------------------------------

# Acessos de Infraestrutura: (Containers precisam estar ligados)  
  
Grafana ->  
Prometheus ->  
Zipkin -> 
Eureka -> 
  
