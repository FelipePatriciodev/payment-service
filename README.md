# Payment Service

Microsserviço de pagamentos desenvolvido em **Java 17 com Spring Boot**, que processa pagamentos via **PIX**, **Cartão de Crédito** e **Cartão de Débito**.  
O serviço também publica eventos no **Kafka** e registra logs ao consumi-los.

---
## Pré-requisitos

Antes de rodar a aplicação, certifique-se de ter instalado:

- **Java 17** (JDK)
- **Maven** (caso não use o wrapper `./mvnw`)
- **Docker** e **Docker Compose**
- (Opcional) **Postman** ou **cURL** para testar os endpoints

## Tecnologias utilizadas
- Java 17
- Spring Boot (Web, Validation, Data JPA)
- MariaDB
- Kafka (Producer/Consumer)
- JUnit 5 + Spring Boot Test
- Docker Compose (para subir Kafka + Zookeeper)

---
## Banco de Dados

A aplicação utiliza **MariaDB** como banco de dados.

1. Certifique-se de que o MariaDB está rodando localmente.

   Por padrão, a aplicação espera acesso em:

   ```
   url: jdbc:mariadb://localhost:3306/payments
   username: root
   password: 
   ```

2. Crie o banco de dados **manualmente** antes de iniciar a aplicação:

   ```sql
   CREATE DATABASE payments;
   ```

---

## Inicialização dos dados
O projeto **não utiliza Flyway nem `data.sql`**.  
Em vez disso, existe a classe `DataInitializer` (`com.desafio.paymentservice.config`) que popula automaticamente 5 produtos no banco de dados ao iniciar a aplicação **caso a tabela esteja vazia**.

Isso garante que sempre haverá produtos disponíveis logo no primeiro start.

---

## Como rodar o projeto

### 1. Subir dependências com Docker Compose (Kafka + Zookeeper)
```bash
docker-compose up -d
```

### 2. Rodar a aplicação localmente
```bash
./mvnw spring-boot:run
```

O serviço sobe por padrão na porta **8080**.

---

## Endpoints principais

### Listar produtos
`GET /products`  
Retorna todos os produtos disponíveis.

---

### Criar pagamento
`POST /payments`  

Corpo esperado:
```json
{
  "paymentMethodType": "PIX",
  "products": [
    { "product_id": "UUID_DO_PRODUTO", "quantity": 1 }
  ]
}
```

Regras:
- **PIX** → aplica 5% de desconto  
- **CREDIT_CARD** → aplica 3% de cashback  
- **DEBIT_CARD** → sem regras extras  

Resposta (exemplo):
```json
{
  "id": "uuid-do-pagamento",
  "status": "PROCESSED",
  "totalAmount": 237.50,
  "createdAt": "2025-09-23T12:00:00"
}
```

---

## Eventos Kafka

Ao criar um pagamento, um evento `PaymentEventDto` é publicado no tópico.  

O consumidor Kafka registra no log algo como:
```
Evento de pagamento consumido do Kafka. Detalhes: { id: 'uuid-do-pagamento', status: 'PROCESSED', ... }
```

---

## Testes automatizados

Rodar com:
```bash
./mvnw test
```

⚠️ **Atenção:** os testes de integração (`PaymentControllerTest`) usam UUIDs específicos de produtos.  
Como os produtos são criados dinamicamente pelo `DataInitializer`, os UUIDs reais podem variar.  

 Caso os testes falhem por `productId` inválido, faça uma chamada ao `GET /products` para obter os UUIDs corretos e substitua nos testes antes de rodar novamente.

---

##  Estrutura do projeto

```
src/main/java/com/desafio/paymentservice
├── config              # Configurações (ex: DataInitializer)
├── domain.model        # Entidades e enums (Payment, Product, PaymentStatus...)
├── domain.service      # Regras de negócio (PaymentService, ProductService)
├── infrastructure.kafka # Producer e Consumer do Kafka
├── infrastructure.repository # Repositórios JPA
├── web.controller      # Controllers REST
├── web.dto             # DTOs (PaymentRequestDto, PaymentResponseDto...)
├── web.exception       # Tratamento global de exceções
```

Testes ficam em `src/test/java`, espelhando a estrutura acima.

---

## Observações finais

- O serviço já está pronto para rodar localmente.  
- Kafka precisa estar em execução via Docker Compose antes de processar pagamentos.  
- Todos os eventos de pagamento são registrados no log para acompanhamento.

---
 Desenvolvido como parte de desafio técnico.
