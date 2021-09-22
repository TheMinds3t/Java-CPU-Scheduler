package cs405.scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CPUFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public CPUFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel headerPanel = new JPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		
		JLabel headerLabel = new JLabel("CPU Scheduler");
		headerLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		headerPanel.add(headerLabel);
		
		JPanel graphContainer = new JPanel();
		getContentPane().add(graphContainer, BorderLayout.SOUTH);
		GridBagLayout gbl_graphContainer = new GridBagLayout();
		gbl_graphContainer.columnWidths = new int[] {64, 382, 0};
		gbl_graphContainer.rowHeights = new int[] {128, 128, 0};
		gbl_graphContainer.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_graphContainer.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		graphContainer.setLayout(gbl_graphContainer);
		
		JLabel cpuLabel = new JLabel("CPU");
		cpuLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_cpuLabel = new GridBagConstraints();
		gbc_cpuLabel.weightx = 0.3;
		gbc_cpuLabel.fill = GridBagConstraints.BOTH;
		gbc_cpuLabel.insets = new Insets(0, 0, 5, 5);
		gbc_cpuLabel.gridx = 0;
		gbc_cpuLabel.gridy = 0;
		graphContainer.add(cpuLabel, gbc_cpuLabel);
		
		JPanel cpuPanel = new JPanel();
		cpuPanel.setBackground(Color.WHITE);
		cpuPanel.setLayout(null);
		GridBagConstraints gbc_cpuPanel = new GridBagConstraints();
		gbc_cpuPanel.weightx = 100.0;
		gbc_cpuPanel.gridwidth = 20;
		gbc_cpuPanel.insets = new Insets(0, 0, 5, 0);
		gbc_cpuPanel.fill = GridBagConstraints.BOTH;
		gbc_cpuPanel.gridx = 1;
		gbc_cpuPanel.gridy = 0;
		graphContainer.add(cpuPanel, gbc_cpuPanel);
		
		JLabel ioLabel = new JLabel("I/O");
		ioLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_ioLabel = new GridBagConstraints();
		gbc_ioLabel.insets = new Insets(0, 0, 0, 5);
		gbc_ioLabel.gridx = 0;
		gbc_ioLabel.gridy = 1;
		graphContainer.add(ioLabel, gbc_ioLabel);
		
		JPanel ioPanel = new JPanel();
		ioPanel.setBackground(Color.WHITE);
		ioPanel.setLayout(null);
		GridBagConstraints gbc_ioPanel = new GridBagConstraints();
		gbc_ioPanel.gridwidth = 20;
		gbc_ioPanel.fill = GridBagConstraints.BOTH;
		gbc_ioPanel.gridx = 1;
		gbc_ioPanel.gridy = 1;
		graphContainer.add(ioPanel, gbc_ioPanel);
		
		JPanel algPanel = new JPanel();
		algPanel.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(algPanel, BorderLayout.WEST);
		GridBagLayout gbl_algPanel = new GridBagLayout();
		gbl_algPanel.columnWidths = new int[] {64, 30, 96};
		gbl_algPanel.rowHeights = new int[] {24, 32, 32, 32, 32};
		gbl_algPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_algPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		algPanel.setLayout(gbl_algPanel);
		
		JLabel algSelectLabel = new JLabel("Select Algorithm");
		algSelectLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		algSelectLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_algSelectLabel = new GridBagConstraints();
		gbc_algSelectLabel.gridwidth = 3;
		gbc_algSelectLabel.insets = new Insets(0, 0, 5, 5);
		gbc_algSelectLabel.gridx = 0;
		gbc_algSelectLabel.gridy = 0;
		algPanel.add(algSelectLabel, gbc_algSelectLabel);
		
		JLabel fcfsAlgLabel = new JLabel("FCFS");
		fcfsAlgLabel.setBackground(Color.GRAY);
		fcfsAlgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		fcfsAlgLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_fcfsAlgLabel = new GridBagConstraints();
		gbc_fcfsAlgLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fcfsAlgLabel.gridx = 0;
		gbc_fcfsAlgLabel.gridy = 1;
		algPanel.add(fcfsAlgLabel, gbc_fcfsAlgLabel);
		
		JButton fcfsButton = new JButton("Select");
		GridBagConstraints gbc_fcfsButton = new GridBagConstraints();
		gbc_fcfsButton.insets = new Insets(0, 0, 5, 0);
		gbc_fcfsButton.gridx = 2;
		gbc_fcfsButton.gridy = 1;
		algPanel.add(fcfsButton, gbc_fcfsButton);
		
		JLabel rrAlgLabel = new JLabel("RR");
		rrAlgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rrAlgLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_rrAlgLabel = new GridBagConstraints();
		gbc_rrAlgLabel.insets = new Insets(0, 0, 5, 5);
		gbc_rrAlgLabel.gridx = 0;
		gbc_rrAlgLabel.gridy = 2;
		algPanel.add(rrAlgLabel, gbc_rrAlgLabel);
		
		JButton rrButton = new JButton("Select");
		rrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_rrButton = new GridBagConstraints();
		gbc_rrButton.insets = new Insets(0, 0, 5, 0);
		gbc_rrButton.gridx = 2;
		gbc_rrButton.gridy = 2;
		algPanel.add(rrButton, gbc_rrButton);
		
		JLabel sjfAlgLabel = new JLabel("SJF");
		sjfAlgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sjfAlgLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_sjfAlgLabel = new GridBagConstraints();
		gbc_sjfAlgLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sjfAlgLabel.gridx = 0;
		gbc_sjfAlgLabel.gridy = 3;
		algPanel.add(sjfAlgLabel, gbc_sjfAlgLabel);
		
		JButton sjfButton = new JButton("Select");
		GridBagConstraints gbc_sjfButton = new GridBagConstraints();
		gbc_sjfButton.insets = new Insets(0, 0, 5, 0);
		gbc_sjfButton.gridx = 2;
		gbc_sjfButton.gridy = 3;
		algPanel.add(sjfButton, gbc_sjfButton);
		
		JLabel psAlgLabel = new JLabel("PS");
		psAlgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		psAlgLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_psAlgLabel = new GridBagConstraints();
		gbc_psAlgLabel.insets = new Insets(0, 0, 0, 5);
		gbc_psAlgLabel.gridx = 0;
		gbc_psAlgLabel.gridy = 4;
		algPanel.add(psAlgLabel, gbc_psAlgLabel);
		
		JButton psButton = new JButton("Select");
		GridBagConstraints gbc_psButton = new GridBagConstraints();
		gbc_psButton.gridx = 2;
		gbc_psButton.gridy = 4;
		algPanel.add(psButton, gbc_psButton);
	}

}
