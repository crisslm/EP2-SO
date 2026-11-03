package usp.ReadWriteImplementation.Graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import usp.ReadWriteImplementation.Entities.Data;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GraphView {
    public static void run(List<Data> data) {
        // 1. Criar o dataset para o gráfico
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Data d : data) {
            dataset.addValue(d.getTime(), "Readers & Writers (Prioridade Leitores)", d.getX());
        }

        JFreeChart grafico = ChartFactory.createLineChart(
                "Desempenho: Prioridade dos Leitores (Readers & Writers)",
                "Proporção (Leitores / Escritores)",
                "Tempo Médio (ms)",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(grafico);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        // 2. Criar tabela de resultados
        String[] colunas = {"Proporção (R/W)", "Tempo Médio (ms)"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Data d : data) {
            model.addRow(new Object[]{d.getX(), String.format("%.2f", d.getTime())});
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(350, 500));

        // 3. Montar a janela com abas ou painel dividido
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gráfico", chartPanel);
        tabbedPane.addTab("Tabela de Dados", tableScrollPane);

        JFrame janela = new JFrame("Desempenho - Readers and Writers");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janela.add(tabbedPane);
        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
