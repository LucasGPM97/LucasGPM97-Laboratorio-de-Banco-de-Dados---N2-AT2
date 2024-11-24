package dao;

import controller.CategoriaController;
import model.Categoria;
import model.Produto;
import utils.DatabaseConnection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private Connection conn;

    // inicia conexao com o DB
    public ProdutoDAO(){
        this.conn =  DatabaseConnection.getConnection();
    }

    public boolean inserir(Produto produto){
        String query = "CALL cadastrar_produto(?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setDouble(4, produto.getPrecoCompra());
            stmt.setDouble(5, produto.getPrecoVenda());
            stmt.setInt(6, produto.getCategoria().getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean atualizar(Produto produto){
        String query = "CALL alterar_produto(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, produto.getId());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getDescricao());
            stmt.setInt(4, produto.getQuantidade());
            stmt.setDouble(5, produto.getPrecoCompra());
            stmt.setDouble(6, produto.getPrecoVenda());
            stmt.setInt(7, produto.getCategoria().getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletar(int id) {
        String query = "CALL excluir_produto(?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Produto> listar(){
        List<Produto> produtos = new ArrayList<>();
        String query = "Select * from produtos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()){
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                int quantidade = rs.getInt("quantidade_estoque");
                double precoCompra = rs.getDouble("preco_compra");
                double precoVenda = rs.getDouble("preco_venda");
                int categoriaId = rs.getInt("categoria_id");

                Categoria categoria = new CategoriaController().buscarCategoriaPorId(categoriaId);
                Produto produto = new Produto(id,nome,descricao,quantidade,precoCompra,precoVenda,categoria);
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    public List<Produto> consultarProdutos(String nome1, String categoria1, Integer quantidadeMin, Integer quantidadeMax) {
        List<Produto> produtos = new ArrayList<>();
        String query = "CALL ConsultarProdutos(?, ?, ?, ?)";

        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, nome1);
            stmt.setString(2, categoria1);
            stmt.setObject(3, quantidadeMin, Types.INTEGER);
            stmt.setObject(4, quantidadeMax, Types.INTEGER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    int quantidade = rs.getInt("quantidade_estoque");
                    double precoCompra = rs.getDouble("preco_compra");
                    double precoVenda = rs.getDouble("preco_venda");
                    int categoriaId = rs.getInt("categoria_id");

                    Categoria categoria = new CategoriaController().buscarCategoriaPorId(categoriaId);
                    Produto produto = new Produto(id,nome,descricao,quantidade,precoCompra,precoVenda,categoria);
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produtos;
    }

    public boolean saida(Produto produto) {
        String query = "CALL saida_produto(?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, produto.getQuantidade());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Exibir a mensagem de erro SQL
            JOptionPane.showMessageDialog(null, "Erro ao realizar a operação: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean entrada(Produto produto) {
        String query = "CALL entrada_produto(?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, produto.getQuantidade());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
