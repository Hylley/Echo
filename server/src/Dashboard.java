import javax.swing.*;

public class Dashboard extends JFrame
{
	private JPanel panel;
	private JButton button1;

	public Dashboard()
	{
		setContentPane(panel); setTitle("Echo Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); pack();
		setSize(800, 600); setLocationRelativeTo(null);
		setIconImage(new ImageIcon("res/echo.png").getImage());

		setVisible(true);
	}
}
