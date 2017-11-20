import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ATM {
	private CashDispenser cashDispenser;
	private BankDatabase bankDatabase;
	private String inputValue; // When "ENT" button is pressed, what user has typed on keypad will assign to inputValue
	InputOperations a = new InputOperations(); // Includes all GUI functions need for this ATM
	ATMHandler atmHandler = new ATMHandler(); // ActionListener of ATM

	JFrame theATMFrame = new JFrame(); // a JFrame to store the ATM JFrame, it will be called by different Transaction. 
	JButton[] rbtn = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	JButton[] lbtn = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	// side buttons l = left and r = right of the ATM
	// Use array value instead of button text as all button text are the same

	JButton enter = createButton("ENT");
	JButton cancel = createButton("CNL");
	// Different from Other Keys on the keypad, "ENT" and "CNL" performs different functions in different situation (controlled by stepCounter)
	// "CNL" = EXIT function after authentication success

	Transaction temp;

	private int currentAccountNo;
	private final int NORMAL = 1000;
	private final int INV_INPUT = 1001;
	private final int AUTH_FAIL = 1002;
	int accountNo = 0;
	int pin = 0;
	public boolean buttonSwitch = true;

	// create Button having atmHandler
	protected JButton createButton(String buttonText) {
		JButton btn = new JButton(buttonText);
		btn.setFocusable(false);
		btn.addActionListener(atmHandler);
		return btn;
	}

	public ATM(BankDatabase theBankDatabase, CashDispenser theCashDispenser) {
		bankDatabase = theBankDatabase;
		cashDispenser = theCashDispenser;
	}

	public void run() {
		theATMFrame.dispose();
		a.stepCounter = 0;
		currentAccountNo = 0;
		a.setSideBtnArr(lbtn, rbtn);
		String[] t = { "","","Welcome to the ATM System", "Press ENT to continue." };
		theATMFrame = a.mainFrame(enter, cancel, t, lbtn, rbtn);
		theATMFrame.toFront();
	}

	void enterAccountNo(int displayMode) {
		a.stepCounter = 1;
		String[] t = { "","Please enter your Account No.", "", "" };

		switch (displayMode){
			case INV_INPUT:{
				// t[1] = "Invalid input";
				// t[2] = "Please re-enter your Account No.";
				t[2] = "Invalid input";
				t[3] = "Please try again";
				break;
			}
			case AUTH_FAIL:{
				// t[1] = "Authentication Failed.";
				// t[2] = "Please re-enter your Account No.";
				t[2] = "Authentication Failed";
				break;
			}
			default:
		}
		a.displayScreen(t, true, false);
	}

	void enterPassword() {
		// allow retry ?
		a.stepCounter = 2;
		try {
			accountNo = Integer.parseInt(inputValue);
			String[] t = { "","Please enter PIN Code", "", "" };
			a.displayScreen(t, false, true);
		} catch (Exception e) {
			System.out.println("Fail to change Integer Value.");
			enterAccountNo(INV_INPUT);
		}
	}

	void authentication() {
		a.stepCounter = 3;
		try {
			pin = Integer.parseInt(inputValue);
			if (bankDatabase.authenticateUser(accountNo, pin)) {
				currentAccountNo = accountNo;
				performTransactions();
			} else {
				System.out.println("Authentication Failed.");
				enterAccountNo(AUTH_FAIL);
			}
		} catch (Exception e) {
			System.out.println("Fail to change Integer Value.");
			enterAccountNo(INV_INPUT);
		}
	}

	void performTransactions() {	// show mainMenu
		a.stepCounter = 4;
		a.mainMenu(currentAccountNo);
	}

	void executeTransaction() {
		temp.execute();
	}

	public class ATMHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source instanceof JButton) {
          JButton btn = (JButton) source;
          try {
            switch (btn.getText()) {
            case "ENT":
              if (a.stepCounter == 0) {
                enterAccountNo(NORMAL);
              } else if (a.stepCounter == 1) {
                inputValue = a.rText;
                a.textField = null;
                enterPassword();
              } else if (a.stepCounter == 2) {
                inputValue = a.rText;
                a.passwordField = null;
                authentication();
              }
              break;
            case "CNL":
              if (a.stepCounter == 1 || a.stepCounter == 2) { // Typing AccountNo OR PIN
								currentAccountNo = 0;
								Timer timer = new Timer(2000, new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										theATMFrame.dispose();
										run();
										theATMFrame.toFront();
									}
								});
								String[] cardReminder = {"", "", "Please take your card.", ""};
								a.displayScreen(cardReminder, false, false);
								theATMFrame.repaint();
								timer.setRepeats(false);
								timer.start();
                // Before Authentication success, it will back to Welcome Message if user press "CNL"
              } else if (a.stepCounter >= 4) {
                String[] t = { "", "", "Sure to exit", "" };
                a.displayOptionScreen(t, "Yes", "No");
                a.stepCounter = 5;
              }
              break;
            case "              ":
              if (a.stepCounter == 4) { // Main Menu
                if (e.getSource() == lbtn[0]) {
                  temp = new BalanceInquiry(currentAccountNo, bankDatabase, theATMFrame, a);
                  a.stepCounter = 11;
                  executeTransaction();
                } else if (e.getSource() == lbtn[1]) {
                  temp = new Withdrawal(currentAccountNo, bankDatabase, cashDispenser, theATMFrame,a);
                  a.stepCounter = 21;
                  executeTransaction();
                } else if (e.getSource() == lbtn[2]) {
                  temp = new Transfer(currentAccountNo, bankDatabase, theATMFrame, a);
                  a.stepCounter = 31;
                  executeTransaction();
                } else if (e.getSource() == lbtn[3]) {
                  String[] t = { "", "", "Sure to exit", "" };
                  a.displayOptionScreen(t, "Yes", "No");
                  a.stepCounter++;
                }
              } else if (a.stepCounter == 5) { // Confirm EXIT
                if (e.getSource() == lbtn[3]) {
									// Reset step counter
									a.stepCounter = -1;
                  currentAccountNo = 0;
									Timer timer = new Timer(2000, new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											theATMFrame.dispose();
											run();
											theATMFrame.toFront();
										}
									});
									String[] cardReminder = {"", "", "Please take your card.", ""};
									a.displayScreen(cardReminder, false, false);
									theATMFrame.repaint();
									timer.setRepeats(false);
									timer.start();
                } else if (e.getSource() == rbtn[3]) {
                  a.mainMenu(currentAccountNo);
                }
              }
              break;
            }
          } catch (Exception exp) {
            exp.printStackTrace();
          }
        }
		}
	}
}
