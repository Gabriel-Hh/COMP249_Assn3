
/**
 * 
 * @author Gabriel Horth
 * @version 1.0
 *
 */
public class CSVDataMissing extends Exception{

  private static final long serialVersionUID = -4200987094118330922L;

  private String message;

  public CSVDataMissing() {
	fillInStackTrace();
	message = "Error: Input row cannot be parsed due to missing information";
  }

  public CSVDataMissing(String message) {
	fillInStackTrace();
	this.message = message;
  }

  @Override
  public String getMessage() {
	return message;

  }
}