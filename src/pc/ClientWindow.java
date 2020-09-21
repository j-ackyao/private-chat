package pc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	static int windowW = 1200;
	static int windowH = 700;
	
	JPanel main;
	JLabel text;
	
	JTextField textField;
	
	JButton sendButton;
	
	static JLabel textLabel;
	static String texts = "";
	
	
	public ClientWindow() {
		super("wingwong");
		setLayout(null);
		setVisible(true);
		setSize(1200, 700);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		sendButton = new JButton("Send");
		getRootPane().setDefaultButton(sendButton);
		sendButton.addActionListener(sendButtonPressed);
        add(sendButton);
        
        textField = new JTextField();
        add(textField);
        
        textLabel = new JLabel();
        textLabel.setVerticalAlignment(JLabel.TOP);
        add(textLabel);
		
		addComponentListener(resizeListener);
		resizeListener.componentResized(null); // Calls the "function" from listener
	}
	
	public static void printLine(Object text) {
		texts = texts + text + "\n";
		textLabel.setText(texts);
	}
	
	ActionListener sendButtonPressed = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(textField.getText());
			textField.setText("");
		}
	};
	
	ComponentAdapter resizeListener = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			windowW = getBounds().width;
			windowH = getBounds().height;
			
			// Where you set all your sizes and stuff.
			int textFieldSizeX = (int) Math.round(windowW * 0.7);
			int textFieldSizeY = 25;
			int sendButtonSizeX = 200;
			int sendButtonSizeY = 25;
			
			textField.setBounds(5, windowH - textFieldSizeY - 44, textFieldSizeX, textFieldSizeY);
			sendButton.setBounds(textFieldSizeX + 10, windowH - textFieldSizeY - 44, sendButtonSizeX, sendButtonSizeY);
	        textLabel.setBounds(5, 5, windowW - 10, windowH - 10);
		}
	};
}
