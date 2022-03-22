//-----------------------------------------------------
//Assignment: 3
//Question: CSVAttributeMissing Class
//Written by: Gabriel Horth, 40186942
//-----------------------------------------------------

/**
 * Custom Exception for CSV2HTML program.
 * @author Gabriel Horth
 * @version 1.0
 * @see CSV2HTML
 *
 */
class CSVAttributeMissing extends Exception{
 
  private static final long serialVersionUID = 8564294055190543227L;
  
  private String message;
  
  /**
   * Default constructor with default message.
   */
  public CSVAttributeMissing() {
	fillInStackTrace();
	message = "Error: Input row cannot be parsed due to missing information";
  }
 
  /**
   * Constructor with String supply a custom error message.
   * @param message String to be returned by Throwable.getMessage()
   */
  public CSVAttributeMissing(String message) {
	fillInStackTrace();
	this.message = message;
  }
  
  /**
   * Overriden from Throwable to supply custom message.
   */
  @Override
  public String getMessage() {
	return message;
	
  }
}
