
/**
 * 
 * @author Gabriel Horth
 * @version 1.0
 *
 */
class CSVAttributeMissing extends Exception{
 
  private static final long serialVersionUID = 8564294055190543227L;
  
  private String message;
  
  public CSVAttributeMissing() {
	fillInStackTrace();
	message = "Error: Input row cannot be parsed due to missing information";
  }
 
  public CSVAttributeMissing(String message) {
	fillInStackTrace();
	this.message = message;
  }
  
  @Override
  public String getMessage() {
	return message;
	
  }
}
