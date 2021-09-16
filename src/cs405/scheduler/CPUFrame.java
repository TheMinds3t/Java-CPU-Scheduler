package cs405.scheduler;

import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class CPUFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public CPUFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{};
		gridBagLayout.rowWeights = new double[]{};
		getContentPane().setLayout(gridBagLayout);
	}

}
