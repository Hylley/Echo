import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Dashboard extends JFrame
{
	private JPanel panel;
	private JList<String> echoes_list;
	private JButton home_btn;
	private JButton qr_btn;
	private JScrollBar scrollBar1;
	private final DefaultListModel<String> echoes_list_model = new DefaultListModel<>();

	public Dashboard()
	{
		setContentPane(panel); setTitle("Echo Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); pack();
		setMinimumSize(new Dimension(800, 600)); setSize(800, 600);
		setLocationRelativeTo(null); setIconImage(new ImageIcon("res/icons/echo.png").getImage());

		echoes_list.setModel(echoes_list_model);

		setVisible(true);
		qr_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage qr = Main.get_data_format_qr();
				if(qr == null) return;

				JFrame qr_frame = new JFrame();
				JLabel qr_label = new JLabel();

				qr_frame.setSize(qr.getWidth(), qr.getHeight()); qr_frame.setResizable(false);
				qr_frame.setIconImage(qr); qr_frame.setLocationRelativeTo(null);
				qr_label.setIcon(new ImageIcon(qr));
				qr_frame.getContentPane().add(qr_label, BorderLayout.CENTER);
				qr_frame.pack();
				qr_frame.setVisible(true);
			}
		});
	}

	public void update_echoes_list(String[] list)
	{
		echoes_list_model.addElement(list[0]);
	}
}
