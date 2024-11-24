package utils;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RelatorioVendasLucro {
    private Connection conn;

    public RelatorioVendasLucro(){
        this.conn = DatabaseConnection.getConnection();
    }

    public void generateRelatorio(){
        try {
            // Abrindo o JFileChooser para o usuário escolher o local e o nome do arquivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Escolha onde salvar o relatório");
            fileChooser.setSelectedFile(new File("relatorio_vendas_lucro.pdf"));  // Nome inicial sugerido

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
                document.add(new Paragraph("Relatório de Vendas e Lucro")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(18)
                        .setMarginBottom(20));

                // Criando a tabela para o relatório
                float[] columnWidths = {1, 1, 3, 2, 2, 2,3};
                Table table = new Table(columnWidths);

                // Centralizando a tabela
                table.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // Cabeçalho da tabela
                addHeaderCell(table, "Data");
                addHeaderCell(table, "ID");
                addHeaderCell(table, "Produto ID");
                addHeaderCell(table, "Produto");
                addHeaderCell(table, "Preco");
                addHeaderCell(table, "Quantidade");
                addHeaderCell(table, "Total");

                // Definindo o formato da data
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                String query = "SELECT * FROM relatorio_vendas";

                double totalVendas = 0;  // Variável para acumular o total das vendas

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    // Preenchendo a tabela com dados do ResultSet
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int produtoId = rs.getInt("produto_id");
                        String produtoNome = rs.getString("produto_nome");
                        double preco = rs.getDouble("preco_venda");
                        int quantidade = rs.getInt("quantidade");
                        double total = rs.getDouble("total_venda");
                        String data = rs.getString("data");

                        totalVendas += total;

                        // Se a data não for null ou vazia, formatá-la
                        String formattedDate = "";
                        if (data != null && !data.isEmpty()) {
                            try {
                                // Se a data estiver no formato "yyyy-MM-dd HH:mm:ss" ou similar, podemos convertê-la
                                Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data);
                                formattedDate = dateFormat.format(parsedDate);  // Formatando no formato desejado
                            } catch (Exception e) {
                                formattedDate = data;  // Caso não consiga formatar, mantemos o valor original
                            }
                        }

                        // Adicionando as células de dados na tabela
                        addDataCell(table, formattedDate);
                        addDataCell(table, String.valueOf(id));
                        addDataCell(table, String.valueOf(produtoId));
                        addDataCell(table, produtoNome);
                        addDataCell(table, String.valueOf(preco));
                        addDataCell(table, String.valueOf(quantidade));
                        addDataCell(table, String.valueOf(total));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Adicionando a linha com o total de vendas
                table.addCell(new Cell(1, 6)  // Esta célula ocupará 6 colunas
                        .add(new Paragraph("Vendas Totais"))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setBold()
                        .setPadding(10));

                // Adicionando o valor do total de vendas na última célula
                table.addCell(new Cell(1, 1)  // Ocupa a última coluna
                        .add(new Paragraph(String.format("R$ %.2f", totalVendas)))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setPadding(10));

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
