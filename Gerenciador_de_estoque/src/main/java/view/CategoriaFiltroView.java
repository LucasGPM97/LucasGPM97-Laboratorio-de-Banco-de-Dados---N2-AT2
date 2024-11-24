package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoriaFiltroView extends JDialog {
    private JButton btnFiltrar;
    private JTextField txtNomeCategoria;

    public CategoriaFiltroView(JFrame parent){

        super(parent, "Filtros de Consulta", true);
        setSize(500, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));

        // Campos de filtro
        panel.add(new JLabel("Nome da Categoria:"));
        txtNomeCategoria = new JTextField();
        panel.add(txtNomeCategoria);

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

    // retorna o texto que devera ser filtrado
    public String getNomeCategoria() {
        return txtNomeCategoria.getText();
    }

}
