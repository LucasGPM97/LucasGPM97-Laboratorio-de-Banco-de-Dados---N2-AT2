package utils;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/estoque";  // URL do banco de dados
    private static final String USER = "";  // Usuário do banco de dados - inserir
    private static final String PASSWORD = "";  // Senha do banco de dados - inserir
    private static Connection conexao;

    private DatabaseConnection(){

    }

    // Método para conectar ao banco de dados
    public static Connection getConnection() {
        if (conexao == null) {
            try {
                conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Mensagem de erro: " + e.getMessage());
                throw new RuntimeException("Erro ao conectar ao banco de dados", e);
            }
        }
        return conexao;
    }

    // Método para fechar a conexão
    public static void closeConnection() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}