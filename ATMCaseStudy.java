public class ATMCaseStudy {

  static BankDatabase bankDatabase = new BankDatabase();
  static CashDispenser cashDispenser = new CashDispenser();
  static ATM theATM = new ATM(bankDatabase, cashDispenser);

  public static void main(String[] args) {
    theATM.run();
  }
}
