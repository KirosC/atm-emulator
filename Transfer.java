import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.Timer;

public class Transfer extends Transaction {
	private int userAccNum;
	private double transferAmount = 0;
	private int receivingAccount = 0;
	private String inputValue;

	private TransferHandler transferHandler = new TransferHandler(); // ActionListener of Transfer
	private BankDatabase bankDatabase = getBankDatabase();
	JButton[] lbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	JButton[] rbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };

	public Transfer(int userAccountNumber, BankDatabase atmBankDatabase, JFrame theATMFrame, InputOperations b) {
		super(userAccountNumber, atmBankDatabase, theATMFrame, b);
		userAccNum = userAccountNumber;
	}

	protected JButton createButton(String buttonText) {
		JButton btn = new JButton(buttonText);
		btn.setFocusable(false);
		btn.addActionListener(transferHandler);
		return btn;
	}

	public void execute() {
		a.stepCounter = 31;
		a.sideButton(lbtns, true);
		a.sideButton(rbtns, false);
		a.setENThandler(transferHandler);
		inputTransferAccountNo();

	}

	private void inputTransferAccountNo() {
		a.stepCounter = 32;
		String[] reqOnTAccount = { "Transfer Account No.", "", "", "Leave  " };
		a.displayScreen(reqOnTAccount, true, false);
	}

	private void checkAcNo() {
		
		a.stepCounter = 33;
		try {
			receivingAccount = Integer.parseInt(inputValue);
			if (receivingAccount == userAccNum || !bankDatabase.checkAccountNum(receivingAccount)) {
				if (receivingAccount == userAccNum) {
					String[] transactionCnl = { "", "", "Account In Use.", "Transaction Cancelled." };
					toMainMenu(transactionCnl);
				} else {
					String[] transactionCnl = { "", "", "Account Not Found.", "Transaction Cancelled." };
					toMainMenu(transactionCnl);
				} // When user inputed something invalid
			} else {
				inputTransferAmt();
			}
		} catch (Exception e) {
			String[] transactionCnl = { "", "", "Invalid Account Input.", "Transaction Cancelled." };
			toMainMenu(transactionCnl);
		}
	}

	private void inputTransferAmt() {
		a.stepCounter = 34;
		String[] reqOnTAmt = { "Transfer Amount", "", "", "Leave  " };
		a.displayScreen(reqOnTAmt, true, false);
	}

	void checkTransferStatus() {
		a.stepCounter = 35;
		try {
			transferAmount = Double.parseDouble(inputValue);
			if (transferAmount * 100 - (int) (transferAmount * 100) != 0
					|| transferAmount > bankDatabase.getTotalBalance(userAccNum)) {
				if (transferAmount * 100 - (int) (transferAmount * 100) != 0) {
					// in case user input amount with 3 or more decimal place e.g. $1.521
					String[] transactionCnl = { "", "", "Invalid Decimal Input.", "Transaction Cancelled." };
					toMainMenu(transactionCnl);
				} else {
					// in case user input amount that is larger than its account balance
					String[] transactionCnl = { "", "", "Insufficient Balance.", "Transaction Cancelled." };
					toMainMenu(transactionCnl);
				}
			} else {
				// Confirm Transfer Details
				a.stepCounter = 36;
				String[] details = { " ", "Transfer FROM: " + Integer.toString(userAccNum),
						"Transfer TO:   " + Integer.toString(receivingAccount),
						"Amount:     " + "$ " + Double.toString(transferAmount) + "0" };
				a.displayOptionScreen(details, "Back", "Next");
			}
		} catch (Exception e) {
			String[] transactionCnl = { "", "", "Invalid Amount Input.", "Transaction Cancelled." };
			toMainMenu(transactionCnl);
		}
	}

	void performTransfer() {
		a.stepCounter = 37;
		bankDatabase.debit(userAccNum, transferAmount);
		bankDatabase.credit(receivingAccount, transferAmount);
		// end of transferring the money
		// check if the transfer is successful or not
		boolean transferSuccess = true; // Assuming the transfer wont fail
		if (transferSuccess) {
			String[] TransactionS = { "", "",  "Transaction Success." ,"Exiting ..."};
			terminate(TransactionS);
		} else {
			String[] TransactionF = { "", "", "Transaction Failed." , ""};
			terminate(TransactionF);
		} // end of checking
	}

	// Show message for a while and back to main menu
	void toMainMenu(String[] msg) {
		Timer t= new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	a.removeENThandler(transferHandler); // This row can delete - same line exist in toMainMenu()
            	toMainMenu();
            }
        });
		theATMFrame.repaint();
		a.displayScreen(msg, false, false);
		
		
		t.setRepeats(false);
		t.start();	
	}
	
	// back to main menu now
	void toMainMenu() {
		a.removeENThandler(transferHandler);
		theATMFrame.repaint();
		a.mainMenu(getAccountNumber());
	}
	
	// Show message for a while and terminate
	void terminate(String[] msg) {
		Timer t= new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	terminate();
            }
        });
		
		theATMFrame.repaint();
		a.displayScreen(msg, false, false);
		
		t.setRepeats(false);
		t.start();	
	}
	
	// termianate now
	void terminate() {
		a.removeENThandler(transferHandler);
		theATMFrame.repaint();
		ATMCaseStudy.main(null);
	}

	public class TransferHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof JButton) {
				JButton btn = (JButton) source;
				try {
					switch (btn.getText()) {
					case "ENT":			
						System.out.println(a.stepCounter);
						if (a.stepCounter == 32) { // InputTransferAccountNumber
							inputValue = a.rText;
							checkAcNo();
						} else if (a.stepCounter == 34) { // InputTransferAmt
							inputValue = a.rText;
							checkTransferStatus();
						}
						break;
					case "              ":
						if (a.stepCounter == 5) { // Confirm EXIT
							if (e.getSource() == lbtns[3]) {
								Timer timer = new Timer(2000, new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										a.removeENThandler(transferHandler);
										terminate(); // Yes
									}
								});
								String[] cardReminder = {"", "", "Please take your card first.", ""};
								a.displayScreen(cardReminder, false, false);
								theATMFrame.repaint();
								timer.setRepeats(false);
								timer.start();
							} else if (e.getSource() == rbtns[3]) {
								toMainMenu(); // No
							}
						}
						if(a.stepCounter == 32 || a.stepCounter == 34) {
							if (e.getSource() == rbtns[2]) { // Return main menu from Transfer
								String[] transactionCnl = { "", "", "Transaction Cancelled.", "" };
								toMainMenu(transactionCnl);
							}
						}
						if(a.stepCounter == 36) { // checkTransferStatus
							if (e.getSource() == lbtns[3]) {
								toMainMenu(); // "Back"
							}else if (e.getSource() == rbtns[3]) {
								performTransfer();  // "Next"
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
