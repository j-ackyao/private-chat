package pc;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
	JLabel background;
	ImageIcon backgroundImageIcon;
	
	JTextField textField;

	String prevText = "";
	
	JButton sendButton;
	
	JButton disconnectButton;
	
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
        
        
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(disconnect);
        
        textField = new JTextField();
        textField.addKeyListener(loadPrevText);
        add(textField);
        
        try {
        	 backgroundImageIcon = new ImageIcon(ImageIO.read(new File("image.jpg")));
        	 
        } catch (IOException ioe) {
        	
        }
        
        background = new JLabel();
        background.setIcon(backgroundImageIcon);
        background.setVerticalAlignment(JLabel.TOP);
        //add(background);
        
        textLabel = new JLabel("<html>");
        textLabel.setVerticalAlignment(JLabel.BOTTOM);
        textLabel.setFont(new Font("", Font.PLAIN, 18));
        //textLabel.setIcon(backgroundImageIcon);
        
        scrollPane = new JScrollPane(textLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.add(background);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        add(scrollPane);
        
		addComponentListener(resizeListener);
		resizeListener.componentResized(null); // Calls the "function" from listener
		addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {new Thread(()-> {Client.exit("safe");}).start();} // dont worry about this
		});
	}
	
	public String nextInput = "";
	public String awaitNextInput() {
		nextInput = "";
		while("".equals(nextInput)) {
			System.out.print("");
		}
		return nextInput;
	}
	
	public void update() {
		//scroll to bottom
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		
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
				prevText = input;
				textField.setText("");
			}
		}
	};
	
	KeyAdapter loadPrevText = new KeyAdapter() {
	    public void keyPressed(KeyEvent e) {
	    	switch(e.getKeyCode()) {
	    	case KeyEvent.VK_UP:
	    		// load prev text
	        	textField.setText(prevText);
	        	break;
	    	case KeyEvent.VK_DOWN:
	    		textField.setText("");
	    		break;
	    	}
	    }
	};
	
	ActionListener disconnect = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Client.exit("restart");
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
			
			textField.setBounds(7, windowH - textFieldSizeY - 52, textFieldSizeX, textFieldSizeY);
			background.setBounds(0, 0, windowW, windowH);
			//background.setIcon(new ImageIcon(backgroundImage.getScaledInstance(windowW, windowH, Image.SCALE_FAST)));
			sendButton.setBounds(textField.getX() + textField.getWidth() + 3, windowH - textFieldSizeY - 52, sendButtonSizeX, textFieldSizeY);
			disconnectButton.setBounds(windowW - 7, sendButton.getY(), (int) (sendButtonSizeX * 0.5f), textFieldSizeY);
	        scrollPane.setBounds(7, 7, windowW - 27, windowH - textFieldSizeY - 64);
	        textLabel.setBounds(0, 0, scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth(), scrollPane.getHeight());
	        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
	};
}
