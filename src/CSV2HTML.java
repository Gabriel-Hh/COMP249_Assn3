import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.Desktop;
import java.util.Date;
import java.util.Scanner;

/**
 * 
 * @author Gabriel Horth
 * @version 1.0
 *
 */
public class CSV2HTML {

  private static Scanner key = new Scanner(System.in);
  private static Scanner[] fr = null;
  private static PrintWriter[] pw = null;
  private static PrintWriter log = null;
  protected static String[] fileNames = null;
  protected static File[] outputFiles = null;
  private static int fileIndex; // USED loop in by main()
  protected final static File inputDirectory = new File("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/doc/");
  protected final static File outputDirectory =new File("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/");

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
	
	fileNames = chooseSourceFiles();

	

	System.out.println();
	System.out.println("Linking file(s):");
	for (String fileName : fileNames) {
	  System.out.println(fileName);
	}

	linkSourceFiles();
	System.out.println("Input files linked successfully.");

	System.out.println("\nPress <ENTER> to initialise output files..");
	key.nextLine();

	linkOutputFiles();
	System.out.println("Output files initialized successfully.");

	System.out.println("\nPress <ENTER> to convert files..");
	key.nextLine();

	for (fileIndex = 0; fileIndex < fileNames.length; fileIndex++) {
	  try {
		ConvertCSVtoHTML(fr[fileIndex], pw[fileIndex], log);
	  } catch (CSVAttributeMissing e) {
		System.out.println(e.getMessage());
		log.println(e.getMessage());
		deleteOnExit(outputFiles[fileIndex]);
		outputFiles[fileIndex] = null;// to delete path as well
	  }
	}
	closeAllFileStreams();
//	System.out.println("File(s) converted: ");
//	for (int i = 0; i < (outputFileNames.length - 1); i++) {
//	  System.out.println(outputFileNames[i]);
//	}

	chooseFilesToOpen(1);

