public class SavingAccount extends Account {

  public SavingAccount(int theAccountNumber, int thePIN, double theAvailBal, double theTotalBal) {
    super(theAccountNumber, thePIN, theAvailBal, theTotalBal);
  }

  // Default interest rate 0.1% per annum
  private static final double INTEREST_RATE = 0.001;
  
  
  
  // new
  @Override
  public String getAccountType() {
	  return "Saving Account";
  }
}