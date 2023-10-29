import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Dashboard extends JFrame
{
	private JPanel panel;
	private JList<String> echoes_list;
	private JButton qr_btn;
	public final DefaultListModel<String> echoes_list_model = new DefaultListModel<>();

	public Dashboard()
	{
		setContentPane(panel); setTitle("Echo Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); pack();
		setMinimumSize(new Dimension(800, 600)); setSize(800, 600);
		setLocationRelativeTo(null); setIconImage(new ImageIcon("res/icons/echo.png").getImage());
		echoes_list.setModel(echoes_list_model);

		qr_btn.addActionListener(e ->
		{
			BufferedImage qr_code  = Main.get_data_format_qr(); if(qr_code == null) return;
			JFrame		  qr_frame = new JFrame();
			JLabel 		  qr_label = new JLabel();

			qr_frame.setSize(qr_code.getWidth(), qr_code.getHeight()); qr_frame.setResizable(false);
			qr_frame.setIconImage(qr_code); qr_frame.setLocationRelativeTo(null);
			qr_label.setIcon(new ImageIcon(qr_code));
			qr_frame.getContentPane().add(qr_label, BorderLayout.CENTER);
			qr_frame.pack();
			qr_frame.setVisible(true);
		});

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent)
			{
				Main.shut();
				System.exit(0);
			}
		});

		setVisible(true);
	}

	public void append_echoes_list(String string)
	{
		if (echoes_list_model.contains(string)) return;
		echoes_list_model.addElement(string);
	}
}
