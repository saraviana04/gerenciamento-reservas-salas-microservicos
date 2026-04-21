# Gerenciamento de Reservas - Microservicos

Projeto demonstrativo de microservicos com mensageria RabbitMQ e frontend Angular (TypeScript).

**Resumo**
- Microservicos independentes por dominio (usuarios, salas, reservas)
- Mensageria com RabbitMQ para eventos
- Frontend Angular para testes e demonstracao

## Arquitetura
**Servicos**
- `service-usuarios` (porta 8081)
- `service-salas` (porta 8082)
- `service-reservas` (porta 8083)
- `service-notificacoes` (porta 8084)
- `common-events` (contratos de eventos)

**Mensageria**
- Exchange: `reservas.exchange`
- Evento principal: `reserva.criada`
- Consumidor: `service-notificacoes`

**Banco de dados**
- H2 em memoria por servico (um banco por servico)

**Frontend**
- Angular 17 + TypeScript
- Consome os microservicos via proxy do Angular (dev)

## Requisitos
- Java 17+
- Node.js 18+
- Docker (para RabbitMQ)

## Como rodar
**1) Subir RabbitMQ**
```bash
docker compose up -d
```
Acesse o painel em `http://localhost:15672` (user/pass: guest/guest).

**2) Rodar backend (microservicos)**
Abra um terminal para cada servico e rode dentro da pasta do modulo:

```bash
cd service-usuarios
../mvnw spring-boot:run
```

```bash
cd service-salas
../mvnw spring-boot:run
```

```bash
cd service-reservas
../mvnw spring-boot:run
```

```bash
cd service-notificacoes
../mvnw spring-boot:run
```

Se algum servico nao iniciar por "Endereco ja em uso", finalize processos nas portas 8081-8084 antes de iniciar.

**3) Rodar frontend (Angular)**
```bash
cd frontend
npm install
npm start
```
Abra `http://localhost:4200`.
As chamadas para `/api/usuarios`, `/api/salas` e `/api/reservas` sao encaminhadas para os servicos nas portas 8081-8083 via `frontend/proxy.conf.json`.

## Exemplo de chamada (reserva)
```bash
curl -X POST http://localhost:8083/api/reservas   -H 'Content-Type: application/json'   -d '{"salaId":1,"usuarioId":1,"dataReserva":"2030-01-01T10:00:00"}'
```
Observacoes:
- A duracao da reserva e fixa em 2 horas.
- Se houver conflito de horario na mesma sala, o servico retorna HTTP 409.
- O `DELETE /api/reservas/{id}` faz cancelamento (nao apaga o registro).

## Parar tudo
```bash
docker compose down
```
Para encerrar os servicos, use `CTRL+C` em cada terminal.
# -gerenciamento-reservas-microservicos
# gerenciamento-reservas-salas-microservicos
# gerenciamento-reservas-salas-microservicos
# gerenciamento-reservas-salas-microservicos
