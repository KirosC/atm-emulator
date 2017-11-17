public class ATMCaseStudy {
	static BankDatabase a = new BankDatabase();
	static CashDispenser b = new CashDispenser();
	static ATM theATM = new ATM(a,b);
		public static void main(String[] args) {			
			theATM.run();
		}
}
