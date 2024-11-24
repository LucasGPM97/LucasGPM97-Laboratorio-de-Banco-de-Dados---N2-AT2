create database estoque;
use estoque;

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT
);

CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    quantidade_estoque INT NOT NULL,
    preco_compra DECIMAL(10, 2) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    categoria_id INT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);
-- tabela para gerar o relatorio de movimentacao
CREATE TABLE movimentacao_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT,
    produto_nome varchar(100) not null,
    operacao VARCHAR(20),  -- 'entrada' ou 'saida'
    quantidade INT,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);
-- tabela para gerar o relatorio de vendas
CREATE TABLE relatorio_vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT,
    produto_nome VARCHAR(100) not null,
    preco_venda DECIMAL(10,2),
    quantidade INT,
    total_venda DECIMAL(10, 2),
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

DELIMITER $$
-- procedurer utilizada para gerar o Relatorio de Produtos Cadastrados
CREATE PROCEDURE carregar_produto()
BEGIN
	SELECT 
		p.id,
		p.nome,
		p.descricao,
		c.nome AS categoria,
		p.quantidade_estoque,
		p.preco_compra,
		p.preco_venda
	FROM
		produtos p
			LEFT JOIN
		categorias c ON p.categoria_id = c.id;
END $$

DELIMITER ;
-- Procedure utilizada para gerar o relatorio de Produtos com baixo estoque
DELIMITER $$

CREATE PROCEDURE baixo_estoque()
BEGIN
	SELECT 
		p.id,
		p.nome,
		p.descricao,
		c.nome AS categoria,
		p.quantidade_estoque,
		p.preco_compra,
		p.preco_venda
	FROM
		produtos p
			LEFT JOIN
		categorias c ON p.categoria_id = c.id
    WHERE
        p.quantidade_estoque <= 20;
END $$

DELIMITER ;
-- Procedure utilizada para cadastrar produtos
DELIMITER $$

CREATE PROCEDURE cadastrar_produto(
    IN p_nome VARCHAR(100),
    IN p_descricao TEXT,
    IN p_qtde INT,
    IN p_preco_compra DECIMAL(10,2),
    IN p_preco_venda DECIMAL(10,2),
    IN p_categoria_id INT
)
BEGIN
    INSERT INTO produtos (nome, descricao, quantidade_estoque, preco_compra, preco_venda, categoria_id)
    VALUES (p_nome, p_descricao, p_qtde, p_preco_compra, p_preco_venda, p_categoria_id);
END $$

DELIMITER ;
 -- Procedure utilizada para excluir produtos
DELIMITER $$
CREATE PROCEDURE excluir_produto(
    IN p_id int
)
BEGIN
		Delete FROM produtos where id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para alterar produtos
DELIMITER $$

CREATE PROCEDURE alterar_produto(
    IN p_id int,
    IN p_nome VARCHAR(100),
    IN p_descricao VARCHAR(100),
    IN p_quantidade int,
    IN p_preco_compra DECIMAL(10,2),
    IN p_preco_venda DECIMAL(10,2),
    IN p_categoria_id INT
)
BEGIN
	UPDATE produtos 
	SET 
		nome = p_nome,
		descricao = p_descricao,
		quantidade_estoque = p_quantidade,
		preco_compra = p_preco_compra,
		preco_venda = p_preco_venda,
		categoria_id = p_categoria_id
	WHERE
		id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para aumentar o estoque de produtos
DELIMITER $$

CREATE PROCEDURE entrada_produto(
    IN p_id int,
    IN p_quantidade int
)
BEGIN
	UPDATE produtos 
	SET 
		quantidade_estoque = quantidade_estoque +  p_quantidade
	WHERE
		id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para diminuir o estoque de produtos
DELIMITER $$

CREATE PROCEDURE saida_produto(
    IN p_id int,
    IN p_quantidade int
)
BEGIN
	UPDATE produtos 
	SET 
		quantidade_estoque = quantidade_estoque - p_quantidade
	WHERE
		id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para excluir categorias
DELIMITER $$

CREATE PROCEDURE deletar_categoria(
    IN p_id INT
)
BEGIN
    DELETE FROM categorias WHERE id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para cadastrar categorias
DELIMITER $$

CREATE PROCEDURE cadastrar_categoria(
    IN p_nome VARCHAR(100),
    IN p_descricao TEXT
)
BEGIN
    INSERT INTO categorias (nome, descricao) 
    VALUES (p_nome, p_descricao);
END $$

DELIMITER ;
 -- Procedure utilizada para filtrar categorias
DELIMITER $$

CREATE PROCEDURE consultar_categoria(
    IN p_nome VARCHAR(100)
)
BEGIN
	SELECT 
    id, nome, descricao
FROM
    categorias
WHERE
    nome LIKE CONCAT('%', p_nome, '%');

END $$

DELIMITER ;
 -- Procedure utilizada para atualizar categorias
DELIMITER $$

CREATE PROCEDURE update_categoria(
    IN p_id int,
    IN p_nome VARCHAR(100),
    IN p_descricao TEXT
)
BEGIN
	UPDATE categorias 
	SET 
		nome = p_nome,
		descricao = p_descricao
	WHERE
		id = p_id;
END $$

DELIMITER ;
 -- Procedure utilizada para inserir dados das movimentacoes de entrada e saida de produtos
DELIMITER $$

