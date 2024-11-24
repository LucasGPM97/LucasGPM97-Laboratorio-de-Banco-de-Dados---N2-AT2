package utils;

import javax.swing.*;

public class Validadores {
    public boolean verificarSeVazio(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isEmpty()) {
                return true; // Retorna true se algum campo estiver vazio
            }
        }
        return false; // Retorna false se nenhum campo estiver vazio
    }

    // Método para verificar se os atributos podem ser convertidos para double
    public  boolean verificarSeDouble(String... campos) {
        for (String campo : campos) {
            try {
                // Tenta converter o campo para double
                Double.parseDouble(campo);
            } catch (NumberFormatException e) {
                return false; // Retorna false se algum campo não for um número válido
            }
        }
        return true; // Retorna true se todos os campos forem números válidos
    }

    // Método para verificar se os atributos podem ser convertidos para int
    public  boolean verificarSeInt(String... campos) {
        for (String campo : campos) {
            try {
                Integer.parseInt(campo);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true; // Retorna true se todos os campos forem números válidos
    }

    // Método para verificar se algum dos valores passados é negativo
    public boolean verificarSeNegativo(String... campos) {
        for (String campo : campos) {
            if (Double.parseDouble(campo) < 0) {
                return true; // Retorna true se algum valor for negativo
            }
        }
        return false; // Retorna false se nenhum valor for negativo
    }

    public boolean validarProduto(String nome, String descricao, String quantidade, String precoVendaStr, String precoCompraStr, String categoriaSelecionada) {

        if (verificarSeVazio(nome, descricao, quantidade, precoVendaStr, precoCompraStr, categoriaSelecionada)) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        if (!verificarSeInt(quantidade)) {
            JOptionPane.showMessageDialog(null, "A quantidade deve ser um número válido.");
            return false;
        }

        if (!verificarSeDouble(precoCompraStr,precoVendaStr)) {
            JOptionPane.showMessageDialog(null, "O preço de compra e venda deve ser um número válido.");
            return false;
        }

        if(verificarSeNegativo(quantidade,precoCompraStr,precoVendaStr)){
            JOptionPane.showMessageDialog(null, "Não pode ser um valor negativo.");
            return false;
        }

        return true;
    }

    public boolean validarOperacao(String quantidadeStr, int quantidadeEmEstoque, String operacao) {

        if (verificarSeVazio(quantidadeStr)){
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        if (!verificarSeInt(quantidadeStr)){
            return false;
        }

        if(verificarSeNegativo(quantidadeStr)){
            JOptionPane.showMessageDialog(null, "Não pode ser um valor negativo.");
            return false;
        }
        int quantidade = Integer.parseInt(quantidadeStr);

        if ("Saida".equals(operacao)) {
            if (quantidadeEmEstoque < quantidade) {
                JOptionPane.showMessageDialog(null, "Quantidade em estoque insuficiente para a saída. Estoque atual: " + quantidadeEmEstoque);
                return false;
            }


        }
        return true;
    }
}
