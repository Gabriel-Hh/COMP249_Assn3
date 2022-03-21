import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.Desktop;
import java.util.Scanner;

/**
 * 
 * @author Gabriel Horth
 * @version 1.0
 *
 */
public class CSV2HTML {
  
  static Scanner key = new Scanner(System.in);
  static Scanner[] fr = null;
  static PrintWriter[] pw = null;
  static PrintWriter log = null;
  static String[] fileNames = null;

  /**
   * 
   */
  public static void main(String[] args) {
	
	System.out.println("=============================================================================================");
	System.out.println("\t\t\tWelcome to the free CSV -> HTML Table Converter");
	System.out.println("=============================================================================================");
	
	System.out.println();
	System.out.println("Press <ENTER> to begin..");
	key.nextLine();
	
	fileNames = new String[]{"covidStatistics.csv","doctorList.csv"};
	
	System.out.println();
	System.out.println("Linking file(s)..");
	
	linkSourceFiles();
	System.out.println("Input files linked successfully.");
	
	System.out.println("\nPress <ENTER> to initialise output files..");
	key.nextLine();
	
	createOutputFiles();
	System.out.println("Output files initialized successfully.");
	
	System.out.println("\nPress <ENTER> to convert files files..");
	key.nextLine();
	
	for(int i = 0; i < fileNames.length; i++) {
	try{
	  ConvertCSVtoHTML(fr[i],pw[i]);
	}
	catch(CSVAttributeMissing e) {
	  System.out.println(e.getMessage());
	  log.println(e.getMessage());
	}
	
	}
	closeAllFileStreams();
	System.out.println("File conversion finished.");
	
	chooseFilesToOpen(0);
	
	key.close();
	System.out.println();
	System.out.println("Thanks for trying trying out this converter!");
	System.out.println("Program closing...");
	System.exit(0);
  }//END main
  
  /**
   * 
   */
  public static void ConvertCSVtoHTML(Scanner fr,PrintWriter pw)throws CSVAttributeMissing {
	
	
  }
  
  /**
   * 
   */
  public static void linkSourceFiles() {
	fr = new Scanner[fileNames.length];
	
	for(int i = 0; i < fileNames.length;i++) {
	  
	  try {
		fr[i] = new Scanner(new FileInputStream("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/doc/"+ fileNames[i]));
	  }catch (IOException ioe) 
	  {System.out.println("Could not open input file "+ fileNames[i] +" for reading."
		  + "\nPlease check that the file exists and is readable. This program will terminate after closing any opened files.");
	  	for(int j = 0; j < i; j++) { //Closes open files in case of IOEXCEPTION.
	  	  fr[j].close();
	  	}
	  }
	}
  }
  
  /**
   * 
   */
  public static void closeAllFileStreams() {
	for(PrintWriter pw: pw) {pw.close();}
	for(Scanner fr: fr) {fr.close();}
	log.close();
  }
  
  /**
   * 
   */
  public static void createOutputFiles() {
	pw = new PrintWriter[fileNames.length];
	
	int i = 0;
	try {
	  log = new PrintWriter(new FileOutputStream("Exceptions.log"),true);
	  for(; i < pw.length; i++) {
		String htmlName = fileNames[i].substring(0, fileNames[i].indexOf('.')) + ".html";
		pw[i] = new PrintWriter(new FileOutputStream(htmlName),true);
		}
	}
	catch (IOException e) {
	  String causalFile;
	  if(i==0) {causalFile = "Exception.log";}
	  else {causalFile = fileNames[i];}
	  
	  System.out.println("Error: file " + causalFile + " could not be created."
	  	+ "Type: Unrecoverable, program will close.");
	  fileCreationErrorCleanup();
	  System.exit(1);
	}
  }

  /**
   * 
   */
  public static void fileCreationErrorCleanup() {
	for(String fileName: fileNames ) {deleteOnExit(fileName);}
	deleteOnExit("Exception.log");
	closeAllFileStreams();
	key.close();
  }
  
  /**
   * 
   */
  private static void chooseFilesToOpen(int trial) {
	boolean failed = true;
	while(trial<2 && failed) {
	  System.out.println("\nPlease enter the name of HTML file(s) to open(Enter as 'file' or 'file,file'): ");
	  String[] files = key.nextLine().split(",");
	  try {openLocalFiles(files);}
	  catch(IllegalArgumentException e) {System.out.println("Error: Invalid file name.");chooseFilesToOpen(++trial);}
	  failed = false;
	}
  }
 
  /** 
   * Opens listed files, files must be in projects' main directory.
   * @param fileNames
   * @throws  
   */  
  private static void openLocalFiles(String[] fileNames) throws IllegalArgumentException {
	for(String fileName:fileNames) {
	  try {
		Desktop.getDesktop().open(new File("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/" + fileName));
	  } catch (IOException e) {
		System.out.println("Error: File" + fileName + "could not be opened"
			+ "\nPlease verify file name.");
	  }
	}
	//Possible other method, need to know how to format URIs.
	//Desktop.getDesktop().browse(new URI("C:/Users/user/Desktop/COMP 249/Assignments/a3/COMP249_A3/COMP249-A 3/doctorList.html"));
  }
  
  /**
   * Deletes listed file after file closed. Files must be in projects' 'doc' directory.
   * @param fileName
   */
  private static void deleteOnExit(String fileName) {
	File file = new File("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/doc/" + fileName);
	try {
	if(!file.exists()) {log.println("File: " + fileName + "was not found and couldn't be deleted." );}}
	catch (NullPointerException npe) 
	  {System.out.println("For deleteOnExit(): Exception log does not exist, exception not written.");}
	File path = file.getAbsoluteFile();  
	path.deleteOnExit();
  }
  
//====================================================== INPUT FORMAT CHECKERS =========================================================//
  
  //REGEX memory-aid: X? = once or no times. X* = zero or more times. X+ = one or more times. X-Y indicates range (inclusive)
  
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
  
  
  @SuppressWarnings("unused")
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
