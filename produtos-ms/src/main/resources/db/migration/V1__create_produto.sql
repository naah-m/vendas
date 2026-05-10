CREATE TABLE produto (
    id         BINARY(16)    NOT NULL,
    nome       VARCHAR(255)  NOT NULL,
    descricao  TEXT,
    preco      DECIMAL(10,2) NOT NULL,
    categoria  VARCHAR(255),
    PRIMARY KEY (id)
);
