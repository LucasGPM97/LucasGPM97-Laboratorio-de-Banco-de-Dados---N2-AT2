package view;

import controller.CategoriaController;
import model.Categoria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProdutoFiltroView extends JDialog {

    private JTextField txtNomeProduto, txtQuantidadeEmEstoqueMin, txtQuantidadeEmEstoqueMax;
    private JComboBox<Categoria> cmbCategoria;
    private JButton btnFiltrar;

    public ProdutoFiltroView(JFrame parent){
        super(parent, "Filtros de Consulta", true);
        setSize(500, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        // Campos de filtro
        panel.add(new JLabel("Nome do Produto:"));
        txtNomeProduto = new JTextField();
        panel.add(txtNomeProduto);

        panel.add(new JLabel("Categoria:"));
        cmbCategoria = new JComboBox<>();
        carregarCategoriasParaCombo();
        panel.add(cmbCategoria);

        panel.add(new JLabel("Quantidade Mínima em Estoque:"));
        txtQuantidadeEmEstoqueMin = new JTextField();
        panel.add(txtQuantidadeEmEstoqueMin);

        panel.add(new JLabel("Quantidade Máxima em Estoque:"));
        txtQuantidadeEmEstoqueMax = new JTextField();
        panel.add(txtQuantidadeEmEstoqueMax);

        // Botão para filtrar
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();  // Fecha a tela de filtro
            }
        });
        panel.add(btnFiltrar);

        add(panel);
    }


    private void carregarCategoriasParaCombo() {
        List<Categoria> categorias = new CategoriaController().listar();
        cmbCategoria.removeAllItems();


        cmbCategoria.addItem(null);
        for (Categoria categoria : categorias){
            cmbCategoria.addItem(categoria);
        }

    }

    public String getNomeProduto() {
        return txtNomeProduto.getText().trim();
    }

    public Categoria getCategoria(){
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
        return categoria;
    }

    public Integer getMax(){
        // Obtém o valor do campo de texto e tenta convertê-lo para Integer
        try {
            return Integer.parseInt(txtQuantidadeEmEstoqueMax.getText().trim());
        } catch (NumberFormatException e) {
            // Caso o valor não seja um número válido, retorna null
            return null;
        }
    }

    public Integer getMin(){
        // Obtém o valor do campo de texto e tenta convertê-lo para Integer
        try {
            return Integer.parseInt(txtQuantidadeEmEstoqueMin.getText().trim());
        } catch (NumberFormatException e) {
            // Caso o valor não seja um número válido, retorna null
            return null;
        }
    }



}