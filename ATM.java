import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ATM {

  private final int NORMAL = 1000;
  private final int INV_INPUT = 1001;
  private final int AUTH_FAIL = 1002;
  public boolean buttonSwitch = true;
  InputOperations inputOp = new InputOperations(); // Includes all GUI functions need for this ATM
  ATMHandler atmHandler = new ATMHandler(); // ActionListener of ATM
  JFrame theATMFrame = new JFrame(); // a JFrame to store the ATM JFrame, it will be called by different Transaction.
  JButton[] rbtn = {createButton("              "), createButton("              "),
      createButton("              "), createButton("              ")};
  // side buttons l = left and r = right of the ATM
  // Use array value instead of button text as all button text are the same
  JButton[] lbtn = {createButton("              "), createButton("              "),
      createButton("              "), createButton("              ")};
  JButton enter = createButton("ENT");
  // Different from Other Keys on the keypad, "ENT" and "CNL" performs different functions in different situation (controlled by stepCounter)
  // "CNL" = EXIT function after authentication success
  JButton cancel = createButton("CNL");
  Transaction temp;
  int accountNo = 0;
  int pin = 0;
  private CashDispenser cashDispenser;
  private BankDatabase bankDatabase;
  private String inputValue; // When "ENT" button is pressed, what user has typed on keypad will assign to inputValue
  private int currentAccountNo;

  public ATM(BankDatabase theBankDatabase, CashDispenser theCashDispenser) {
    bankDatabase = theBankDatabase;
    cashDispenser = theCashDispenser;
  }

  // create Button having atmHandler
  protected JButton createButton(String buttonText) {
    JButton btn = new JButton(buttonText);
    btn.setFocusable(false);
    btn.addActionListener(atmHandler);
    return btn;
  }

  public void run() {
    theATMFrame.dispose();
    inputOp.stepCounter = 0;
    currentAccountNo = 0;
    inputOp.setSideBtnArr(lbtn, rbtn);
    String[] t = {"", "", "Welcome to the ATM System", "Press ENT to continue."};
    theATMFrame = inputOp.mainFrame(enter, cancel, t, lbtn, rbtn);
    theATMFrame.toFront();
  }

  void enterAccountNo(int displayMode) {
    inputOp.stepCounter = 1;
    String[] t = {"", "Please enter your Account No.", "", ""};

    switch (displayMode) {
      case INV_INPUT: {
        // t[1] = "Invalid input";
        // t[2] = "Please re-enter your Account No.";
        t[2] = "Invalid input";
        t[3] = "Please try again";
        break;
      }
      case AUTH_FAIL: {
        // t[1] = "Authentication Failed.";
        // t[2] = "Please re-enter your Account No.";
        t[2] = "Authentication Failed";
        break;
      }
      default:
    }
    inputOp.displayScreen(t, true, false, false);
  }

  void enterPassword() {
    inputOp.stepCounter = 2;
    try {
      accountNo = Integer.parseInt(inputValue);
      String[] t = {"", "Please enter PIN Code", "", ""};
      inputOp.displayScreen(t, false, true, false);
    } catch (Exception e) {
      System.out.println("Fail to change Integer Value."); // Remove this line
      enterAccountNo(INV_INPUT);
    }
  }

  void authentication() {
    inputOp.stepCounter = 3;
    try {
      pin = Integer.parseInt(inputValue);
      if (bankDatabase.authenticateUser(accountNo, pin)) {
        currentAccountNo = accountNo;
        performTransactions();
      } else {
        System.out.println("Authentication Failed.");  // Remove this line
        enterAccountNo(AUTH_FAIL);
      }
    } catch (Exception e) {
      System.out.println("Fail to change Integer Value.");  // Remove this line
      enterAccountNo(INV_INPUT);
    }
  }

  void performTransactions() {  // show mainMenu
    inputOp.stepCounter = 4;
    inputOp.mainMenu(currentAccountNo);
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
              if (inputOp.stepCounter == 0) {
                enterAccountNo(NORMAL);
              } else if (inputOp.stepCounter == 1) {
                inputValue = inputOp.rText;
                inputOp.textField = null;
                enterPassword();
              } else if (inputOp.stepCounter == 2) {
                inputValue = inputOp.rText;
                inputOp.passwordField = null;
                authentication();
              }
              break;
            case "CNL":
              if (inputOp.stepCounter == 1 || inputOp.stepCounter == 2) { // Typing AccountNo OR PIN
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
                inputOp.displayScreen(cardReminder, false, false, false);
                theATMFrame.repaint();
                timer.setRepeats(false);
                timer.start();
                // Before Authentication success, it will back to Welcome Message if user press "CNL"
              } else if (inputOp.stepCounter >= 4) {
                String[] t = {"", "", "Sure to exit", ""};
                inputOp.displayOptionScreen(t, "Yes", "No");
                inputOp.stepCounter = 5;
              }
              break;
            case "              ":
              if (inputOp.stepCounter == 4) { // Main Menu
                if (e.getSource() == lbtn[0]) {
                  temp = new BalanceInquiry(currentAccountNo, bankDatabase, theATMFrame, inputOp);
                  inputOp.stepCounter = 11;
                  executeTransaction();
                } else if (e.getSource() == lbtn[1]) {
                  temp = new Withdrawal(currentAccountNo, bankDatabase, cashDispenser, theATMFrame,
                      inputOp);
                  inputOp.stepCounter = 21;
                  executeTransaction();
                } else if (e.getSource() == lbtn[2]) {
                  temp = new Transfer(currentAccountNo, bankDatabase, theATMFrame, inputOp);
                  inputOp.stepCounter = 31;
                  executeTransaction();
                } else if (e.getSource() == lbtn[3]) {
                  String[] t = {"", "", "Sure to exit", ""};
                  inputOp.displayOptionScreen(t, "Yes", "No");
                  inputOp.stepCounter++;
                }
              } else if (inputOp.stepCounter == 5) { // Confirm EXIT
                if (e.getSource() == lbtn[3]) {
                  // Reset step counter
                  inputOp.stepCounter = -1;
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
                  inputOp.displayScreen(cardReminder, false, false, false);
                  theATMFrame.repaint();
                  timer.setRepeats(false);
                  timer.start();
                } else if (e.getSource() == rbtn[3]) {
                  inputOp.mainMenu(currentAccountNo);
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
