-- Criação das tabelas do banco de dados
CREATE DATABASE avaliacao_java;
USE avaliacao_java;

-- Tabela Mae
CREATE TABLE mae (
    id_mae INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(200),
    data_nascimento DATE
);

-- Tabela Servico
CREATE TABLE servico (
    id_servico INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL
);

-- Tabela Encontro
CREATE TABLE encontro (
    id_encontro INT PRIMARY KEY AUTO_INCREMENT,
    data_encontro DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Tabela Responsabilidade (relacionamento entre Mae, Servico e Encontro)
CREATE TABLE responsabilidade (
    id_responsabilidade INT PRIMARY KEY AUTO_INCREMENT,
    id_mae INT,
    id_servico INT,
    id_encontro INT,
    descricao_atividade TEXT,
    FOREIGN KEY (id_mae) REFERENCES mae(id_mae),
    FOREIGN KEY (id_servico) REFERENCES servico(id_servico),
    FOREIGN KEY (id_encontro) REFERENCES encontro(id_encontro)
);

-- Adicionando índices para melhor performance
CREATE INDEX idx_mae_nome ON mae(nome);
CREATE INDEX idx_servico_nome ON servico(nome);
CREATE INDEX idx_encontro_data ON encontro(data_encontro);

-- Comentários sobre as tabelas
/*
mae: Armazena informações das mães
servico: Cadastro dos serviços disponíveis
encontro: Registro dos encontros
responsabilidade: Tabela de relacionamento que conecta mães, serviços e encontros
*/