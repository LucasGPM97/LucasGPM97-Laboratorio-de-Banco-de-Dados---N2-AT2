package controller;

import dao.CategoriaDAO;
import model.Categoria;

import java.util.List;

// realiza a comunicacao entre a view e os metodos de validacao e de conexao com o banco de dados
public class CategoriaController {

    private CategoriaDAO dao;

    public CategoriaController(){
        this.dao = new CategoriaDAO();
    }

    public boolean salvar(Categoria categoria){
        return categoria.getId() == 0 ? dao.inserir(categoria) : dao.atualizar(categoria);
    }

    public boolean deletar(int id){
        return dao.deletar(id);
    }

    public List<Categoria> listar(){
        return dao.listar();
    }

    public List<Categoria> listarPorNome(String nomeCategoria){
        return dao.listarPorNome(nomeCategoria);
    }

    public Categoria buscarCategoriaPorId(int categoriaId){
        return dao.buscarCategoriaPorId(categoriaId);
    }

}
