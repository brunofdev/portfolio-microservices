# 🚀 PLATAFORMA DE MICROSSERVIÇOS PARA FEEDBACK DE CLIENTES

---

## 🧭 VISÃO GERAL

Este projeto é uma **arquitetura de microsserviços completa, robusta e observável**, construída com **Spring Boot** e **Spring Cloud**.  
O sistema simula um ambiente real de produção para **coletar e gerenciar feedbacks de usuários**, incluindo:

- Cadatstro e autenticação de usuários gerando tokens(JWT)
- Postagem/criação de feedbacks
- Gerenciamento de endpoints centralizados por um gateway central com regras de segurança especificas
- Envio assíncrono de e-mails com RabbitMQ  
- Autorização baseada em permissões (Roles)

A arquitetura é **100% conteinerizada** com **Docker Compose**, e inclui uma **stack de observabilidade completa** com **Prometheus**, **Grafana** e **Zipkin**.

---

## ⚙️ PRINCIPAIS FUNCIONALIDADES

- 🧩 **API Gateway Centralizado** — único ponto de entrada (api-gateway) para todo o tráfego externo.  
- 🔐 **Autenticação e Autorização JWT** — fluxo completo de registro, login e controle de acesso.  
- 🧑‍💼 **Segurança por Permissão (Roles)** — separação entre rotas `ADMIN` e `USER`.  
- 📨 **Comunicação Assíncrona** — usa **RabbitMQ** para tarefas desacopladas (como envio de e-mails).  
- 🌐 **Descoberta de Serviços** — com **Netflix Eureka** para registro dinâmico.  
- 📊 **Observabilidade Completa (3 pilares)**:
  - **Métricas:** Prometheus + Grafana  
  - **Rastreamento:** Zipkin  
  - **Logs:** Micrometer + Trace IDs  
- 🛡️ **Resiliência:** impede acesso direto aos serviços internos e valida entidades antes da gravação no banco.

---

## 🏗️ ARQUITETURA DO SISTEMA

O projeto roda como um **ecossistema de múltiplos contêineres Docker interconectados**:

### 🔧 Serviços Principais
1. **api-gateway** → Roteamento, autenticação (JWT), roles e balanceamento de carga.  
2. **eureka-server** → Descoberta e registro de serviços.  
3. **user-service** → CRUD de usuários e dados de autenticação.  
4. **auth-service** → Validação de credenciais e emissão de tokens JWT.  
5. **feedback-service** → Gerenciamento de feedbacks (validação de usuário + permissões).  
6. **mailservice** → Worker assíncrono que envia e-mails via API externa (Brevo).

### 🗄️ Infraestrutura de Suporte
7. **postgres-user-db** → Banco de dados do `user-service`.  
8. **postgres-feedback-db** → Banco de dados do `feedback-service`.  
9. **rabbitmq** → Broker de mensagens assíncronas.

### 🧠 Observabilidade
10. **prometheus** → Coleta e armazena métricas.  
11. **grafana** → Visualiza métricas e dashboards.  
12. **zipkin** → Rastreia requisições distribuídas entre serviços.

> 💡 Toda a configuração de rede, variáveis de ambiente e dependências já está integrada no `docker-compose.yml`.

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

### 💾 Dados
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

> ⚡ Tudo é orquestrado automaticamente pelo `docker-compose.yml`.

### 1️⃣ Clonar o Repositório
Abrir um terminal (Por exemplo, pelo próprio Intelij, IDE que eu utilizo)
Navega até a pasta que deseja criar o projeto e rode:
1 - git clone https://github.com/seu-usuario/seu-repositorio.git
2 - cd seu-repositorio/serviços
3 - Sincroneze e baixe as dependencias de cada microserviço através do mavem (Clicar com o direito em cima do arquvio POM) de cada microserviço.

🧱 2️⃣ Construir o Ambiente (Build)
# Verifica se o Docker está ativo
**PRECISA TER O DOCKER INSTALADO**

# Remove containers e volumes antigos para evitar conflito
docker-compose down -v

# Faz o build das imagens Docker (recria do zero)
docker-compose up --build 

Geralmente o processo leva em torno de 10 minutos até que todos os serviços liguem e estejam disponiveis para testes.
Lembrando que o serviço de email, não enviara emails reais, pois a apikey do brevo (aplicação de email que utilizamos) é pessoal e intransferivel.

