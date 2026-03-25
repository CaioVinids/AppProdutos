# AppProdutos API - Projeto Java & Oracle

Este projeto foi desenvolvido como parte do programa de Jovens Profissionais, focando em uma solução de back-end para gerenciamento de produtos, categorias, controle de estoque e fluxo de vendas via carrinho de compras.

---

# Pilares do Projeto

* Gestão de Estoque: Validação de disponibilidade tanto na adição de itens quanto na finalização da compra.

* Price Snapshot: O preço do produto é registrado no momento da inserção no carrinho, evitando alterações retroativas em vendas finalizadas.

* Trilha de Auditoria: Implementação de EstoqueTransaction para registrar cada entrada e saída de produtos.

* Validação em Tempo Real: O sistema impede a adição de itens ao carrinho e a finalização da compra se não houver saldo em estoque.

* Trava de "Carrinho Fantasma": Impedimento de finalização de compras sem itens.

* Cálculo Automático: Recálculo em tempo real de totais ao adicionar, atualizar quantidades ou remover itens.

* Documentação: Uso de Swagger/OpenAPI para facilitar testes e integração.

---

# Tecnologias Utilizadas

* Java 21 com Spring Boot 3

* Banco de Dados Oracle (Dbeaver)

* Maven para gerenciamento de dependências.

* Postman: Plataforma de API utilizada para o desenvolvimento, teste e documentação dos endpoints do sistema.

* Spring Data JPA para persistência e Lombok para código.

* Swagger/OpenAPI para documentação e testes de contrato da API.

---

# Funcionalidades
* Fluxo do Carrinho: Criar, visualizar, adicionar itens, atualizar quantidades e deletar itens.

* Finalização de Compra: Transição de status de ATIVO para FINALIZADO com registro de data/hora.

* Histórico de Compras: Consulta de pedidos anteriores ordenados do mais recente para o mais antigo.

---

# Configuração e Segurança

* O projeto utiliza um arquivo application.yml integrado a variáveis de ambiente para garantir que credenciais sensíveis não sejam expostas no versionamento.
* Arquivo `.env`: Utilizado para armazenar localmente suas chaves de acesso (este arquivo está no .gitignore e não é enviado ao GitHub).
* Arquivo `.env.example`: Fornecido no repositório como um modelo das variáveis necessárias.

---

# Como Rodar e Testar

1. **Clone o repositório**
2. **Configure seu ambiente Oracle no application.yml:**

```bash
${DB_URL}: URL de conexão do Oracle.

${DB_USER}: Seu usuário do banco.

${DB_PWD}: Sua senha do banco.
```

3. **Execução**
* No terminal, dentro da pasta raiz do projeto, execute:

```bash
mvn spring-boot:run
```

4. **Homologação via Swagger (Interface Visual)**
* Para facilitar os testes e o entendimento das rotas, utilize a documentação interativa do Swagger assim que a aplicação estiver rodando:
  * http://localhost:8080/swagger-ui/index.html

---