	key.close();
	System.out.println();
	System.out.println("Thanks for trying out this converter!");
	System.out.println("Program closing...");
	System.exit(0);
  }// END main

  /**
   * 
   */
  public static void ConvertCSVtoHTML(Scanner fr, PrintWriter pw, PrintWriter log) throws CSVAttributeMissing {
	int lineNumber = 0;
	String line;
	String[] lineArray;
	String[] columnTitleArray;

	setUpHTML(pw);

	//////////////////////// Table Title ///////////////////////
	line = fr.nextLine();
	lineNumber++;
	if (isValidTitle(line)) {
	  lineArray = line.split(",");
	  if (lineArray[0].contains("﻿")) {
		lineArray[0] = lineArray[0].substring(3);
	  }

	  pw.println("<caption><b>" + lineArray[0] + "</b></caption>");
	} else {
	  log.println("Error: In conversion file " + fileNames[fileIndex] + ": title not created for file");
	}

	/////////////////////// Column Titles //////////////////////
	line = fr.nextLine();
	lineNumber++;
	if (isValidColumnTitles(line)) {
	  columnTitleArray = line.split(",");

	  pw.println("  <tr>" + "\n\t<td><b>" + columnTitleArray[0] + "</b></td>" + "\n\t<td><b>" + columnTitleArray[1]
		  + "</b></td>" + "\n\t<td><b>" + columnTitleArray[2] + "</b></td>" + "\n\t<td><b>" + columnTitleArray[3]
		  + "</b></td>" + "\n  </tr>");
	} else {
	  throw new CSVAttributeMissing(
		  "ERROR: In file " + fileNames[fileIndex] + ". " + "Missing attribute. File is not converted to HTML.");
	}

	///////////////////// Data Rows and Note ////////////////////
	boolean hasNote = false;
	while (fr.hasNextLine()) {
	  line = fr.nextLine();
	  lineNumber++;
	  lineArray = line.split(",");

	 
	  // Valid Data Row
	  if (isValidDataRow(line)) {

		pw.println("  <tr>" + "\n\t<td>" + lineArray[0] + "</td>" + "\n\t<td>" + lineArray[1] + "</td>" + "\n\t<td>"
			+ lineArray[2] + "</td>" + "\n\t<td>" + lineArray[3] + "</td>" + "\n  </tr>");
	  }

	  // Valid Note
	  else if (isValidNote(line)) {
		pw.println("</table>" + "\n<span>" + lineArray[0] + "</span>" + "\n</body>" + "\n</html>");
	  }
	  
	  // Valid Data Row with missing data
	  else if (isValidDataRowWithMissingData(line)) {
		try {
		  int missingDataIndex = 0;// initialised to zero to avoid OutOfBounds in error CSVDataMissing message.

		  for (int i = 0; i < lineArray.length; i++) {
			if (lineArray[i].equalsIgnoreCase("")) {missingDataIndex = i;}
		  }
		  throw new CSVDataMissing("WARNING: In file " + fileNames[fileIndex] + " line " + lineNumber
			  + " is not converted to HTML: missing data: " + columnTitleArray[missingDataIndex] + ".");
		} catch (CSVDataMissing e) {
		  System.out.println(e.getMessage());
		  log.println(e.getMessage());
		}
	  }
	  // ALL other invalid lines
	  else {
		String otherError = "WARNING: In  file " + fileNames[fileIndex] + " line " + lineNumber
			+ " is not converted to HTML: Invalid format or character.";
		System.out.println(otherError);
		log.println(otherError);
	  }
	} // END while(hasNextLine())
	if(!hasNote) {pw.println("</table>" + "\n</body>" + "\n</html>");}
  }

  public static String[] chooseSourceFiles() {
	
	String[] availableFiles = inputDirectory.list((optionalInnerDirectory, fileInDirectory) -> {
		return fileInDirectory.toLowerCase().endsWith("csv");
	});
		
	System.out.println("Available file(s):");
	for(String fileName: availableFiles) {
	  System.out.println(fileName);
	}
	
	System.out.println();
	System.out.println("Enter the files to convert ('file,file' or 'all'): ");
	String userInput[] = key.nextLine().split(",");
	if(userInput[0].equalsIgnoreCase("all")) {
	 return availableFiles;
	}
	return userInput;
  }
  
  /**
   * 
   */
  public static void linkSourceFiles() {
	fr = new Scanner[fileNames.length];

	for (int i = 0; i < fileNames.length; i++) {

	  try {
		fr[i] = new Scanner(
			new FileInputStream("C:/Users/user/Desktop/Java Workplace/COMP249_Assn3/doc/" + fileNames[i]));
	  } catch (IOException ioe) {
		System.out.println("Could not open input file " + fileNames[i] + " for reading."
			+ "\nPlease check that the file exists and is readable. This program will terminate after closing any opened files.");
		
		closeAllFileStreams();
		key.close();
		System.exit(1);
	  }
	}
  }

  /**
   * 
   */
  public static void closeAllFileStreams() {
	if(pw!=null) {
	  for (PrintWriter pw : pw) {
		if (pw != null) {
		  pw.close();
		}
	  }
	}
	for (Scanner fr : fr) {
	  if (fr != null) {
		fr.close();
	  }
	}
	if (log != null) {
	  log.println();
	  log.close();
	}
  }

  /**
   * 
   */
  public static void linkOutputFiles() {
	pw = new PrintWriter[fileNames.length];
	outputFiles = new File[fileNames.length + 1];

	int i = -1;
	try {
	  log = new PrintWriter(new FileOutputStream("Exceptions.log",true), true); //Append = true
	  Date logTime = new Date();
	  log.println(logTime);
	  outputFiles[(outputFiles.length - 1)] = new File(outputDirectory.getAbsolutePath() + "/" + "Exceptions.log");
	  for (i = 0; i < pw.length; i++) {
		String htmlName = fileNames[i].substring(0, fileNames[i].indexOf('.')) + ".html";
		pw[i] = new PrintWriter(new FileOutputStream(htmlName), true);// true = autoflush()
		outputFiles[i] = new File(outputDirectory.getAbsolutePath() + "/" + htmlName);
		System.out.println(outputFiles[i].getName());
	  }
	} catch (IOException e) {
	  String causalFile;
	  if (i == -1) {
		causalFile = "Exception.log";
	  } else {
		causalFile = fileNames[i];
	  }

	  System.out
		  .println("Error: file " + causalFile + " could not be created." + "Type: Unrecoverable, program will close.");
	  fileCreationErrorCleanup();
	  System.exit(1);
	}
  }

  /**
   * Preforms clean-up in case of error at output file creation.
   */
  public static void fileCreationErrorCleanup() {
	for (int i = 0; i < (outputFiles.length-1); i++) { //last output file 'Exceptions.log' will not be deleted: 
	  deleteOnExit(outputFiles[i]);
	}
	closeAllFileStreams();
	key.close();
  }

 /**
  * Deletes listed file once closed.
  * @param fileToDelete
  */
 private static void deleteOnExit(File fileToDelete) {
	try {
	  if (!fileToDelete.exists()) {
		log.println("File: " + fileToDelete.getName() + "was not found and couldn't be deleted.");
	  }
	} catch (NullPointerException npe) {
	  System.out.println("For deleteOnExit(): Exception.log does not exist, exception not written.");
	}
	
	fileToDelete.deleteOnExit();
 }
  
  /**
   * Formats new html doc and opens table.
   * 
   * @param pw
   */
  public static void setUpHTML(PrintWriter pw) {
	pw.println("<!DOCTYOE html>" + "\n<html>" + "\n<style>"
		+ "\ntable {font-family: arial, sans-serif;border-collapse: collapse;}"
		+ "\ntd, th {border: 1px solid #000000;test-align: left;padding: 8px;}"
		+ "\ntr:nth-child(even) {background-color: #dddddd;}" + "\nspan{font-size: small}" + "\n</style>" + "\n<body>"
		+ "\n" + "\n<table>");
  }

  /**
   *Prompts user to choose files to open.
   *@param int control variable, user gets only two chances to choose valid files. 
   */
  private static void chooseFilesToOpen(int trial) {
	
	System.out.println("File(s) created:");
	for (File file : outputFiles) {
	  if(file != null) {
	  System.out.println(file.getName());
	  }
	  }
	
	
	boolean failed = true;
	while (trial <= 2 && failed) {
	  System.out.println("\nEnter the name of HTML file(s) to open ('file,file' or 'all'): ");
	  String[] userInput = key.nextLine().split(",");
	  File[] selectedFiles = null;
	  
	  if(userInput[0].equalsIgnoreCase("all"))
		selectedFiles = outputFiles;
	  else {
		selectedFiles = new File[userInput.length];
		for(int i = 0; i < userInput.length; i++) {
		  selectedFiles[i] = new File(outputDirectory.getAbsolutePath() + "/" + userInput[i]);
		}
	}
	  try {
		openLocalFiles(selectedFiles);
	  } catch (IllegalArgumentException e) {
		System.out.println("Error: Invalid file name.");
		chooseFilesToOpen(++trial);
	  }
	  failed = false;
	}
  }

  /**
   * Opens listed files.
   * 
   * @param fileNames
   * @throws
   */
  private static void openLocalFiles(File[] filesToOpen) throws IllegalArgumentException {
	for (File file : filesToOpen) {
	  if(file != null) {
		try {
		  Desktop.getDesktop().open(new File(file.getAbsolutePath()));
		} catch (IOException e) {
		  System.out.println("Error: File" + file.getName() + "could not be opened" + "\nPlease verify file name.");
		}
	  }
	}
	// Possible other method, need to know how to format URIs.
	// Desktop.getDesktop().browse(new URI("C:/Users/user/Desktop/COMP
	// 249/Assignments/a3/COMP249_A3/COMP249-A 3/doctorList.html"));
  }

  /**
 

//====================================================== INPUT FORMAT CHECKERS =========================================================//

  // REGEX memory-aid: X? = once or no times. X* = zero or more times. X+ = one or
  // more times. X-Y indicates range (inclusive)

  /**
   * Checks if String is valid title.
   * 
   * @param line
   * @return
   */
  private static boolean isValidTitle(String line) {
	return line.matches("(([a-zA-Z0-9:%[-][.][﻿]])++\s{0,1})++,,,");
  }

  /**
   * Checks if String is valid column titles.
   * 
   * @param line
   * @return
   */
  private static boolean isValidColumnTitles(String line) {
	return isValidDataRow(line);
  }

  /**
   * Checks if String is valid data row.
   * 
   * @param line
   * @return
   */
