package cs405.scheduler.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * The main JFrame for the application. Sports many helper methods for updating the elements present in the GUI.
 * @author Ashton Schultz
 */
public class CPUFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable processTable;
	private JScrollPane processPanel;
	private JTextPane systemDataLabel;
	private JTextField qField;
	private JTextPane processLog;
	private ButtonGroup algGroup = new ButtonGroup();
	private JRadioButtonMenuItem[] algButs = new JRadioButtonMenuItem[4];
	private JComboBox<Integer> fpsCombo;
	private QueuePanel queuePanel = new QueuePanel();
	
	private ArrayList<ProcessLogEntry> processLogRaw = new ArrayList<ProcessLogEntry>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CPUFrame frame = new CPUFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CPUFrame() {
		setBackground(Color.GRAY);
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.WHITE);
		menuBar.setMargin(new Insets(0, 20, 0, 20));
		setJMenuBar(menuBar);
		
		JLabel menuLMarg = new JLabel("          ");
		menuBar.add(menuLMarg);
		
		JLabel algLabel = new JLabel("Scheduling Algorithm:  ");
		menuBar.add(algLabel);
		JRadioButtonMenuItem fcfsAlg = new JRadioButtonMenuItem("FCFS");
		fcfsAlg.setSelected(true);
		menuBar.add(fcfsAlg);
		
		JRadioButtonMenuItem nonpAlg = new JRadioButtonMenuItem("Non-Preemptive");
		menuBar.add(nonpAlg);
		
		JRadioButtonMenuItem sjfAlg = new JRadioButtonMenuItem("SJF");
		menuBar.add(sjfAlg);
		
		JRadioButtonMenuItem rrAlg = new JRadioButtonMenuItem("RR");
		menuBar.add(rrAlg);
		
		
		algButs[0] = fcfsAlg;
		algButs[1] = nonpAlg;
		algButs[2] = sjfAlg;
		algButs[3] = rrAlg;

		algGroup.add(sjfAlg);
		algGroup.add(nonpAlg);
		algGroup.add(fcfsAlg);
		algGroup.add(rrAlg);
		
		JLabel qLabel = new JLabel("q=");
		menuBar.add(qLabel);
		
		qField = new JTextField();
		menuBar.add(qField);
		qField.setText("1");
		//Filter input to numeric only
		PlainDocument doc = (PlainDocument) qField.getDocument();
	    doc.setDocumentFilter(new DocumentFilter()
	    {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException 
			{
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.insert(offset, string);

				if (test(sb.toString())) {
					super.insertString(fb, offset, string, attr);
				}
			}

			private boolean test(String text) 
			{
				try {
					Integer.parseInt(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException 
			{
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.replace(offset, offset + length, text);

				if (test(sb.toString())) {
					super.replace(fb, offset, length, text, attrs);
				}
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException 
			{
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.delete(offset, offset + length);

				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				}
			}
			
			// Source:
			// https://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
	    });
		
		JLabel menuRMarg = new JLabel("      ");
		menuBar.add(menuRMarg);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {200, 200, 200, 200};
		gbl_contentPane.rowHeights = new int[] {100, 100, 100, 100, 100, 100};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE, 1.0, 1.0};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane systemDataPanel = new JScrollPane();
		GridBagConstraints gbc_systemDataPanel = new GridBagConstraints();
		gbc_systemDataPanel.gridheight = 3;
		gbc_systemDataPanel.insets = new Insets(0, 0, 5, 5);
		gbc_systemDataPanel.fill = GridBagConstraints.BOTH;
		gbc_systemDataPanel.gridx = 0;
		gbc_systemDataPanel.gridy = 0;
		contentPane.add(systemDataPanel, gbc_systemDataPanel);
		
		systemDataLabel = new JTextPane();
		systemDataPanel.setViewportView(systemDataLabel);
		systemDataPanel.setBounds(0, 0, 100, 100);
		
		JPanel panel = new JPanel();
		systemDataPanel.setColumnHeaderView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {89, 30, 32, 30};
		gbl_panel.rowHeights = new int[] {0, 23, 0, 0, 0, 24};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel.setLayout(gbl_panel);
		
		JLabel lblExecuteLabel = new JLabel("Execution Controls");
		GridBagConstraints gbc_lblExecuteLabel = new GridBagConstraints();
		gbc_lblExecuteLabel.gridwidth = 4;
		gbc_lblExecuteLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblExecuteLabel.gridx = 0;
		gbc_lblExecuteLabel.gridy = 0;
		panel.add(lblExecuteLabel, gbc_lblExecuteLabel);
		
		JButton btnStartStop = new JButton("Start / Stop");
		GridBagConstraints gbc_btnStartStop = new GridBagConstraints();
		gbc_btnStartStop.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnStartStop.insets = new Insets(0, 0, 5, 5);
		gbc_btnStartStop.gridx = 0;
		gbc_btnStartStop.gridy = 1;
		panel.add(btnStartStop, gbc_btnStartStop);
		btnStartStop.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				onStartStop();
			}			
		});
		JLabel fpsLabel = new JLabel("FPS: ");
		GridBagConstraints gbc_fpsLabel = new GridBagConstraints();
		gbc_fpsLabel.anchor = GridBagConstraints.WEST;
		gbc_fpsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fpsLabel.gridx = 1;
		gbc_fpsLabel.gridy = 1;
		panel.add(fpsLabel, gbc_fpsLabel);
		
		fpsCombo = new JComboBox<Integer>();
		fpsCombo.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1, 2, 3, 4, 5}));
		fpsCombo.setSelectedIndex(0);
		GridBagConstraints gbc_fpsCombo = new GridBagConstraints();
		gbc_fpsCombo.anchor = GridBagConstraints.WEST;
		gbc_fpsCombo.insets = new Insets(0, 0, 5, 5);
		gbc_fpsCombo.gridx = 2;
		gbc_fpsCombo.gridy = 1;
		panel.add(fpsCombo, gbc_fpsCombo);
		
		JButton btnStepOnce = new JButton("Step Once");
		GridBagConstraints gbc_btnStepOnce = new GridBagConstraints();
		gbc_btnStepOnce.gridwidth = 2;
		gbc_btnStepOnce.insets = new Insets(0, 0, 5, 5);
		gbc_btnStepOnce.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnStepOnce.gridx = 0;
		gbc_btnStepOnce.gridy = 2;
		btnStepOnce.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				onStepOnce();
			}			
		});
		panel.add(btnStepOnce, gbc_btnStepOnce);
		
		JLabel fileLabel = new JLabel("File");
		GridBagConstraints gbc_fileLabel = new GridBagConstraints();
		gbc_fileLabel.insets = new Insets(0, 0, 5, 0);
		gbc_fileLabel.gridwidth = 4;
		gbc_fileLabel.gridx = 0;
		gbc_fileLabel.gridy = 3;
		panel.add(fileLabel, gbc_fileLabel);
		
		JButton loadFileButton = new JButton("Load From File");
		loadFileButton.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_loadFileButton = new GridBagConstraints();
		gbc_loadFileButton.gridwidth = 2;
		gbc_loadFileButton.insets = new Insets(0, 0, 5, 5);
		gbc_loadFileButton.gridx = 0;
		gbc_loadFileButton.gridy = 4;
		panel.add(loadFileButton, gbc_loadFileButton);
		loadFileButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				int result = chooser.showOpenDialog(CPUFrame.this);
				
				if(result == JFileChooser.APPROVE_OPTION)
				{
					File f = chooser.getSelectedFile();
					onLoadFile(f);
				}
			}			
		});

		JLabel systemInfoLabel = new JLabel("System Information");
		GridBagConstraints gbc_systemInfoLabel = new GridBagConstraints();
		gbc_systemInfoLabel.gridwidth = 4;
		gbc_systemInfoLabel.insets = new Insets(0, 0, 0, 5);
		gbc_systemInfoLabel.gridx = 0;
		gbc_systemInfoLabel.gridy = 5;
		panel.add(systemInfoLabel, gbc_systemInfoLabel);
		setSystemData(1,1,1,1);
		
		JPanel queuesPanel = new JPanel();
		queuesPanel.setBackground(Color.GRAY);
		GridBagConstraints gbc_queuesPanel = new GridBagConstraints();
		gbc_queuesPanel.gridheight = 3;
		gbc_queuesPanel.gridwidth = 3;
		gbc_queuesPanel.insets = new Insets(0, 0, 5, 5);
		gbc_queuesPanel.fill = GridBagConstraints.BOTH;
		gbc_queuesPanel.gridx = 1;
		gbc_queuesPanel.gridy = 0;
		contentPane.add(queuesPanel, gbc_queuesPanel);
		GridBagLayout gbl_queuesPanel = new GridBagLayout();
		gbl_queuesPanel.columnWidths = new int[] {100, 100, 100, 100};
		gbl_queuesPanel.rowHeights = new int[]{0, 0, 0};
		gbl_queuesPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_queuesPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		queuesPanel.setLayout(gbl_queuesPanel);
		
		JLabel queuesLabel = new JLabel("Queues");
		queuesLabel.setForeground(Color.WHITE);
		GridBagConstraints gbc_queuesLabel = new GridBagConstraints();
		gbc_queuesLabel.insets = new Insets(0, 0, 5, 0);
		gbc_queuesLabel.gridwidth = 4;
		gbc_queuesLabel.gridx = 0;
		gbc_queuesLabel.gridy = 0;
		queuesPanel.add(queuesLabel, gbc_queuesLabel);
		
		
		GridBagConstraints gbc_queuePanel = new GridBagConstraints();
		gbc_queuePanel.gridwidth = 4;
		gbc_queuePanel.insets = new Insets(0, 0, 0, 5);
		gbc_queuePanel.fill = GridBagConstraints.BOTH;
		gbc_queuePanel.gridx = 0;
		gbc_queuePanel.gridy = 1;
		queuesPanel.add(queuePanel, gbc_queuePanel);
		
		processPanel = new JScrollPane();
		GridBagConstraints gbc_processPanel = new GridBagConstraints();
		gbc_processPanel.insets = new Insets(0, 0, 0, 5);
		gbc_processPanel.gridheight = 3;
		gbc_processPanel.gridwidth = 3;
		gbc_processPanel.fill = GridBagConstraints.BOTH;
		gbc_processPanel.gridx = 0;
		gbc_processPanel.gridy = 3;
		contentPane.add(processPanel, gbc_processPanel);
		
		processTable = new JTable();
		processTable.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		processPanel.setViewportView(processTable.getTableHeader());
		processPanel.setViewportView(processTable);
		setTableData(null);
		processTable.setFillsViewportHeight(true);
		processTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JPanel logPanel = new JPanel();
		GridBagConstraints gbc_logPanel = new GridBagConstraints();
		gbc_logPanel.gridheight = 3;
		gbc_logPanel.fill = GridBagConstraints.BOTH;
		gbc_logPanel.gridx = 3;
		gbc_logPanel.gridy = 3;
		contentPane.add(logPanel, gbc_logPanel);
		GridBagLayout gbl_logPanel = new GridBagLayout();
		gbl_logPanel.columnWidths = new int[] {10, 40, 40, 40, 40, 10};
		gbl_logPanel.rowHeights = new int[] {20, 220, 20};
		gbl_logPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_logPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		logPanel.setLayout(gbl_logPanel);
		
		JLabel processLogLabel = new JLabel("Process Log");
		processLogLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_processLogLabel = new GridBagConstraints();
		gbc_processLogLabel.gridwidth = 2;
		gbc_processLogLabel.anchor = GridBagConstraints.NORTH;
		gbc_processLogLabel.insets = new Insets(0, 0, 5, 5);
		gbc_processLogLabel.gridx = 2;
		gbc_processLogLabel.gridy = 0;
		logPanel.add(processLogLabel, gbc_processLogLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		logPanel.add(scrollPane, gbc_scrollPane);
		
		processLog = new JTextPane();
		processLog.setContentType("text/html");
		scrollPane.setViewportView(processLog);
		
		addToProcessLog("testing this dang text pane", Color.red);
		addToProcessLog("Hopefully it works!");
	}
	
	/**
	 * Sets the data present in the processTable listing the process details.
	 * 
	 * Format of data:
	 * 		Array of rows, each second dimension is the following:
	 * 			{ID (int), Arrival (int), Priority (int), CPU Bursts (String), I/O Bursts (String), Start Time (int), End Time (int), Wait Time (int), Wait I/O Time (int), Status (String)}
	 * Values can be null to specify an empty cell.
	 * @param data the 2d array to specify the rows to add
	 */
	@SuppressWarnings("rawtypes")
	public void setTableData(Object[][] data)
	{
		processTable.setModel(new DefaultTableModel(
				data == null ? new Object[][] { //Data
					{null, null, null, null, null, null, null, null, null, null},
				} : data,
				new String[] { //Headers
					"ID", "Arrival", "Priority", "CPU Bursts", "I/O Bursts", "Start Time", "End Time", "Wait Time", "Wait I/O Time", "Status"
				}) 
			{
				private static final long serialVersionUID = 1L;
				
				//Constrains input to certain data types
				Class[] columnTypes = new Class[] {
					Integer.class, Integer.class, Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class
				};
				
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
		});
		
		//Sizing 
		processTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		processTable.getColumnModel().getColumn(0).setMinWidth(25);
		processTable.getColumnModel().getColumn(0).setMaxWidth(40);
		processTable.getColumnModel().getColumn(1).setPreferredWidth(42);
		processTable.getColumnModel().getColumn(1).setMinWidth(40);
		processTable.getColumnModel().getColumn(1).setMaxWidth(50);
		processTable.getColumnModel().getColumn(2).setPreferredWidth(44);
		processTable.getColumnModel().getColumn(2).setMinWidth(44);
		processTable.getColumnModel().getColumn(2).setMaxWidth(50);
		processTable.getColumnModel().getColumn(3).setPreferredWidth(78);
		processTable.getColumnModel().getColumn(3).setMinWidth(78);
		processTable.getColumnModel().getColumn(3).setMaxWidth(120);
		processTable.getColumnModel().getColumn(4).setPreferredWidth(78);
		processTable.getColumnModel().getColumn(4).setMinWidth(78);
		processTable.getColumnModel().getColumn(4).setMaxWidth(120);
		processTable.getColumnModel().getColumn(5).setPreferredWidth(60);
		processTable.getColumnModel().getColumn(5).setMinWidth(60);
		processTable.getColumnModel().getColumn(5).setMaxWidth(80);
		processTable.getColumnModel().getColumn(6).setPreferredWidth(57);
		processTable.getColumnModel().getColumn(6).setMinWidth(57);
		processTable.getColumnModel().getColumn(6).setMaxWidth(80);
		processTable.getColumnModel().getColumn(7).setPreferredWidth(57);
		processTable.getColumnModel().getColumn(7).setMinWidth(57);
		processTable.getColumnModel().getColumn(7).setMaxWidth(80);
		processTable.getColumnModel().getColumn(8).setPreferredWidth(80);
		processTable.getColumnModel().getColumn(8).setMinWidth(80);
		processTable.getColumnModel().getColumn(8).setMaxWidth(90);
		processTable.getColumnModel().getColumn(9).setPreferredWidth(60);
		processTable.getColumnModel().getColumn(9).setMinWidth(60);
		processTable.getColumnModel().getColumn(9).setMaxWidth(120);
		processTable.setColumnSelectionAllowed(true);
		processTable.setCellSelectionEnabled(true);
		processTable.getTableHeader().setForeground(new Color(0,100,0));
		
		processPanel.setHorizontalScrollBar(processPanel.createHorizontalScrollBar());
		processPanel.setVerticalScrollBar(processPanel.createVerticalScrollBar());
	}
	
	/**
	 * Adds an entry to the process log, colored black.
	 * @param msg the message to add
	 */
	public void addToProcessLog(String msg)
	{
		addToProcessLog(msg,null);
	}

	/**
	 * Adds an entry to the process log.
	 * @param msg the message to add
	 * @param col the color of the message to add. Can be null, will default to black.
	 */
	public void addToProcessLog(String msg, Color col)
	{
		processLogRaw.add(0,new ProcessLogEntry(msg,col == null ? Color.black : col));
		String formatted = "<html>";
		
		for(ProcessLogEntry ent : processLogRaw)
		{
			formatted += "<p style='margin:0;padding:0;color:rgb("+ent.color.getRed()+","+ent.color.getGreen()+","+ent.color.getBlue()+");'>"+ent.entry+"</p>";
		}
		
		processLog.setText(formatted+"</html>");
	}
	
	/**
	 * Removes all output from the process log.
	 */
	public void wipeProcessLog()
	{
		processLogRaw.clear();
		processLog.setText("");
	}
	
	/**
	 * Updates the JLabel containing the system time, throughput, turnaround and wait times to reflect the parameters passed.
	 * @param time new system time 
	 * @param throughput new throughput
	 * @param turnaround new average turnaround
	 * @param wait new average wait
	 */
	public void setSystemData(int time, double throughput, double turnaround, double wait)
	{
		systemDataLabel.setText("System Time: "+time+"\nThroughput: "+throughput+"\nAverage Turnaround: "+turnaround+"\nAverage Wait: " + wait);
	}
	
	/**
	 * Returns the index of the selected algorithm. Index corresponds with the following algorithm:
	 * 		0 - FCFS,
	 * 		1 - Non-Preemptive,
	 * 		2 - SJF,
	 * 		3 - RR
	 * @return the index of the selected algorithm, or in the event none are selected -1.
	 */
	public int getSelectedAlgorithm()
	{
		for(int i = 0; i < algButs.length; ++i)
		{
			if (algButs[i].isSelected())
			{
				return i;
			}
		}
		
		//THIS SHOULDNT HAPPEN
		return -1;
	}
	
	/**
	 * @return the specified q value, or 1 if there is no text entered. 
	 */
	public int getQValue()
	{
		return qField.getText().length() == 0 ? 1 : Integer.parseInt(qField.getText());
	}
	
	/**
	 * @return The selected framerate from the combo box in the execution controls section.
	 */
	public int getSelectedFrameRate()
	{
		return (Integer)fpsCombo.getSelectedItem();
	}
	
	//TODO
	/**
	 * A placeholder function called when the Step Once button is pressed. To be filled in by backend.
	 */
	public void onStepOnce()
	{
		System.out.println("Stepped once! please fill this method out, or let me know once backend has a callback for this.");
	}

	//TODO
	/**
	 * A placeholder function called when the Start/Stop button is pressed. To be filled in by backend.
	 */
	public void onStartStop()
	{
		System.out.println("Started/Stopped! please fill this method out, or let me know once backend has a callback for this.");
	}

	//TODO
	/**
	 * A placeholder function called when the Load File button is pressed. To be filled in by backend.
	 */
	public void onLoadFile(File file)
	{
		System.out.println("Chose file \'"+file.getAbsolutePath()+"\'! please fill this method out, or let me know once backend has a callback for this.");		
	}

	/**
	 * @return the GUI element to display the CPU / IO processing queues.
	 */
	public QueuePanel getQueuePanel()
	{
		return queuePanel;
	}
	
	/**
	 * A data structure to tuple the entry and colors together for processLogRaw.
	 * @author Ashton Schultz
	 */
	private class ProcessLogEntry
	{
		public String entry;
		public Color color;
		
		public ProcessLogEntry(String ent, Color col)
		{
			entry = ent;
			color = col;
		}
	}
}
