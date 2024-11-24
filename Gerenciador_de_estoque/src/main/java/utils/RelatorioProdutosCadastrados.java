package utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.colors.DeviceRgb;

import javax.swing.*;
import java.sql.*;
import java.io.File;

public class RelatorioProdutosCadastrados {
    private Connection conn;

    public RelatorioProdutosCadastrados(){
        this.conn = DatabaseConnection.getConnection();
    }

    public void generateRelatorio() {

        try {
            // Abrindo o JFileChooser para o usuário escolher o local e o nome do arquivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Escolha onde salvar o relatório");
            fileChooser.setSelectedFile(new File("relatorio_produtos_cadastrados.pdf"));  // Nome inicial sugerido

            // Mostrar o diálogo de salvar arquivo
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtendo o caminho do arquivo escolhido
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Se o usuário não incluir a extensão .pdf, adicionar automaticamente
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Criando o writer e o documento PDF
                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Definindo margens e título
                document.setMargins(20, 20, 20, 20);
                document.add(new Paragraph("Relatório de Produtos Cadastrados")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(18)
                        .setMarginBottom(20));

                // Criando a tabela para o relatório
                float[] columnWidths = {1, 2, 3, 2, 2, 2, 2};
                Table table = new Table(columnWidths);

                // Centralizando a tabela
                table.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // Cabeçalho da tabela
                addHeaderCell(table, "ID");
                addHeaderCell(table, "Nome");
                addHeaderCell(table, "Descricao");
                addHeaderCell(table, "Categoria");
                addHeaderCell(table, "Quantidade");
                addHeaderCell(table, "Preco Compra");
                addHeaderCell(table, "Preco Venda");

                String query = "CALL carregar_produto()";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    // Preenchendo a tabela com dados do ResultSet
                    while (rs.next()) {
                        int idProduto = rs.getInt("id");
                        String nomeProduto = rs.getString("nome");
                        String descricaoProduto = rs.getString("descricao");
                        String categoria = rs.getString("categoria");
                        int quantidade = rs.getInt("quantidade_estoque");
                        double precoCompra = rs.getDouble("preco_compra");
                        double precoVenda = rs.getDouble("preco_venda");

                        // Adicionando as células de dados na tabela
                        addDataCell(table, String.valueOf(idProduto));
                        addDataCell(table, nomeProduto);
                        addDataCell(table, descricaoProduto);
                        addDataCell(table, (categoria != null) ? categoria : "null");
                        addDataCell(table, String.valueOf(quantidade));
                        addDataCell(table, String.valueOf(precoCompra));
                        addDataCell(table, String.valueOf(precoVenda));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Adicionando a tabela ao documento PDF
                document.add(table);

                // Finalizando o documento PDF
                document.close();

                System.out.println("Relatório gerado com sucesso em: " + filePath);
            } else {
                System.out.println("O usuário cancelou o processo de salvamento.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Função para adicionar célula de cabeçalho
    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setBold()
                .setBackgroundColor(new DeviceRgb(0, 102, 204))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5);
        table.addCell(cell);
    }

    // Função para adicionar célula de dados
    private void addDataCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5);
        table.addCell(cell);
    }

}
