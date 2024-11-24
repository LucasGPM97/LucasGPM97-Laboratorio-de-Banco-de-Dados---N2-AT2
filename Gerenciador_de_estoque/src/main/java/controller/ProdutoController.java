package controller;

import dao.ProdutoDAO;
import model.Categoria;
import model.Produto;
import utils.Validadores;

import java.util.List;

// realiza a comunicacao entre a view e os metodos de validacao e de conexao com o banco de dados
public class ProdutoController {
    private ProdutoDAO dao;

    public ProdutoController(){
        this.dao = new ProdutoDAO();
    }

    public boolean salvar(Produto produto){
        return produto.getId() == 0 ? dao.inserir(produto) : dao.atualizar(produto);
    }

    public boolean deletar(int id){
        return dao.deletar(id);
    }

    public List<Produto> listar(){
        return dao.listar();
    }

    public List<Produto> filtrarProduto(String nome, Categoria categoria, Integer quantidadeMin, Integer quantidadeMax) {
        return dao.consultarProdutos(nome, categoria != null ? categoria.getNome() : null, quantidadeMin, quantidadeMax);
    }

    public boolean realizarOperacaoProduto(Produto produto, String operacao){
        return operacao.equals("Saida") ? dao.saida(produto) : dao.entrada(produto);
    }

    public boolean validarProduto(String nome, String descricao, String quantidade, String precoVendaStr, String precoCompraStr, String categoriaSelecionada) {
        return new Validadores().validarProduto(nome,descricao,quantidade,precoVendaStr,precoCompraStr,categoriaSelecionada);
    }

    public boolean validarOperacao(String quantidadeStr, int quantidadeEmEstoque, String operacao) {
        return new Validadores().validarOperacao(quantidadeStr, quantidadeEmEstoque, operacao);
    }
}
