package view;

import controller.CategoriaController;
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

public class ProdutoView extends JFrame {

    private JTextField txtIdProduto, txtNomeProduto, txtDescricaoProduto, txtQuantidadeProduto, txtPrecoCompra, txtPrecoVenda;
    private JComboBox<Categoria> cmbCategoria;
    private JTable tableProdutos;
    private JButton btnSalvar, btnDeletar, btnListar, btnLimpar, btnConsultar;
    private DefaultTableModel tableModel;
    private ProdutoController controller;
    private TableRowSorter<TableModel> sorter;


    public ProdutoView(ProdutoController controller){
        this.controller = controller;

        setTitle("Gerenciar Produtos");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Painel de formulário para cadastro/edição
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2, 10, 10));

        formPanel.add(new JLabel("*ID:"));
        txtIdProduto = new JTextField();
        txtIdProduto.setEditable(false);
        formPanel.add(txtIdProduto);

        // Adicionando os campos de entrada com os labels
        formPanel.add(new JLabel("Nome do Produto:"));
        txtNomeProduto = new JTextField();
        formPanel.add(txtNomeProduto);

        formPanel.add(new JLabel("Descrição:"));
        txtDescricaoProduto = new JTextField();
        formPanel.add(txtDescricaoProduto);

        formPanel.add(new JLabel("Categoria:"));
        cmbCategoria = new JComboBox<>();
        carregarCategoriasParaCombo();  // Carrega as categorias
        formPanel.add(cmbCategoria);

        formPanel.add(new JLabel("Quantidade:"));
        txtQuantidadeProduto = new JTextField();
        formPanel.add(txtQuantidadeProduto);

        formPanel.add(new JLabel("Preço de Compra:"));
        txtPrecoCompra = new JTextField();
        formPanel.add(txtPrecoCompra);

        formPanel.add(new JLabel("Preço de Venda:"));
        txtPrecoVenda = new JTextField();
        formPanel.add(txtPrecoVenda);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5, 10, 10));  // 1 linha, 5 colunas

        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarProduto());
        buttonPanel.add(btnSalvar);

        btnDeletar = new JButton("Excluir");
        btnDeletar.addActionListener(e -> deletarProduto());
        buttonPanel.add(btnDeletar);


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
                        txtQuantidadeProduto.setEditable(false);
                        int modelRowIndex = tableProdutos.convertRowIndexToModel(selectedRow);
                        preencherCamposComDadosDaLinha(modelRowIndex);
                    }else {
                        txtQuantidadeProduto.setEditable(true);
                    }
                }
            }
        });

    }

    private void carregarCategoriasParaCombo() {
        List<Categoria> categorias = new CategoriaController().listar();
        cmbCategoria.removeAllItems();

        for (Categoria categoria : categorias){
            cmbCategoria.addItem(categoria);
        }

    }

    private void salvarProduto() {
        // quando estiver cadastrando um produto ele coloca o id como 0 somente para criar o objeto, se tiver alterando ele pega o id do campo
        int id = txtIdProduto.getText().isEmpty() ? 0 : Integer.parseInt(txtIdProduto.getText());
        String nome = txtNomeProduto.getText();
        String descricao = txtDescricaoProduto.getText();
        String quantidade = txtQuantidadeProduto.getText();
        String precoCompraStr = txtPrecoCompra.getText();
        String precoVendaStr = txtPrecoVenda.getText();
        Categoria categoriaSelecionada = (Categoria) cmbCategoria.getSelectedItem();

        if (categoriaSelecionada == null){
            JOptionPane.showMessageDialog(this, "Categoria Vazia");

            return;
        }

        int categoriaId = categoriaSelecionada.getId();


        // realiza as validacoes dos campos antes de criar o objeto.
        boolean produtoValido = controller.validarProduto(nome, descricao, quantidade, precoVendaStr, precoCompraStr, categoriaSelecionada.toString());
        if (!produtoValido){
            return;
        }

        // cria o objeto que sera passado para o controller
        Categoria categoria = new CategoriaController().buscarCategoriaPorId(categoriaId);
        Produto produto = new Produto(id,nome, descricao,Integer.parseInt(quantidade),Double.parseDouble(precoCompraStr),Double.parseDouble(precoVendaStr), categoria);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente salvar o produto?", "Confirmaçâo", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            if (controller.salvar(produto)) {
                JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!");
                listarProduto();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar produto");
            }
        }

    }

    private void deletarProduto() {
        if (txtIdProduto.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Selecione um produto para deletar.");
            return;
        }

        int id = Integer.parseInt(txtIdProduto.getText());

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar o produto?", "Confirmaçâo", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION){
            if (controller.deletar(id)){
                JOptionPane.showMessageDialog(this, "Produto deletado com sucesso!");
                listarProduto();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this,"Erro ao deletar produto!");
            }
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

        txtIdProduto.setText("");
        txtNomeProduto.setText("");
        txtDescricaoProduto.setText("");
        txtQuantidadeProduto.setText("");
        txtPrecoCompra.setText("");
        txtPrecoVenda.setText("");
        cmbCategoria.setSelectedIndex(0);
        sorter.setSortKeys(Collections.emptyList());
    }

    // metodo para abrir o filtro.
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

    // metodo para atualizar a tabela de acordo com o filtro
    private void atualizarTabela(List<Produto> produtosFiltrados) {
        tableModel.setRowCount(0);

        for (Produto produto : produtosFiltrados){
            tableModel.addRow(new Object[]{produto.getId(), produto.getNome(),produto.getDescricao(),produto.getCategoria().getNome(),produto.getQuantidade(),produto.getPrecoCompra(),produto.getPrecoVenda()});
        }
    }

    private void preencherCamposComDadosDaLinha(int selectedRow) {
        String idProduto =  tableModel.getValueAt(selectedRow, 0).toString();
        String nomeProduto = (String) tableModel.getValueAt(selectedRow, 1);
        String descricaoProduto = (String) tableModel.getValueAt(selectedRow, 2);
        String nomeCategoria = (String) tableModel.getValueAt(selectedRow, 3); // Nome da categoria na tabela
        int quantidade = (int) tableModel.getValueAt(selectedRow, 4);
        double precoCompra = (double) tableModel.getValueAt(selectedRow, 5);
        double precoVenda = (double) tableModel.getValueAt(selectedRow, 6);

        // Preenche os campos de texto com os dados
        txtIdProduto.setText(idProduto);
        txtNomeProduto.setText(nomeProduto);
        txtDescricaoProduto.setText(descricaoProduto);
        txtQuantidadeProduto.setText(String.valueOf(quantidade));
        txtPrecoCompra.setText(String.valueOf(precoCompra));
        txtPrecoVenda.setText(String.valueOf(precoVenda));

        // Definir a categoria no JComboBox
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Categoria categoria = cmbCategoria.getItemAt(i); // Obtém o item da categoria no JComboBox
            if (categoria.getNome().equals(nomeCategoria)) { // Compara o nome da categoria
                cmbCategoria.setSelectedIndex(i); // Define o índice correto da categoria
            }
        }
    }


}
