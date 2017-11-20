import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class InputOperations extends JPanel {
	private ActionHandler actionHandler;
	JTextField textField;
	JPasswordField passwordField;
	JFrame a;
	JPanel screen = new JPanel();
	JPanel leftBtns = new JPanel();
	JPanel rightBtns = new JPanel();
	JButton[] left;
	JButton[] right;
	JButton ent;

	public String rText;
	public int stepCounter;
	// 0-10 : ATM
	// 11-20: BALANCE INQUIRY
	// 21-30: WITHDRAWAL
	// 31-40: TRANSFER



	public JFrame mainFrame(JButton enter, JButton cancel, String[] displayLine, JButton[] l, JButton[] r) {
		a = new JFrame("the ATM");
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setLayout(new BorderLayout(2, 2));
		leftBtns = sideButton(l, true);
		rightBtns = sideButton(r, false);
		a.add(leftBtns, BorderLayout.WEST);
		a.add(rightBtns, BorderLayout.EAST);

		screen = displayScreen(displayLine, false, false);
		a.add(screen, BorderLayout.CENTER);
		ent = enter;

		// Get screen ressolution
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();

		// Set the width and the height to one-third and 80% base on the monitor resolution respectively
		a.setSize((int) (screenWidth / 3), (int) (screenHeight * 0.8));
		a.setLocationRelativeTo(null);
		a.setVisible(true);
		a.setResizable(false);

		// Set a panel to reserve the size for the keypad, the size is one-third of the main frame
		JPanel keypadPanel = new JPanel();
		keypadPanel.setPreferredSize(new Dimension(a.getWidth(),a.getHeight()/3));
		keypadPanel.setLayout(new BorderLayout());
		keypadPanel.add(keypad(ent, cancel), BorderLayout.CENTER);
		a.add(keypadPanel, BorderLayout.SOUTH);

		return a;
	}

	public JPanel sideButton(JButton[] sideBtn, boolean isLeft) {
		if (isLeft) {
			leftBtns.removeAll();
			leftBtns.setLayout(new GridLayout(8, 1, 2, 2));
			for (int i = 1; i <= 3; i++)
				leftBtns.add(new JLabel());
			for (int j = 0; j < 4; j++)
				leftBtns.add(sideBtn[j]);
			return leftBtns;
		}
		rightBtns.removeAll();
		rightBtns.setLayout(new GridLayout(8, 1, 2, 2));
		for (int i = 1; i <= 3; i++)
			rightBtns.add(new JLabel());
		for (int j = 0; j < 4; j++)
			rightBtns.add(sideBtn[j]);
		return rightBtns;
	}

	public void setSideBtnArr(JButton[] LBtn, JButton[] RBtn) {
		left = LBtn;
		right = RBtn;
	}

	public void setENThandler(ActionListener handler) {
		ent.addActionListener(handler);
	}

	public void removeENThandler(ActionListener handler) {
		ent.removeActionListener(handler);
	}
	// Remove
	// before execute transaction : atmhandler
	// transaction end : ActionHandler of that transaction

	// screen to show plain message, or message with input field
	public JPanel displayScreen(String[] displayLine, boolean haveTextField,
			boolean havePasswordField) {
		screen.removeAll();

		screen.setLayout(new GridLayout(8, 1, 2, 2));
		screen.add(new JLabel(" "));
		screen.add(new JLabel(" "));
		for (int i = 0; i < displayLine.length; i++) {  // Total no. of message line avaliable = 5
			JLabel log = new JLabel(displayLine[i]);
			screen.add(log);
			// Setup for Leave button
			if (displayLine[i] != "Leave  "
					&& displayLine[i] != "  $100                                    $300  "
					&& displayLine[i] != "  $500                                   $1000  ") {
				log.setHorizontalAlignment((int) CENTER_ALIGNMENT);
			} else {
				log.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			log.setFont(new Font("Consolas", Font.PLAIN, 18));
		}

		if (haveTextField) {
			textField = new JTextField();
			textField.setText("");
			textField.setEditable(false); // block keyboard input
			textField.requestFocusInWindow();
			textField.setHorizontalAlignment(JTextField.RIGHT);
			// Set font type to "Consolas", font style to "BOLD", font size to "30"
			textField.setFont(new Font("Consolas", Font.BOLD, 30));
			screen.add(textField);
		} else if (havePasswordField) {
			passwordField = new JPasswordField();
			passwordField.setText(null);
			passwordField.setEditable(false);
			passwordField.setEchoChar('*'); // character to hide password content
			passwordField.requestFocusInWindow();
			passwordField.setHorizontalAlignment(JPasswordField.RIGHT);
			// Set font type to "Consolas", font style to "BOLD", font size to "30"
			passwordField.setFont(new Font("Consolas", Font.BOLD, 30));
			screen.add(passwordField);
		}
		return screen;
	}

	// Main Menu diplay details
	public JPanel mainMenu(int accountNo) {
		stepCounter = 4;
		String[] displayLine = { "Welcome! " + accountNo, "Please choose a service.", " - VIEW MY BALANCE       ",
				" - WITHDRAW CASH         ", " - TRANSFER MONEY        ", " - EXIT                  " };
		screen.removeAll();
		screen.setLayout(new GridLayout(8, 1, 2, 2));
		screen.add(new JLabel());
		JLabel fl = new JLabel(displayLine[0]);
		JLabel sl = new JLabel(displayLine[1]);
		screen.add(fl);
		screen.add(sl);
		fl.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		fl.setFont(new Font("Consolas", Font.PLAIN, 18));
		sl.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		sl.setFont(new Font("Consolas", Font.PLAIN, 18));
		for (int i = 2; i < displayLine.length; i++) {
			JLabel log = new JLabel(displayLine[i]);
			screen.add(log);
			log.setFont(new Font("Consolas", Font.PLAIN, 18));
		}
		sideButton(left, true);
		sideButton(right, false);
		return screen;
	}

	// Screen to show confirm message
	public JPanel displayOptionScreen(String[] displayLine, String leftOption, String rightOption) {
		screen.removeAll();

		screen.setLayout(new GridLayout(8, 1, 2, 2));
		screen.add(new JLabel());
		screen.add(new JLabel());
		for (int i = 0; i < displayLine.length; i++) {
			JLabel log = new JLabel(displayLine[i]);
			screen.add(log);
			log.setHorizontalAlignment((int) CENTER_ALIGNMENT);
			log.setFont(new Font("Consolas", Font.PLAIN, 18));
		}
		JPanel option = new JPanel();
		option.setLayout(new GridLayout(1, 5));
		JLabel left = new JLabel(leftOption);
		JLabel right = new JLabel(rightOption);
		option.add(left);
		for (int i = 1; i <= 3; i++)
			option.add(new JLabel());
		option.add(right);
		left.setFont(new Font("Consolas", Font.PLAIN, 18));
		right.setFont(new Font("Consolas", Font.PLAIN, 18));
		left.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		right.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		screen.add(option);
		screen.repaint();
		return screen;
	}

	public JPanel keypad(JButton enter, JButton cancel) {
		JPanel keypad = new JPanel();
		keypad.setLayout(new GridLayout(4, 4, 2, 2));
		actionHandler = new ActionHandler();
		JButton clear = createButton("CLR");
		// Set font type to "Consolas", font style to "BOLD", font size to "24"
		clear.setFont(new Font("Consolas", Font.BOLD, 24));
		enter.setFont(new Font("Consolas", Font.BOLD, 24));
		cancel.setFont(new Font("Consolas", Font.BOLD, 24));
		clear.setBackground(Color.yellow);
		enter.setBackground(Color.green);
		cancel.setBackground(Color.red);
		for (int number = 1; number < 10; number++) {
			keypad.add(createButton(String.valueOf(number)));
			switch (number) {
				case 3:
					keypad.add(clear);
					break;
				case 6:
					keypad.add(cancel);
					break;
				case 9:
					keypad.add(new JLabel(" "));
					break;
			}
		}
		keypad.add(createButton("."));
		keypad.add(createButton("0"));
		keypad.add(createButton("00"));
		keypad.add(enter);
		return keypad;
	}

	public JButton createButton(String buttonText) {
		JButton btn = new JButton(buttonText);
		btn.setFocusable(false);
		btn.addActionListener(actionHandler);
		btn.setBackground(Color.LIGHT_GRAY);
		// Set font type to "Consolas", font style to "BOLD", font size to "24"
		btn.setFont(new Font("Consolas", Font.BOLD, 24));
		return btn;
	}

	public class ActionHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			JButton btn = (JButton) source;
			try {
				switch (btn.getText()) { // get button text
					case "0":
					case "1":
					case "2":
					case "3":
					case "4":
					case "5":
					case "6":
					case "7":
					case "8":
					case "9":
					case "00":
					case ".":
						String value = btn.getText();
						if (textField != null) {
							if (value.equals(".") && textField.getText().indexOf(".") != -1) { // more than one decimal point
								textField.setText(textField.getText());
							} else {
								textField.setText(textField.getText() + value);
							}
							rText = textField.getText();
						} else if (passwordField != null) {
							char[] input = passwordField.getPassword();
							passwordField.setText(String.valueOf(input) + value);
							rText = String.valueOf(passwordField.getPassword());
						}
						break;
					case "CLR": // clear value
						if (textField != null) {
							textField.setText("");
						} else if (passwordField != null) {
							passwordField.setText("");
						}
						rText = "";
						break;
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	}
}



