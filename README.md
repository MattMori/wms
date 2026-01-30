# WMS Mori 🏗️

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

**WMS Mori** é um Sistema de Gerenciamento de Armazém (Warehouse Management System) focado em controle volumétrico, auditoria e eficiência operacional. O sistema gerencia o ciclo de vida dos produtos dentro do estoque, desde o recebimento até a expedição, com cálculo automático de ocupação de racks.

---

## Funcionalidades

###  Segurança & Acesso
- **Autenticação JWT:** Login seguro com tokens JSON Web Token.
- **Criptografia:** Senhas salvas com BCrypt.
- **Controle de Sessão:** Acesso protegido a endpoints sensíveis.

### Gestão de Estoque
- **Entrada (Inbound):** Recebimento de mercadorias via bipagem.
- **Saída (Outbound):** Baixa de estoque com validação de saldo.
- **Transferência Interna:** Movimentação entre endereços (Racks) sem perder rastreabilidade.
- **Conferência Cega:** Auditoria de inventário com detecção automática de divergências.

### Inteligência Logística
- **Dashboard Executivo:** KPIs de ocupação, capacidade total vs. real e curvas de estoque.
- **Lógica Volumétrica:** O sistema entende "Caixas" vs "Unidades" (ex: 1 caixa = 12 unidades) para calcular a lotação real do armazém.
- **Auditoria:** Histórico completo de quem moveu o que e quando.

---

## Tecnologias Utilizadas

- **Back-end:** Java 21, Spring Boot, Spring Security.
- **Banco de Dados:** PostgreSQL (Produção no Neon.tech / Local via Docker).
- **ORM:** Hibernate / Spring Data JPA.
- **Front-end:** NÃO DEFINIDO
- **Deploy:** Docker, Render (App) e Neon (DB).

---

## Como Rodar Localmente

### Pré-requisitos
- Java 21 JDK instalado.
- Maven instalado.
- PostgreSQL rodando (ou acesso a um banco na nuvem).

### 1. Clone o repositório
```bash
git clone [https://github.com/SEU-USUARIO/wms-mori.git](https://github.com/SEU-USUARIO/wms-mori.git)
cd wms-mori
```

### 2. Configure o Banco de Dados

O projeto utiliza variáveis de ambiente para conectar ao banco. No seu arquivo application.properties, ele busca por DATABASE_URL ou usa o localhost como fallback.

Se estiver rodando local, certifique-se de ter um banco criado:
SQL
```bash
CREATE DATABASE wms;
```
### 3. Execute o Projeto
Bash
```bash
./mvnw spring-boot:run
```
O servidor iniciará na porta 8080.

### 4. Primeiro Acesso

Como o banco inicia vazio, você precisa criar o primeiro usuário via API ou Swagger:

POST /auth/register
JSON
```bash
{
  "nome": "Admin",
  "matricula": "admin",
  "senha": "123"
}
```
Depois, acesse o frontend em:
```bash
 http://localhost:8080/index.html
```
 Rodando com Docker

Se você tem Docker instalado, não precisa configurar Java ou Maven na sua máquina.
construir a imagem:
```bash
docker build -t wms-mori .
```
Rodar o container:
```bash
docker run -p 8080:8080 -e DATABASE_URL="jdbc:postgresql://host.docker.internal:5432/wms" wms-mori
```

### Documentação da API

O projeto possui Swagger/OpenAPI integrado. Com a aplicação rodando, acesse:
```bash
 http://localhost:8080/swagger-ui.html
```

 Licença
Este projeto foi desenvolvido para fins de estudo e portfólio.
