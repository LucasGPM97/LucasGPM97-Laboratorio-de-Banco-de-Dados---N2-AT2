package dao;

import model.Categoria;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private Connection conn;

    // inicia a conexao com o DB
    public CategoriaDAO(){
        this.conn =  DatabaseConnection.getConnection();
    }

    public boolean inserir(Categoria categoria){
        String query = "CALL cadastrar_categoria(?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean atualizar(Categoria categoria){
        String query = "CALL update_categoria(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoria.getId());
            stmt.setString(2, categoria.getNome());
            stmt.setString(3, categoria.getDescricao());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletar(int id) {
        String query = "CALL deletar_categoria(?)";
        String updateProdutosQuery = "UPDATE produtos SET categoria_id = NULL WHERE categoria_id = ?";

        // Inicia uma transação
        try {
            conn.setAutoCommit(false);  // Desativa o auto-commit para um controle manual da transação

            // Atualiza os produtos primeiro
            try (PreparedStatement stmtProdutos = conn.prepareStatement(updateProdutosQuery)) {
                stmtProdutos.setInt(1, id);
                stmtProdutos.executeUpdate();
            }

            // Chama o procedimento armazenado para deletar a categoria
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Se ambas as operações forem bem-sucedidas, faz o commit
            conn.commit();
            return true;
        } catch (SQLException e) {
            // Se ocorrer um erro, faz o rollback
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Restaura o comportamento de auto-commit para o valor original
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Categoria> listar(){
        List<Categoria> categorias = new ArrayList<>();
        String query = "Select * FROM categorias";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
           while (rs.next()){
               Categoria categoria = new Categoria(rs.getInt("id"),
                       rs.getString("nome"),
                       rs.getString("descricao"));
               categorias.add(categoria);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    public List<Categoria> listarPorNome(String nomeCategoria) {
        List<Categoria> categorias = new ArrayList<>();
        String query = "SELECT * FROM categorias WHERE nome LIKE ?";

        String nomeBusca = "%" + nomeCategoria + "%";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria(rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                    categorias.add(categoria);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }

    public Categoria buscarCategoriaPorId(int categoriaId) {
        Categoria categoria = null;
        String query = "SELECT * FROM categorias WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoriaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nomeCategoria = rs.getString("nome");
                    String descricaoCategoria = rs.getString("descricao");
                    categoria = new Categoria(categoriaId,
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoria;
    }

}
