
// BalanceInquiry.java
// Represents a balance inquiry ATM transaction
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class BalanceInquiry extends Transaction {
	BalInqHandler biHandler = new BalInqHandler();
	JButton[] lbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	JButton[] rbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };

	// BalanceInquiry constructor
	public BalanceInquiry(int userAccountNumber, BankDatabase atmBankDatabase, JFrame theATMFrame, InputOperations a) {
		super(userAccountNumber, atmBankDatabase, theATMFrame, a);
	} // end BalanceInquiry constructor

	protected JButton createButton(String buttonText) {
		JButton btn = new JButton(buttonText);
		btn.setFocusable(false);
		btn.addActionListener(biHandler);
		return btn;
	}

	// performs the transaction
	public void execute() {
		a.stepCounter = 11;
		// get references to bank database and screen
		BankDatabase bankDatabase = getBankDatabase();

		// Get Account Type

		// get the available balance for the account involved
		double availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

		// get the total balance for the account involved
		double totalBalance = bankDatabase.getTotalBalance(getAccountNumber());

		String[] message = { "Balance Information:", bankDatabase.getAccType(getAccountNumber())+" - "+getAccountNumber(), "Available Balance: " + Double.toString(availableBalance),
				"Total Balance:     " + Double.toString(totalBalance) };
		a.displayOptionScreen(message, " ", "Leave");
		a.sideButton(lbtns, true);
		a.sideButton(rbtns, false);

	} // end method execute

	public class BalInqHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (a.stepCounter != 5&&e.getSource() == rbtns[3]) {
				theATMFrame.repaint();
				a.mainMenu(getAccountNumber());
			}
			if (a.stepCounter == 5) { // Confirm EXIT
				if (e.getSource() == lbtns[3]) {
					Timer timer = new Timer(2000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							theATMFrame.dispose();
							ATMCaseStudy.main(null);
						}
					});
					String[] cardReminder = {"", "", "Please take your card.", ""};
					a.displayScreen(cardReminder, false, false, false);
					theATMFrame.repaint();
					timer.setRepeats(false);
					timer.start();
				} else if (e.getSource() == rbtns[3]) {
					theATMFrame.repaint();
					a.mainMenu(getAccountNumber());
				}
			}
		}
	}

} // end class BalanceInquiry

/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and * Pearson Education,
 * Inc. All Rights Reserved. * * DISCLAIMER: The authors and publisher of this
 * book have used their * best efforts in preparing the book. These efforts
 * include the * development, research, and testing of the theories and programs
 * * to determine their effectiveness. The authors and publisher make * no
 * warranty of any kind, expressed or implied, with regard to these * programs
 * or to the documentation contained in these books. The authors * and publisher
 * shall not be liable in any event for incidental or * consequential damages in
 * connection with, or arising out of, the * furnishing, performance, or use of
 * these programs. *
 *************************************************************************/