private static boolean isValidDataRow(String line) {
	return line.matches(
		"((([a-zA-Z0-9:%[-][.]])++\s{0,1})++,){3}(([a-zA-Z0-9:%[-][.]])++\s{0,1})++");
  }

  /**
   * Checks if String is a valid note.
   * 
   * @param line String to compare
   * @return true is line starts with "note", false otherwise.
   */
  private static boolean isValidNote(String line) {
	return isValidTitle(line) && line.regionMatches(true, 0, "note", 0, 4);
  }

  /**
   * Checks if String is a valid data row; but allows missing entries.
   * 
   * @param line String to check
   * @return true if seperating commas are present with or without data entries,
   *         false iff commas are missing or illegal characters.
   */
  private static boolean isValidDataRowWithMissingData(String line) {
	return line.matches("((([a-zA-Z0-9:%[-][.]])*+\s{0,1})++,){3}(([a-zA-Z0-9:%[-][.]])++\s{0,1})*+");
  }

//====================================================== TESTS =========================================================//
  
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

	System.out.println("titleTest1 isTitle? " + isValidTitle(titleTest1));

	System.out.println("titleTest1 isNote? " + isValidNote(titleTest1));

	System.out.println("titleTest2 isNote? " + isValidNote(titleTest2));

	System.out.println("titleTest3 isNote? " + isValidNote(titleTest3));

	System.out.println("titleTest4 isTitle? " + isValidTitle(titleTest4));

  }

  private final static void columnTitleMatchTest() {
	System.out.println("\t\t\tLaunching Column Title Match...");
	System.out.println();

	String columnTest1 = "Age group,Hospitalized,ICU,Fully vaccinated";
	String columnTest2 = "Age group,Hospitalized, ,Fully vaccinated";
	String columnTest3 = "Age group,,,Fully vaccinated";

	System.out.println("columnTest1 String: " + "\"" + columnTest1 + "\"");
	System.out.println("columnTest2 String: " + "\"" + columnTest2 + "\"");
	System.out.println("columnTest3 String: " + "\"" + columnTest3 + "\"");
	System.out.println();

	System.out.println("columnTest1 isColumnTitles? " + isValidColumnTitles(columnTest1));

	System.out.println("columnTest1 isColumnTitles? " + isValidColumnTitles(columnTest1));

	System.out.println("columnTest1 isColumnTitles? " + isValidColumnTitles(columnTest1));

  }

  private final static void dataRowMatchTest() {
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

	System.out.println("rowTest1 isDataRow? " + isValidDataRow(rowTest1));

	System.out.println("rowTest2 isDataRowWithMissingData? " + isValidDataRowWithMissingData(rowTest2));

	System.out.println("rowTest3 isDataRowWithMissingData? " + isValidDataRowWithMissingData(rowTest3)); //

	System.out.println("rowTest4 isDataRow? " + isValidDataRow(rowTest4));

	System.out.println("rowTest5 isDataRow? " + isValidDataRow(rowTest5));

  }

}
