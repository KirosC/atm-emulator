import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.Timer;

public class Withdrawal extends Transaction {
	private int amount;
	private String inputValue;
	private CashDispenser cashDispenser;
	private WithdrawalHandler withdrawalHandler = new WithdrawalHandler();
	JButton[] lbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	JButton[] rbtns = { createButton("              "), createButton("              "), createButton("              "), createButton("              ") };
	private boolean buttonSwitch = true;

	public Withdrawal(int userAccountNumber, BankDatabase atmBankDatabase, CashDispenser atmCashDispenser,
			JFrame theMainMenu, InputOperations a) {
		super(userAccountNumber, atmBankDatabase, theMainMenu, a);
		cashDispenser = atmCashDispenser;
	}

	protected JButton createButton(String buttonText) {
		JButton btn = new JButton(buttonText);
		btn.setFocusable(false);
		btn.addActionListener(withdrawalHandler);
		return btn;
	}

	public void execute() {
		a.stepCounter = 21;
		a.sideButton(lbtns, true);
		a.sideButton(rbtns, false);
		a.setENThandler(withdrawalHandler);
		dispenseAmount();
	}

	void dispenseAmount() {
		a.stepCounter = 22;
		String[] reqOnAmt = { "", "Amount to withdraw", "", "" };
		a.displayScreen(reqOnAmt, true, false);
	}

	void confirmMessage() {
		a.stepCounter = 23;
		try {
			amount = Integer.parseInt(inputValue);
			// Compare the withdrawal amount with balance
			if (amount > super.getBankDatabase().getAvailableBalance(getAccountNumber())) {
				String[] transactionCnl = {"", "", "Insufficient Balance.", "Transcation Cancelled."};
				toMainMenu(transactionCnl);
			}
			else if (!cashDispenser.isSufficientCashAvailable(amount)){
				String[] transactionCnl = {"", "Insufficient Bill To Dispense.", "Please try a lower amount or use another ATM.", "", "Transcation Cancelled."};
				toMainMenu(transactionCnl);
			}else if (amount % 100 == 0) {
				String[] details = {" ", " ", "Withdraw $ " + amount, "Sure ?"};
				a.displayOptionScreen(details, "No", "Yes");
			} else {
				String[] transactionCnl = {"", "", "Indispensible amount.", "Transcation Cancelled."};
				toMainMenu(transactionCnl);
			}
		} catch (Exception e) {
			String[] transactionCnl = {"", "", "Invalid Amount Input.", "Transcation Cancelled."};
			toMainMenu(transactionCnl);
		}
	}

	void dispense(){
		// Reset step counter
		a.stepCounter = -1;
		// Set delay for dispensing cash
		Timer timer = new Timer(3000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cashDispenser.dispenseCash(amount);
				String[] transactionCnl = { "", "Withdrawed $ " + amount, "Please take your money.","Exiting ..."};
				terminate(transactionCnl);
			}
		});

		// Debit the account balance and eject the card
		getBankDatabase().debit(getAccountNumber(), amount);
		String[] cardReminder = {"", "", "Please take your card first.", ""};
		theATMFrame.repaint();
		a.displayScreen(cardReminder, false, false);
		timer.setRepeats(false);
		timer.start();
	}

	void toMainMenu(String[] msg) {
		Timer t= new Timer(3500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	toMainMenu();
            }
        });
		theATMFrame.repaint();
		a.displayScreen(msg, false, false);
		
		t.setRepeats(false);
		t.start();	
	}
	
	void toMainMenu() {
		a.removeENThandler(withdrawalHandler);
		theATMFrame.repaint();
		a.mainMenu(getAccountNumber());
	}
	
	void terminate(String[] msg) {
		Timer t= new Timer(4000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	terminate();
            }
        });
		
		theATMFrame.repaint();
		a.displayScreen(msg, false, false);
		
		t.setRepeats(false);
		t.start();	
	}
	
	void terminate() {
		a.removeENThandler(withdrawalHandler);
		theATMFrame.repaint();
		ATMCaseStudy.main(null);
	}

	public class WithdrawalHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (buttonSwitch) {
				Object source = e.getSource();
				if (source instanceof JButton) {
          JButton btn = (JButton) source;
          try {
            switch (btn.getText()) {
            case "ENT":
              if (a.stepCounter == 22) {
                inputValue = a.rText;
                confirmMessage();
              }
            case "              ":
              if (a.stepCounter == 5) { // Confirm EXIT
                if (e.getSource() == lbtns[3]) {
                  theATMFrame.dispose();
                  ATMCaseStudy.main(null);
                } else if (e.getSource() == rbtns[3]) {
                  theATMFrame.repaint();
                  a.mainMenu(getAccountNumber());
                }
              }
              if (a.stepCounter == 23) {
                if (e.getSource() == lbtns[3]) {
                  toMainMenu();
                } else if (e.getSource() == rbtns[3]) {
                  dispense();
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
}
