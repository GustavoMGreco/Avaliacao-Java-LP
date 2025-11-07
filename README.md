# Sistema de Cadastro - Mães Que Oram pelos Filhos

Projeto de sistema desktop em Java para a 3ª Avaliação de Linguagem de Programação (LP) da FATEC São José dos Campos.
O objetivo é auxiliar na organização dos encontros das "Mães Que Oram pelos Filhos", permitindo o cadastro de mães, o gerenciamento de encontros e serviços, e a emissão de relatórios.

Este projeto foi desenvolvido como parte da 3ª Avaliação de LP, ministrada pela Profª. Juliana Pasquini.

---

## 🎯 Funcionalidades

O sistema implementa as seguintes funcionalidades principais:

* **Cadastro de Mães:** Gerenciamento completo (CRUD) das mães participantes, incluindo nome, telefone, endereço e data de aniversário.
* **Cadastro de Encontros:** Registro de novos encontros, associando uma mãe responsável e uma descrição de atividade para cada um dos serviços pré-definidos.
* **Serviços Fixos:** O sistema já inclui uma lista de serviços fixos para cada encontro (ex: MÚSICA, ACOLHIDA, TERÇO, etc.).
* **Edição e Exclusão:** Permite editar ou excluir encontros que ainda não ocorreram. Para encontros passados, é aplicada uma exclusão lógica (status "cancelado" ou "não realizado").
* **Lista de Aniversariantes:** Exibe uma consulta de todas as mães que fazem aniversário no mês atual.
* **Geração de Relatório:** Exporta um resumo de um encontro específico (selecionado por data) em formato `.txt`.

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java
* **Interface:** Java Desktop (Swing/JavaFX) 
* **Banco de Dados:** MySQL 
* **Conexão:** JDBC
* **Princípios:** Programação Orientada a Objetos (POO)

## 📁 Estrutura de Pacotes

O projeto segue a estrutura de pacotes solicitada:

* `factory`: Contém a `ConnectionFactory` para gerenciar a conexão JDBC.
* `modelo`: Classes de modelo (POJOs) que representam as entidades (Mae, Encontro, Servico, Responsabilidade).
* `dao`: Classes DAO (Data Access Object) responsáveis pela persistência dos dados no MySQL (CRUD).
* `gui`: Telas da interface gráfica (JFrames, JPanels).

## 🚀 Como Executar

1.  **Banco de Dados:**
    * Certifique-se de ter o MySQL Server instalado e em execução.
    * Execute o script **`banco_dados.sql`** para criar o banco `avaliacao_java` e suas tabelas (`mae`, `servico`, `encontro`, `responsabilidade`).
    * **Importante:** Popule a tabela `servico` com os serviços fixos listados no PDF para que o sistema funcione corretamente.

2.  **Configuração da Conexão:**
    * Abra o arquivo `src/factory/ConnectionFactory.java`.
    * Altere as variáveis `URL`, `USER` e `PASSWORD` para corresponder à sua configuração local do MySQL.

3.  **Execução:**
    * Importe o projeto em sua IDE Java (Eclipse, IntelliJ, NetBeans).
    * Localize a classe principal (provavelmente em `src/gui/`) e execute-a.
    * Opcionalmente, pode ser gerado um arquivo `.jar` executável.

## ✅ Entregáveis

O repositório contém todos os itens solicitados para a avaliação:

* [X] Código-fonte completo do sistema (organizado em pacotes)
* [X] Script SQL (`banco_dados.sql`)
* [X] Diagrama de Classes UML
* [X] Diagrama Conceitual do BD
* [X] Diagrama Lógico do BD
* [ ] Exemplo de Relatório `.txt` gerado pelo sistema
* [ ] Arquivo `.jar` executável (Opcional)

---

**Autores:**

* Gustavo Monteiro Greco
* Rafael Giordano Matesco

**Professora:**

* Juliana Pasquini