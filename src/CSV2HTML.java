import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.net.URISyntaxException;

/**
 * 
 * @author user
 *
 */
public class CSV2HTML {
  
 
  
  public static void main(String[] args) throws IOException, URISyntaxException {
	
	launchAllInputFormatTests();
	//openLocalFiles(new String[] {"doctorList.html","covidStatistics.html"});
	
	
	
	

	
	System.exit(0);
  }//END main
  
  /**
   * Opens listed files, files must be in project 'doc' directory.
   * @param fileNames
   */
  private static void openLocalFiles(String[] fileNames) {
	for(String fileName:fileNames) {
	  try {
		Desktop.getDesktop().open(new File("C:/Users/user/Desktop/COMP 249/Assignments/a3/COMP249_A3/COMP249-A 3/" + fileName));
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}
	//Possible other method, need to know how to format URIs.
	//Desktop.getDesktop().browse(new URI("C:/Users/user/Desktop/COMP 249/Assignments/a3/COMP249_A3/COMP249-A 3/doctorList.html"));
  }
   
  
//====================================================== INPUT FORMAT CHECKERS =========================================================//
  
  //REGEX memory-aid: X? = once or no times. X* zero or more times. X+ one or more times. X-Y indicates range (inclusive)
  
  /**
   * Checks if String is valid title.
   * @param line
   * @return
   */
  private static boolean isTitle(String line) {
	return line.matches("([a-zA-Z_0-9:-[.]]+\s*)+,,,");
  }
  
  /**
   * Checks if String is valid column titles.
   * @param line
   * @return
   */
  private static boolean isColumnTitles(String line) {
	return line.matches("(([a-zA-Z_0-9]+\s*)+,){3}([a-zA-Z_0-9]+\s*)+");
  } 
 
 /**
  * Checks if String is valid data row. 
  * @param line
  * @return
  */
  private static boolean isDataRow(String line) {
	return line.matches("(([a-zA-Z_0-9_:_-[.]]+\s*)+,){3}([a-zA-Z_0-9_%-]+\s*)+");
  } 
  
  /**
   * Checks if String is a valid note.
   * @param line String to compare
   * @return true is line starts with "note", false otherwise.
   */
  private static boolean isNote(String line) {
	return line.matches("([a-zA-Z_0-9:-[.]]+\s*)+,,,") && line.regionMatches(true, 0, "note", 0, 4);
  }
  
  /**
   * Checks if String is a valid data row; but allows missing entries.
   * @param line String to check
   * @return true if seperating commas are present with or without data entries, false iff commas are missing or of  count.
   */
  private static boolean isDataRowWithMissingData(String line) {
	return line.matches("(([a-zA-Z_0-9_:_-[.]]+\s*)*,){3}([a-zA-Z_0-9_%-]+\s*)*");
  }

  
//============================================================= TESTS ==================================================================//
  
  
  private final static void launchAllInputFormatTests() {
	noteAndTitleMatchTest();
	columnTitleMatchTest();
	dataRowMatchTest();
	System.out.println("\nTests Complete.");
  }
 
  private final static void noteAndTitleMatchTest() {
	System.out.println("\t\t\tLaunching Title or Note Match...");
	System.out.println();
	
	String titleTest1 = "Summary of confirmed cases of COVID-19,,,";
	String titleTest2 = "Note: Each ICU admission is also included in the total number of hospitalization.,,,";
	String titleTest3 = "Note: This is not a proper note.,25203,,";
	String titleTest4 = ",,,";
	
	
	System.out.println("titleTest1 String: " + "\"" + titleTest1 + "\"");
	System.out.println("titleTest2 String: " + "\"" + titleTest2 + "\"");
	System.out.println("titleTest3 String: " + "\"" + titleTest3 + "\"");
	System.out.println("titleTest4 String: " + "\"" + titleTest4 + "\"");
	System.out.println();
  
	System.out.println("titleTest1 isTitle? " + isTitle(titleTest1));
	
	System.out.println("titleTest1 isNote? " + isNote(titleTest1));
	
	System.out.println("titleTest2 isNote? " + isNote(titleTest2));
	
	System.out.println("titleTest3 isNote? " + isNote(titleTest3));
	
	System.out.println("titleTest4 isTitle? " + isTitle(titleTest4));
	
  }
  
  private final static void columnTitleMatchTest(){
 	System.out.println("\t\t\tLaunching Column Title Match...");
 	System.out.println();
 	
 	String  columnTest1 = "Age group,Hospitalized,ICU,Fully vaccinated";
 	String  columnTest2 = "Age group,Hospitalized, ,Fully vaccinated";
 	String  columnTest3 = "Age group,,,Fully vaccinated";
 	
 	System.out.println("columnTest1 String: " + "\"" + columnTest1 + "\"");
 	System.out.println("columnTest2 String: " + "\"" + columnTest2 + "\"");
 	System.out.println("columnTest3 String: " + "\"" + columnTest3 + "\"");
 	System.out.println();
 	
 	System.out.println("columnTest1 isColumnTitles? " + isColumnTitles(columnTest1));
 	
 	System.out.println("columnTest1 isColumnTitles? " + isColumnTitles(columnTest1));
 	
 	System.out.println("columnTest1 isColumnTitles? " + isColumnTitles(columnTest1));
 	
   }
   
  private final static void dataRowMatchTest(){
	System.out.println("\t\t\tLaunching Data Row Match...");
	System.out.println();
	
	String rowTest1 = "Age: 0-11,25203,18,54%";
	String rowTest2 = ",25203,18,54%";
	String rowTest3 = "Age: 0-11,25203,1854%";
	String rowTest4 = "Dr. Alice  ,DERMATOLOGY,1-555-234569,H-256";
	String rowTest5 = "Dr. Jhon,ALLERGY AND IMMUNOLOGY,1-555-125340,H-351";
	
	
	System.out.println("rowTest1 String: " + "\"" + rowTest1 + "\"");
	System.out.println("rowTest2 String: " + "\"" + rowTest2 + "\"");
	System.out.println("rowTest3 String: " + "\"" + rowTest3 + "\"");
	System.out.println("rowTest4 String: " + "\"" + rowTest4 + "\"");
	System.out.println("rowTest5 String: " + "\"" + rowTest5 + "\"");
	System.out.println();
	
	
	System.out.println("rowTest1 isDataRow? " + isDataRow(rowTest1)); 
	
	System.out.println("rowTest2 isDataRowWithMissingData? " + isDataRowWithMissingData(rowTest2)); 
	
	System.out.println("rowTest3 isDataRowWithMissingData? " + isDataRowWithMissingData(rowTest3)); //
	
	System.out.println("rowTest4 isDataRow? " + isDataRow(rowTest4)); 
	
	System.out.println("rowTest5 isDataRow? " + isDataRow(rowTest5)); 
	
  }
  
 
}
