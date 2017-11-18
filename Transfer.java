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
		String[] reqOnTAccount = { "Transfer Account No.", "", "", "" };
		a.displayScreen(reqOnTAccount, true, false);
	}

	private void checkAcNo() {
		
		a.stepCounter = 33;
		try {
			receivingAccount = Integer.parseInt(inputValue);
			if (receivingAccount == userAccNum || !bankDatabase.checkAccountNum(receivingAccount)) {
				if (receivingAccount == userAccNum) {
					String[] transcationCnl = { "", "", "Account In Use.", "Transcation Cancelled." };
					toMainMenu(transcationCnl);
				} else {
					String[] transcationCnl = { "", "", "Account Not Found.", "Transcation Cancelled." };
					toMainMenu(transcationCnl);
				} // When user inputed something invalid
			} else {
				inputTransferAmt();
			}
		} catch (Exception e) {
			String[] transcationCnl = { "", "", "Invalid Account Input.", "Transcation Cancelled." };
			toMainMenu(transcationCnl);			
		}
	}

	private void inputTransferAmt() {
		a.stepCounter = 34;
		String[] reqOnTAmt = { "Transfer Amount", "", "", "" };
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
					String[] transcationCnl = { "", "", "Invalid Decimal Input.", "Transcation Cancelled." };
					toMainMenu(transcationCnl);	
				} else {
					// in case user input amount that is larger than its account balance
					String[] transcationCnl = { "", "", "Insufficient balance.", "Transcation Cancelled." };
					toMainMenu(transcationCnl);	
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
			String[] transcationCnl = { "", "", "Invalid Amount Input.", "Transcation Cancelled." };
			toMainMenu(transcationCnl);	
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
			String[] transcationS = { "", "",  "Transcation Success." ,"Exiting ..."};
			terminate(transcationS);	
		} else {
			String[] transcationF = { "", "", "Transcation Failed." , ""};
			terminate(transcationF);		
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
		Timer t= new Timer(5000, new ActionListener() {
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
								a.removeENThandler(transferHandler);
								terminate(); // Yes
							} else if (e.getSource() == rbtns[3]) {
								toMainMenu(); // No
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
