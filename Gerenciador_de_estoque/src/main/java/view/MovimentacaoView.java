package view;

import controller.ProdutoController;
import model.Categoria;
import model.Produto;
import utils.NonEditableTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class MovimentacaoView extends JFrame{

    private JTextField txtNomeProduto, txtQuantidade, txtIdProduto;
    private JComboBox<String> cmbOperacao;
    private JTable tableProdutos;
    private JButton btnRealizarOperacao, btnListar, btnLimpar, btnConsultar;
    private DefaultTableModel tableModel;
    private ProdutoController controller;
    private TableRowSorter<TableModel> sorter;

    public MovimentacaoView(ProdutoController controller){
        this.controller = controller;

        setTitle("Movimentar Estoque");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Painel de formulário para cadastro/edição
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("*ID:"));
        txtIdProduto = new JTextField();
        txtIdProduto.setEditable(false);
        formPanel.add(txtIdProduto);

        // Adicionando os campos de entrada com os labels
        formPanel.add(new JLabel("Nome do Produto:"));
        txtNomeProduto = new JTextField();
        formPanel.add(txtNomeProduto);

        formPanel.add(new JLabel("Operaçâo:"));
        cmbOperacao = new JComboBox<>();
        cmbOperacao.addItem("Entrada");
        cmbOperacao.addItem("Saida");
        formPanel.add(cmbOperacao);

        formPanel.add(new JLabel("Quantidade:"));
        txtQuantidade = new JTextField();
        formPanel.add(txtQuantidade);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));  // 1 linha, 5 colunas

        btnRealizarOperacao = new JButton("Realizar Operação");
        btnRealizarOperacao.addActionListener(e -> realizarOperacaoProduto());
        buttonPanel.add(btnRealizarOperacao);

        btnListar = new JButton("Listar");
        btnListar.addActionListener(e -> listarProduto());
        buttonPanel.add(btnListar);

        // Botão para limpar a seleção
        btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        buttonPanel.add(btnLimpar);

        // Botão para abrir a tela de filtros
        btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultarProduto());
        buttonPanel.add(btnConsultar);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);  // Painel de botões na parte inferior

        // Tabela para listar os produtos cadastrados
        String[] columnNames = {"ID", "Nome", "Descrição", "Categoria", "Quantidade", "Preço Compra", "Preço Venda"};
        tableModel = new NonEditableTableModel(columnNames, 0);
        tableProdutos = new JTable(tableModel);

        // Configura o TableRowSorter para permitir a ordenação pelas colunas
        sorter = new TableRowSorter<>(tableModel);
        tableProdutos.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tableProdutos);

        // Adicionar a tabela à tela
        panel.add(scrollPane, BorderLayout.CENTER);

        // Adiciona o painel principal à janela
        add(panel);

        tableProdutos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Verifica se a seleção foi ajustada
                    int selectedRow = tableProdutos.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRowIndex = tableProdutos.convertRowIndexToModel(selectedRow);
                        preencherCamposComDadosDaLinha(modelRowIndex);
                    }
                }
            }
        });

    }

    private void realizarOperacaoProduto() {
        int selectedRow = tableProdutos.getSelectedRow();
        int quantidadeEmEstoque = (int) tableModel.getValueAt(selectedRow, 4); // usado para verificar se a quantidade de saida nao e maior que a quantidade em estoque

        String quantidadeStr = txtQuantidade.getText(); // quaantidade que tera a operacao
        String operacao = (String) cmbOperacao.getSelectedItem(); // verifica se e entrada ou saida.


        if (selectedRow != -1) {
            int modelRowIndex = tableProdutos.convertRowIndexToModel(selectedRow);
            int idProduto = (int) tableModel.getValueAt(modelRowIndex, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente realizar a operaçâo?",
                    "Confirmar Operaçâo", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (!controller.validarOperacao(quantidadeStr,quantidadeEmEstoque,operacao)){ // valida os campos da operacao antes de realizar a mesma
                    return;
                }
            }

            Produto produto = new Produto(idProduto, Integer.parseInt(quantidadeStr));
            controller.realizarOperacaoProduto(produto, operacao); // realiza a operacao
            listarProduto(); // atualiza a tabela com os novos valores
        }


    }

    private void listarProduto() {
        tableModel.setRowCount(0);
        List<Produto> produtos = controller.listar();
        for (Produto produto : produtos){
            tableModel.addRow(new Object[]{produto.getId(), produto.getNome(),produto.getDescricao(),(produto.getCategoria() != null) ? produto.getCategoria().getNome() : "null",produto.getQuantidade(),produto.getPrecoCompra(),produto.getPrecoVenda()});
        }
    }

    private void limparCampos() {
        tableProdutos.clearSelection();

        txtNomeProduto.setText("");
        txtQuantidade.setText("");
        sorter.setSortKeys(Collections.emptyList());
    }

    // abre a tela de filtro
    private void consultarProduto() {
        ProdutoFiltroView filtroView = new ProdutoFiltroView(this);
        filtroView.setVisible(true);

        String nomeProduto = filtroView.getNomeProduto();
        Categoria categoria = filtroView.getCategoria();

        Integer qtdMax = filtroView.getMax();
        Integer qtdMin = filtroView.getMin();

        List<Produto> produtosFiltrados = controller.filtrarProduto(nomeProduto, categoria, qtdMax, qtdMin);

        atualizarTabela(produtosFiltrados);
    }

    // atualiza a tabela com os valores filtrados
    private void atualizarTabela(List<Produto> produtosFiltrados) {
        tableModel.setRowCount(0);

        for (Produto produto : produtosFiltrados){
            tableModel.addRow(new Object[]{produto.getId(), produto.getNome(),produto.getDescricao(),produto.getCategoria().getNome(),produto.getQuantidade(),produto.getPrecoCompra(),produto.getPrecoVenda()});
        }
    }

    private void preencherCamposComDadosDaLinha(int selectedRow) {
        String nomeProduto = (String) tableModel.getValueAt(selectedRow, 1);
        String idProduto =  tableModel.getValueAt(selectedRow,0).toString();
        // Preenche os campos de texto com os dados
        txtIdProduto.setText(idProduto);
        txtNomeProduto.setText(nomeProduto);
    }

}
