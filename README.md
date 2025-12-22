# 🎟️ TicketPro

**TicketPro** é uma aplicação **full stack baseada em microserviços** para gerenciamento de eventos, ingressos e pedidos, utilizando **Go**, **Java**, **Vue.js** e **PostgreSQL**, totalmente orquestrada com **Docker**.

O projeto foi desenvolvido com foco em **arquitetura desacoplada**, **processamento assíncrono** e **separação clara de responsabilidades**, servindo tanto como estudo quanto base para evolução futura.

---

## 📌 Visão Geral

O TicketPro permite:

- Cadastro e consulta de **eventos**
- Emissão e controle de **ingressos**
- Criação e processamento de **pedidos**
- Registro de **transações**
- Processamento assíncrono via **worker Java**
- Interface web moderna em **Vue 3**

---

## 🧩 Arquitetura

```
┌────────────┐      ┌────────────┐      ┌──────────────┐
│  Web (Vue) │ ───▶ │ API (Go)   │ ───▶ │ Worker (Java)│
└────────────┘      └────────────┘      └──────────────┘
                          │
                          ▼
                    PostgreSQL
```

---

## 🚀 Tecnologias Utilizadas

### Backend
- Go (Golang)
- Java + Gradle
- PostgreSQL

### Frontend
- Vue 3
- TypeScript
- Vite

### Infraestrutura
- Docker
- Docker Compose

---

## 📂 Estrutura do Projeto

```
ticketpro/
├── api-go/
├── worker-java/
├── web-vue/
├── db/
└── docker-compose.yml
```

---

## ⚙️ Pré-requisitos

- Docker
- Docker Compose
- Git

---

## ▶️ Como Executar

```bash
git clone https://github.com/Esdras-Garcia/ticketpro.git
cd ticketpro
docker-compose up --build
```

- Frontend: http://localhost:5173
- API: http://localhost:8080

---

## 🧪 Banco de Dados

Inicializado automaticamente via:
```
db/01_db_structure.sql
```

---

## 🛣️ Roadmap

- Autenticação JWT
- Mensageria (RabbitMQ/Kafka)
- Observabilidade
- Testes automatizados

---

## 📄 Licença

MIT

---

## ✨ Autor

**Esdras Garcia**  
https://github.com/Esdras-Garcia
