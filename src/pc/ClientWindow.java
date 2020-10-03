package pc;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ClientWindow extends JFrame {
	private static final long serialVersionUID = -3231753162628808049L;
	
	static int windowW = 1200;
	static int windowH = 700;
	
	JPanel main;
	JLabel text;
	
	JTextField textField;
	
	JButton sendButton;
	
	static JLabel textLabel;
	
	static JScrollPane scrollPane;
	
	public ClientWindow() {
		super("Client");
		setLayout(null);
		setVisible(true);
		setSize(1200, 700);
		setResizable(true);
		
		sendButton = new JButton("Send");
		getRootPane().setDefaultButton(sendButton);
		sendButton.addActionListener(sendButtonPressed);
        add(sendButton);
        
        textField = new JTextField();
        add(textField);
        
        textLabel = new JLabel("<html>");
        textLabel.setVerticalAlignment(JLabel.BOTTOM);
        textLabel.setFont(new Font("", Font.PLAIN, 18));
        scrollPane = new JScrollPane(textLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        add(scrollPane);
		
		addComponentListener(resizeListener);
		resizeListener.componentResized(null); // Calls the "function" from listener
	}
	
	public static String nextInput = "";
	public String awaitNextInput() {
		nextInput = "";
		while("".equals(nextInput)) {
			System.out.print("");
		}
		return nextInput;
	}
	
	public void update() { // Scroll to bottom
		//if(scrollPane.getVerticalScrollBar().getValue() + scrollPane.getVerticalScrollBar().getWidth() >= scrollPane.getVerticalScrollBar().getMaximum()*0.9) {
	        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		//}
		//print(Double.toString(scrollPane.getVerticalScrollBar().HEIGHT), Data.systemFont);
		//print(Integer.toString(scrollPane.getVerticalScrollBar().getValue()), Data.systemFont);
		//print(Integer.toString(scrollPane.getVerticalScrollBar().getMaximum()), Data.systemFont);
	}
	
	public void print(String text, String font) { // Print to clientwindow with given font
		textLabel.setText(textLabel.getText() + "<p style=" + font + ">" + text + "</p>");
		try {
			Thread.sleep(10);
			update();
		} catch (InterruptedException e) {}
	}
	
	ActionListener sendButtonPressed = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String input = textField.getText();
			if(!input.isEmpty()) {
				// send to client
				nextInput = input;
				textField.setText("");
			}
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
			
			textField.setBounds(7, windowH - textFieldSizeY - 44, textFieldSizeX, textFieldSizeY);
			sendButton.setBounds(textFieldSizeX + 10, windowH - textFieldSizeY - 44, sendButtonSizeX, sendButtonSizeY);
	        scrollPane.setBounds(7, 7, windowW - 27, windowH - textFieldSizeY - 55);
	        textLabel.setBounds(0, 0, scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth(), scrollPane.getHeight());
	        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
	};
}
