package view;

import controller.CategoriaController;
import controller.ProdutoController;
import utils.RelatorioBaixoEstoque;
import utils.RelatorioMovimentacao;
import utils.RelatorioProdutosCadastrados;
import utils.RelatorioVendasLucro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaInicialView extends JFrame {
    private JComboBox<String> cmbRelatorios;

    public TelaInicialView() {
        setTitle("Menu Principal - Gerenciador de Estoque");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnProdutos = new JButton("Gerenciar Produtos");
        btnProdutos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProdutoController controller = new ProdutoController();
                ProdutoView view = new ProdutoView(controller);
                view.setVisible(true);
            }
        });

        JButton btnCategorias = new JButton("Gerenciar Categorias");
        btnCategorias.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CategoriaController controller = new CategoriaController();
                CategoriaView view = new CategoriaView(controller);
                view.setVisible(true);
            }
        });

        JButton btnMovimentacao = new JButton("Movimentar Estoque");
        btnMovimentacao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProdutoController controller = new ProdutoController();
                MovimentacaoView view = new MovimentacaoView(controller);
                view.setVisible(true);
            }
        });


        // Criar um JComboBox com as opções de relatórios
        String[] relatorios = {
                "Relatorio de Produtos Cadastrados",
                "Relatorio de Movimentação de Estoque",
                "Relatorio de Produtos com Baixo Estoque",
                "Relatorio de Vendas e Lucro"
        };
        cmbRelatorios = new JComboBox<>(relatorios);

        JButton btnRelatorios = new JButton("Gerar Relatórios");
        btnRelatorios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Exibir o JComboBox para o usuário selecionar
                int option = JOptionPane.showConfirmDialog(
                        TelaInicialView.this,
                        cmbRelatorios,
                        "Selecione o Tipo de Relatório",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );


                if (option == JOptionPane.OK_OPTION) {
                    String selectedReport = (String) cmbRelatorios.getSelectedItem();

                    if (selectedReport != null) {
                        switch (selectedReport) {
                            case "Relatorio de Produtos Cadastrados":
                                new RelatorioProdutosCadastrados().generateRelatorio();
                                break;
                            case "Relatorio de Movimentação de Estoque":
                                new RelatorioMovimentacao().generateRelatorio();
                                break;
                            case "Relatorio de Produtos com Baixo Estoque":
                                 new RelatorioBaixoEstoque().generateRelatorio();
                                break;
                            case "Relatorio de Vendas e Lucro":
                                 new RelatorioVendasLucro().generateRelatorio();
                                break;
                            default:
                                JOptionPane.showMessageDialog(TelaInicialView.this, "Opção inválida!");
                        }
                    }
                }
            }
        });


        panel.add(btnProdutos);
        panel.add(btnCategorias);
        panel.add(btnMovimentacao);
        panel.add(btnRelatorios);

        add(panel);
    }
}
