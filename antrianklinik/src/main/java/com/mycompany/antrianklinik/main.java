package com.mycompany.antrianklinik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Pasien {
    String nama;
    int usia;
    String jenisKelamin;
    String keluhan;
    String prioritas;
    int nomorAntrian;

    public Pasien(String nama, int usia, String jenisKelamin, String keluhan, String prioritas, int nomorAntrian) {
        this.nama = nama;
        this.usia = usia;
        this.jenisKelamin = jenisKelamin;
        this.keluhan = keluhan;
        this.prioritas = prioritas;
        this.nomorAntrian = nomorAntrian;
    }
}

public class main extends JFrame {
    private int nomorAntrian = 1;
    private PriorityQueue<Pasien> antrian;
    private java.util.List<Pasien> histori;

    private JTextField tfNama, tfUsia, tfKeluhan;
    private JComboBox<String> cbJenisKelamin, cbPrioritas;
    private JTextArea taStatus;
    private DefaultTableModel modelHistori;
    private JLabel lblSedangDilayani;

    public main() {
        setTitle("Aplikasi Antrian Klinik");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        antrian = new PriorityQueue<>((p1, p2) -> getPriorityValue(p1.prioritas) - getPriorityValue(p2.prioritas));
        histori = new ArrayList<>();

        initComponents();
    }

    private int getPriorityValue(String p) {
        return switch (p) {
            case "Darurat" -> 1;
            case "Lansia" -> 2;
            default -> 3;
        };
    }

    private void initComponents() {
        JTabbedPane tab = new JTabbedPane();

        // Form Pendaftaran
        JPanel panelForm = new JPanel(new GridLayout(7, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tfNama = new JTextField();
        tfUsia = new JTextField();
        tfKeluhan = new JTextField();
        cbJenisKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        cbPrioritas = new JComboBox<>(new String[]{"Umum", "Lansia", "Darurat"});

        JButton btnDaftar = new JButton("Daftar");
        btnDaftar.addActionListener(e -> daftarPasien());

        panelForm.add(new JLabel("Nama")); panelForm.add(tfNama);
        panelForm.add(new JLabel("Usia")); panelForm.add(tfUsia);
        panelForm.add(new JLabel("Jenis Kelamin")); panelForm.add(cbJenisKelamin);
        panelForm.add(new JLabel("Keluhan")); panelForm.add(tfKeluhan);
        panelForm.add(new JLabel("Jenis Prioritas")); panelForm.add(cbPrioritas);
        panelForm.add(new JLabel("")); panelForm.add(btnDaftar);

        tab.add("Pendaftaran", panelForm);

        // Antrian & Pelayanan
        JPanel panelAntrian = new JPanel(new BorderLayout());
        panelAntrian.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        lblSedangDilayani = new JLabel("Belum ada pasien", JLabel.CENTER);
        JButton btnLayani = new JButton("Layani Pasien Berikutnya");
        btnLayani.addActionListener(e -> layaniPasien());

        taStatus = new JTextArea();
        taStatus.setEditable(false);
        JScrollPane scrollStatus = new JScrollPane(taStatus);

        panelAntrian.add(lblSedangDilayani, BorderLayout.NORTH);
        panelAntrian.add(scrollStatus, BorderLayout.CENTER);
        panelAntrian.add(btnLayani, BorderLayout.SOUTH);

        tab.add("Antrian & Pelayanan", panelAntrian);

        // Histori
        JPanel panelHistori = new JPanel(new BorderLayout());
        panelHistori.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        modelHistori = new DefaultTableModel(new String[]{"No", "Nama", "Prioritas"}, 0);
        JTable tableHistori = new JTable(modelHistori);
        JScrollPane scrollHistori = new JScrollPane(tableHistori);
        panelHistori.add(scrollHistori, BorderLayout.CENTER);

        tab.add("Histori", panelHistori);

        add(tab);
    }

    private void daftarPasien() {
        try {
            String nama = tfNama.getText();
            int usia = Integer.parseInt(tfUsia.getText());
            String jk = cbJenisKelamin.getSelectedItem().toString();
            String keluhan = tfKeluhan.getText();
            String prioritas = cbPrioritas.getSelectedItem().toString();
            
            tfNama.setText("");
            
            Pasien p = new Pasien(nama, usia, jk, keluhan, prioritas, nomorAntrian++);
            antrian.add(p);

            JOptionPane.showMessageDialog(this, "Pasien didaftarkan dengan nomor antrian: " + p.nomorAntrian);
            updateStatus();
            tfNama.setText("");
            tfUsia.setText("");
            tfKeluhan.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void layaniPasien() {
        Pasien p = antrian.poll();
        if (p != null) {
            lblSedangDilayani.setText("Sedang Dilayani: " + p.nama);
            modelHistori.addRow(new Object[]{p.nomorAntrian, p.nama, p.prioritas});
            histori.add(p);
            updateStatus();
        } else {
            lblSedangDilayani.setText("Tidak ada pasien dalam antrian.");
        }
    }

    private void updateStatus() {
        StringBuilder sb = new StringBuilder();
        PriorityQueue<Pasien> temp = new PriorityQueue<>(antrian);
        while (!temp.isEmpty()) {
            Pasien p = temp.poll();
            sb.append(p.nomorAntrian).append(" - ").append(p.nama).append(" (Prioritas: ").append(p.prioritas).append(")\n");
        }
        taStatus.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main().setVisible(true));
    }
}
