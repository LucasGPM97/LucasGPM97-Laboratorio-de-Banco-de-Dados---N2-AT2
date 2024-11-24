package view;

import controller.CategoriaController;
import model.Categoria;
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

public class CategoriaView extends JFrame {

    private JTextField txtId, txtNomeCategoria, txtDescricaoCategoria;
    private JTable tableCategorias;
    private JButton btnSalvar, btnDeletar, btnListar, btnLimpar, btnConsultar;
    private DefaultTableModel tableModel;
    private CategoriaController controller;
    private TableRowSorter<TableModel> sorter;

    public CategoriaView(CategoriaController controller){
        this.controller = controller;

        setTitle("Gerenciar Categorias");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Painel de formulário para cadastro/edição
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));

        formPanel.add(new JLabel("*ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nome da Categoria:"));
        txtNomeCategoria = new JTextField();
        formPanel.add(txtNomeCategoria);

        formPanel.add(new JLabel("Descrição:"));
        txtDescricaoCategoria = new JTextField();
        formPanel.add(txtDescricaoCategoria);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5, 10, 10));  // 1 linha, 5 colunas

        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarCategoria());
        buttonPanel.add(btnSalvar);

        btnDeletar = new JButton("Excluir");
        btnDeletar.addActionListener(e -> deletarCategoria());
        buttonPanel.add(btnDeletar);


        btnListar = new JButton("Listar");
        btnListar.addActionListener(e -> listarCategoria());
        buttonPanel.add(btnListar);


        btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        buttonPanel.add(btnLimpar);

        btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultarCategoria());
        buttonPanel.add(btnConsultar);


        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);  // Painel de botões na parte inferior

        // Tabela para listar as categorias cadastradas
        String[] columnNames = {"ID", "Nome", "Descrição"};
        tableModel = new NonEditableTableModel(columnNames, 0);
        tableCategorias = new JTable(tableModel);

        // Configura o TableRowSorter para permitir a ordenação pelas colunas
        sorter = new TableRowSorter<>(tableModel);
        tableCategorias.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tableCategorias);

        // Adicionar a tabela à tela
        panel.add(scrollPane, BorderLayout.CENTER);

        // Adiciona o painel principal à janela
        add(panel);


        tableCategorias.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Verifica se a seleção foi ajustada
                    int selectedRow = tableCategorias.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRowIndex = tableCategorias.convertRowIndexToModel(selectedRow);
                        preencherCamposComDadosDaLinha(modelRowIndex);
                    }
                }
            }
        });

        setLocationRelativeTo(null);  // Centraliza a janela na tela
    }

    private void salvarCategoria() {
        int id = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String nome = txtNomeCategoria.getText();
        String descricao = txtDescricaoCategoria.getText();
        Categoria categoria = new Categoria(id,nome, descricao);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente salvar o produto?", "Confirmaçâo", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            if (controller.salvar(categoria)) {
                JOptionPane.showMessageDialog(this, "Categoria salva com sucesso!");
                listarCategoria();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar categoria");
            }
        }

    }

    private void deletarCategoria() {
        if (txtId.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para deletar.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar a categoria e desvincular os produtos?", "Confirmaçâo", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION){
            if (controller.deletar(id)){
                JOptionPane.showMessageDialog(this, "Categoria deletada e produtos desvinculados com sucesso !");
                listarCategoria();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this,"Erro ao deletar categoria!");
            }
        }
    }

    private void listarCategoria() {
        tableModel.setRowCount(0);
        List<Categoria> categorias = controller.listar();
        for (Categoria categoria : categorias){
            tableModel.addRow(new Object[]{categoria.getId(), categoria.getNome(), categoria.getDescricao()});
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNomeCategoria.setText("");
        txtDescricaoCategoria.setText("");
        sorter.setSortKeys(Collections.emptyList());
    }

    // metodo para abrir o filtro.
    private void consultarCategoria() {

        CategoriaFiltroView filtroView = new CategoriaFiltroView(this);
        filtroView.setVisible(true);

        // Após fechar a tela de filtro, verifica se o filtro foi aplicado
        String nomeCategoria = filtroView.getNomeCategoria();

        // Realiza a consulta, se o campo de nome não estiver vazio
        if (nomeCategoria != null && !nomeCategoria.isEmpty()) {
            // Filtra as categorias usando o nome
            List<Categoria> categoriasFiltradas = controller.listarPorNome(nomeCategoria);
            atualizarTabela(categoriasFiltradas);
        } else {
            // Caso contrário, lista todas as categorias
            listarCategoria();
        }
    }

    // metodo para atualizar a tabela de acordo com o filtro
    private void atualizarTabela(List<Categoria> categorias) {
        // Limpa as linhas da tabela
        tableModel.setRowCount(0);

        // Adiciona as categorias filtradas à tabela
        for (Categoria categoria : categorias) {
            tableModel.addRow(new Object[]{categoria.getId(), categoria.getNome(), categoria.getDescricao()});
        }
    }
    private void preencherCamposComDadosDaLinha(int selectedRow) {
        String idCategoria =  tableModel.getValueAt(selectedRow, 0).toString();
        String nomeCategoria = (String) tableModel.getValueAt(selectedRow, 1);
        String descricaoCategoria = (String) tableModel.getValueAt(selectedRow, 2);

        // Preenche os campos de texto com os dados
        txtId.setText(idCategoria);
        txtNomeCategoria.setText(nomeCategoria);
        txtDescricaoCategoria.setText(descricaoCategoria);

    }
}
