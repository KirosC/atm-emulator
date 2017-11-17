public class CurrentAccount extends Account 
{ 
    private int overdrawnLimit = 10000;    //  Default value of HKD10000 overdrawn limit 
  
    // constructor BEGIN 
    public CurrentAccount( int theAccountNo, int thePIN, double theAvailBal, double theTotalBal ) 
    { 
        super( theAccountNo, thePIN, theAvailBal, theTotalBal); 
    } 
  
    public CurrentAccount( int theAccountNo, int thePIN, double theAvailBal, double theTotalBal , int theOverdrawnLimit) 
    { 
        super( theAccountNo, thePIN, theAvailBal, theTotalBal); 
        overdrawnLimit = theOverdrawnLimit;    //  Value assigned by user 
    } 
    // constructor END 
    
  
    public int getOverdrawnLimit() 
    { 
        return overdrawnLimit; 
    } 
     
    public void setOverdrawnLimit( int theOverdrawnLimit) 
    { 
        overdrawnLimit = theOverdrawnLimit; 
    } 
    
    // new
    @Override
    public String getAccountType() {
    	return "Current Account";
    }
} 