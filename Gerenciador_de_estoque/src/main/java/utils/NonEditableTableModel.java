package utils;

import javax.swing.table.DefaultTableModel;

 // classe para impedir a edicao dos campos na propria tabela
public class NonEditableTableModel extends DefaultTableModel {
    public NonEditableTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;  // Impede a edição de todas as células
    }
}