CREATE PROCEDURE registrar_log_movimentacao(
    IN p_id INT,
    IN p_nome_produto VARCHAR(255),
    IN p_operacao VARCHAR(10),
    IN p_quantidade INT
)
BEGIN
    INSERT INTO movimentacao_estoque (produto_id, produto_nome, operacao, quantidade)
    VALUES (p_id, p_nome_produto, p_operacao, p_quantidade);
END $$

DELIMITER ;
 -- Trigger utilizada para chamar procedure que adiciona dados das movimentacoes de entrada e saida de produtos
DELIMITER $$

CREATE TRIGGER movimentacao_estoque_trigger 
AFTER UPDATE ON produtos 
FOR EACH ROW 
BEGIN

    DECLARE operacao VARCHAR(10); -- entrada ou saida
    DECLARE quantidade INT; 
    DECLARE nome_produto VARCHAR(100);

    -- Verifica se a quantidade de estoque foi alterada
    IF OLD.quantidade_estoque <> NEW.quantidade_estoque THEN
        -- Atribui o nome do produto à variável
        SET nome_produto = NEW.nome;  -- Assumindo que o nome da coluna é 'nome', ajuste se necessário

        -- Verifica se houve aumento ou diminuição na quantidade
        IF NEW.quantidade_estoque > OLD.quantidade_estoque THEN
            SET operacao = 'Entrada';
            SET quantidade = NEW.quantidade_estoque - OLD.quantidade_estoque;
        ELSE
            SET operacao = 'Saida';
            SET quantidade = OLD.quantidade_estoque - NEW.quantidade_estoque;
        END IF;

        -- Chama a procedure para registrar a movimentação no log
        CALL registrar_log_movimentacao(NEW.id, nome_produto, operacao, quantidade);
    END IF;
END $$

DELIMITER ;
-- procedure utilizada no filtro de produtos, permite filtrar por nome, categoria, quantidade minima e maxima
DELIMITER $$

CREATE PROCEDURE ConsultarProdutos(
    IN p_nome VARCHAR(255),
    IN p_categoria VARCHAR(255),
    IN p_quantidade_min INT,
    IN p_quantidade_max INT
)
BEGIN
    -- Seleciona os produtos com base nos parâmetros fornecidos
	SELECT 
		*
	FROM
		produtos p
			LEFT JOIN
		categorias c ON p.categoria_id = c.id
	WHERE
		(p_nome IS NULL
			OR p.nome LIKE CONCAT('%', p_nome, '%'))
			AND (p_categoria IS NULL
			OR c.nome LIKE CONCAT('%', p_categoria, '%'))
			AND (p_quantidade_min IS NULL
			OR p.quantidade_estoque <= p_quantidade_min)
            AND (p_quantidade_max IS NULL 
            OR p.quantidade_estoque >= p_quantidade_max)
	ORDER BY p.id;
END $$

DELIMITER ;
-- Procedure utilizada para inserir os dados no log de vendas
DELIMITER $$

CREATE PROCEDURE relatorio_vendas(
    IN p_produto_id int,
    IN p_produto_nome VARCHAR(100),
    IN p_preco_venda DECIMAL(10,2),
    IN p_quantidade INT,
    IN p_total_venda DECIMAL(10,2)
)
BEGIN
    INSERT INTO relatorio_vendas (produto_id, produto_nome, preco_venda, quantidade, total_venda)
    VALUES (p_produto_id, p_produto_nome, p_preco_venda, p_quantidade, p_total_venda);
END $$

DELIMITER ;
-- Trigger que chama a procedure relatorio_vendas.
DELIMITER $$

CREATE TRIGGER relatorio_vendas_trigger 
AFTER UPDATE ON produtos 
FOR EACH ROW 
BEGIN

    DECLARE valor_total DECIMAL(10,2);
    DECLARE quantidade INT;

    -- Verifica se a quantidade de estoque foi alterada
    IF OLD.quantidade_estoque <> NEW.quantidade_estoque THEN

        -- Verifica se houve diminuição na quantidade
        IF NEW.quantidade_estoque < OLD.quantidade_estoque THEN
            SET valor_total = (old.quantidade_estoque - new.quantidade_estoque) * NEW.preco_venda;
            SET quantidade = old.quantidade_estoque - new.quantidade_estoque;
            -- Chama a procedure para registrar a movimentação no log
            CALL relatorio_vendas(NEW.id, NEW.nome, new.preco_venda, quantidade, valor_total);
        END IF;
        
    END IF;
END $$

DELIMITER ;
-- Trigger que impede que o estoque fique abaixo de 0
DELIMITER $$

CREATE TRIGGER verificar_estoque_baixo
AFTER UPDATE ON produtos
FOR EACH ROW
BEGIN
    DECLARE mensagem VARCHAR(255);
    
        -- Verifica se a quantidade de estoque foi alterada
    IF OLD.quantidade_estoque <> NEW.quantidade_estoque THEN

        -- Verifica se houve diminuição na quantidade
        IF NEW.quantidade_estoque < OLD.quantidade_estoque THEN
			IF NEW.quantidade_estoque < 0 THEN
				SET mensagem = CONCAT('Aviso: Estoque baixo para o produto ', NEW.nome);
				-- Lança um erro com código SQLSTATE '45000' para estoque abaixo do limite
				SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = mensagem;
			END IF;
        END IF;
        
    END IF;
    
END $$

DELIMITER ;

select * from categorias;
select * from produtos;
select * from relatorio_vendas;
select * from movimentacao_estoque;

-- drop database estoque;