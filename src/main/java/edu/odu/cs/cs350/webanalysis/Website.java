package edu.odu.cs.cs350.webanalysis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Website will serve as the overarching class for the user's website.
 * Through this class, we will generate a list of pages that belong to the website
 * as well as maintain statistics applicable to the entire website.
 *
 */
public class Website {

	String basePath; 
	String[] urls;
	private ArrayList<Page> pages = new  ArrayList<Page>();
	private AssetTracker assetTracker = new  AssetTracker();
	
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
	
	public static final String defaultConstructorException = "No path specified. Can't use default constructor for object ";
	
	Date date;			// Created a method that takes care of this. Propose removal
	String timeString;	// Created a method that takes care of this. Propose removal
	//String commonFileName;	// Will use this after we replace local variable definitions
	//String outputDirPath = "./output/"; // Will use this after we replace local variable definitions
	
	/**
	 * An inner class to format JSON output. No methods. 
	 * All string data gives tight control over output appearance. 
	 * 
	 * -pageData is a map of maps
	 * -{"pagename" : 
	 * 	{ "Local Images" : "integer",
	 *    "External Images" : "integer",
	 *    "Number of Scripts" : "integer",
	 *    "Number of styles" : "integer", 
	 *    "List of Images" : "images.toString()",
	 *    "List of Scripts" : "scripts.toString()", 
	 *    "List of styles" : "styles.toString()",
	 *    "Number of intra-Page links" : "integer",
	 *    "Number of inter-site links" : "integer",
	 *    "number of external links" : "integer" 
	 *    }}
	 *    
	 */

	

    
	
	
	
	/**
	 *  All output file names must be prefixed with yyyyMMdd-hhmmss-summary.
	 *  This method should only be called once so that the three output files
	 *  have the same date and time.
	 *  
	 * @return	The file name of the file without the type extension (e.g. .json)
	 */

	public String outputFileName() {

		final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
		final LocalDateTime ldt = LocalDateTime.now();
		
		return (dtf.format(ldt) + "-summary");
	}

	/**
	 * Creates the output directory in root; root/output/<p>
	 * The text, excel, and JSON files will be saved to this directory.
	 * 
	 * @param dirPath	output directory path defined in Website class
	 */
	private void createOutputDirectory(String dirPath) {

		//final String outputDirName = "./output/"; // Deprecated. Changed method to accept string arg
		
		File directory = new File(dirPath);
		directory.mkdirs();
	}
	

	
	/**
     * Default constructor for Website.
     * Website should always be created by taking in one argument,
     * the absolute path to the website
     * <p>
     * When invoking the default constructor, the program will throw an IllegalArgumentException
     * 
     * @throws IllegalArgumentException	because Website should always be created by taking in one argument
     */
    public Website() throws IllegalArgumentException { //I'm not sure we need to add throws here, as it is a runtime error, but im not sure.
    	
    	throw new IllegalArgumentException(Website.defaultConstructorException + this.getClass().getSimpleName());
    
    }
	
	/**
	 * Website has a location, and this is the parameter for this nondefault
	 * constructor. This is the only valid constructor. Everything in the 
	 * Website is built off of this input.
	 * 
	 * @param	args	the location of the Website on a user's local drive.
	 * @throws	IOException	if path or filename are invalid
	 */
	public Website(String[] args) throws IOException{
		
		// TODO verify filepath is valid with method verifyFileOrDirectory(String)
		// TODO replace date and timeString references with method return
		basePath = args[0];
		urls = new String[args.length - 1]; 
		//Populate website urls array with args[1..n]
		System.arraycopy(args, 1, urls, 0, args.length - 1);
		
//		if(verifyFileOrDirectory(basePath)) {
//			// Generate the common fileName used for all output files
//			commonFileName = outputFileName();
//			
//			// Create the output directory for all output files to be stored
//			createOutputDirectory(outputDirPath);
//		
//			// Continue with constructor...
//		}
		
		date = new Date();  // Created method for this. Propose removal
		timeString = dateFormat.format(date); // Created method for this. Propose removal
		
		/*
		 * This grabs a stream of Path objects for each file in the directory recursively, 
		 * maps those paths to their corresponding strings, and then uses a collector to 
		 * gather them into a list. 
		 */
		Stream<Path> paths = Files.walk(Paths.get(basePath)).filter(Files::isRegularFile); 
		List<String> fileIndex = paths.map(path -> path.toString()).collect(Collectors.toList());
		File tempFile;
		
		for (String f : fileIndex) {

			try{
				tempFile = new File(f); 
				
				if (FileUtils.readFileToString(tempFile, "UTF-8").contains("<html>")) {
					if (pages.size() == 1000) {
						System.out.println("Page limit of 1000 exceeded. Only first 1000 will be processed");
						break; 
					}
					Page page = new Page(f, basePath, urls);
					pages.add(page);
					assetTracker.addAssetTracker(page.getAssetsInfo());
					page.getAssetsInfo().getImageUsage().clear();
				}
			}
			catch (Exception e){
				
			}
		}
		Collections.sort(pages, Page.compareByPath);
		
		//exportText();		// Uncomment to run with constructor
		//excelExport();	// Uncomment to run with constructor
		//jsonExport();		// Uncomment to run with constructor
	}

	/**
	 * This exports the path to each page along with the size of images contained 
	 * on each, followed by the total size of images on all pages, in a text file. 
	 * <p>
	 * Format:
	 * <ul>
	 * 		<li>image size           path<li>
	 * 		<li>...</li>
	 * 		<li>image size           path<li>
	 * 		<li>image size sum<li>
	 * </ul>
	 */
	public void exportText() {
		
		double localTotalSizeVar= 0;
		
		try {
			// TODO move outputDirectory and filename to constructor for method calls
			// TODO remove output directory creation. Constructor handles.
			String outputDirectory = "./output/";
			String fileName = outputDirectory + timeString + "-summary.txt";
			// Replace above line with = outputDirectory + commonFileName + ".txt"
			File directory = new File(outputDirectory); // Remove and use method in constructor
			directory.mkdirs(); // Remove and use method in constructor
			File file = new File(fileName);
			PrintWriter writer = new PrintWriter(file);
			//writer.println("empty file");
			for(Page pageVar : pages){
				
				//System.out.println("Hello");
				if (pageVar.getSizeOfImages() < 10.0) {
					writer.printf("%.1f", pageVar.getSizeOfImages());
				} else  {
					writer.printf("%.0f", pageVar.getSizeOfImages());
				}
				
				writer.printf("M\t./");
				writer.printf("%s", FilenameUtils.separatorsToUnix(pageVar.getPagePath())); 
				writer.printf("\n");
				localTotalSizeVar += pageVar.getSizeOfImages();
			}
			if (localTotalSizeVar < 10.0) {
				writer.printf("%.1f", localTotalSizeVar);
			} else {
				writer.printf("%.0f", localTotalSizeVar);
			}
			writer.printf("M"); 
			writer.close(); 
			fileName = FilenameUtils.separatorsToSystem(fileName);
			System.out.println("Exported Text file " + fileName);
		}
		catch (IOException except){
			
			except.printStackTrace();
			
		}
	}
	
	/**
	 * Utilizes the apache poi library to set up an excel spreadhseet. Output
	 * to an xlsx file with one row for each page, including the 
	 * following columns: 
	 * <ul>
	 * 		<li>Page</li>	
	 *		<li># Images</li>
	 *		<li># CSS</li>	
	 *		<li># Scripts</li>
	 *		<li># Links (Intra-Page)</li>
	 *		<li># Links (Internal)</li>
	 *		<li># Links (External)</li>
	 * </ul>
	 * <p>
	 * Calls: {@link #createHeaderRow(Row, CreationHelper, CellStyle) createHeaderRow}
	 * 
	 * @throws FileNotFoundException		File not found
	 * @throws IOException 					Input/Output failure
	 * @throws EncryptedDocumentException	Document is encrypted
	 */
	public void excelExport() throws FileNotFoundException, IOException {
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper(); ///used for string creation
		Sheet sheet = wb.createSheet("Summary");
		String wbName = FilenameUtils.separatorsToSystem("./output/" + timeString + "-summary.xlsx"); 
		
		Row row = sheet.createRow(0); ///creates the first row.
		
		 CellStyle style = wb.createCellStyle();
		    Font font = wb.createFont();
		    font.setBold(true);
		    style.setFont(font);
		    
		    createHeaderRow(row,createHelper,style);
		    
		    /*
		     * Iterate through each page of website and send the name of the
		     * page, # images on page, # CSS scripts on page, # of intra page,
		     * inter-site, and external links to the excel writer
		     */
		    int rowCount = sheet.getLastRowNum();
		    
		    for(Page webPages : pages) {
		    	Row excelRow = sheet.createRow(++rowCount);
                
                // Output the path to the html page. Use createHelper for string
                excelRow.createCell(0).setCellValue(
                		createHelper.createRichTextString(
                				webPages.getPagePath()));
                // Output # Images
                excelRow.createCell(1).setCellValue(
                		webPages.getNumImages());
                // Output # CSS
                excelRow.createCell(2).setCellValue(
                		webPages.getNumStyles());
                // Output # Javascript scripts
                excelRow.createCell(3).setCellValue(
                		webPages.getNumScripts());
                // Output # Intra-page links
                excelRow.createCell(4).setCellValue(
                		webPages.getNumIntraLinks());
                // Output # Inter-site links
                excelRow.createCell(5).setCellValue(
                		webPages.getNumInterLinks());
                // Output # External links
                excelRow.createCell(6).setCellValue(
                		webPages.getNumExternLinks());
                
		    }
		    
		    ///auto adjust the column widths to fit the contents
		    for( int i = 0; i < 7; i++) {
		    	sheet.autoSizeColumn(i);
		    }
		    
		    
		    try (OutputStream fileOut = new FileOutputStream(wbName)) {
		    	// replace FileOutoutStream arg with outputDirectory + commonFileName + ".xlsx"
		    	wb.write(fileOut);
		        wb.close();
		        //fileOut.close(); // Handled automatically by the try-with-resources
		        
                System.out.println("Exported Excel file " + wbName);
		        } catch (Exception e) {
		        	System.out.println(e);
		        }
	}
	
	/**
	 * Creates the header row in the exported excel file.
	 * <p>
	 * Calling method: {@link #excelExport() excelExport}
	 * 
	 * @param	row				Worksheet row
	 * @param	createHelper	The CreationHelper
	 * @param	style			The CellStyle
	 */
	public void createHeaderRow(Row row, CreationHelper createHelper, CellStyle style) {

		Cell cell = row.createCell(0);
		cell.setCellValue(createHelper.createRichTextString("Page"));      
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue(createHelper.createRichTextString("#images"));      
		cell.setCellStyle(style);

		cell = row.createCell(2);
		cell.setCellValue(createHelper.createRichTextString("#CSS"));      
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue(createHelper.createRichTextString("Scripts"));      
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue(createHelper.createRichTextString("#Links(Intra-Page)"));      
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue(createHelper.createRichTextString("#Links(Internal)"));      
		cell.setCellStyle(style);

		cell = row.createCell(6);
		cell.setCellValue(createHelper.createRichTextString("#Links(external)"));      
		cell.setCellStyle(style);
	}
	
	
	/**
	 * Exports a JSON file with data describing the website. 
	 * <p>
	 * The JSON file should describe each page and resource in the site:
	 * <p>
	 * One entry per page detailing:
	 * <ul>
	 *		<li>Number of local images</li>
	 *		<li>Number of external images</li>
	 *		<li>Number of scripts referenced</li>
	 *		<li>Number of stylesheets utilized</li>
	 *		<li>Listing of images</li>
	 *		<li>Listing of scripts</li>
	 *		<li>Listing of stylesheets</li>
	 *		<li>Number of intra-page links</li>
	 *		<li>Number of inter-site links</li>
	 *		<li>Number of external links</li>
	 * </ul>
	 * One entry per image detailing:
	 * <ul>
	 *		<li>Number of pages on which it is displayed</li>
	 *		<li>Listing of pages on which it is displayed</li>
	 * </ul>
	 * One entry per archive file detailing
	 * <ul>
	 *		<li>File size</li>
	 *		<li>Path to resource (relative to local site root)</li>
	 * </ul>
	 * One entry per video file detailing
	 * <ul>
	 *		<li>File size</li>
	 *		<li>Path to resource (relative to local site root)</li>
	 * </ul>
	 * One entry per audio file detailing
	 * <ul>
	 *		<li>File size</li>
	 *		<li>Path to resource (relative to local site root)</li>
	 * </ul>
	 * One entry per non-categorized file detailing
	 * <ul>
	 *		<li>File size</li>
	 *		<li>Path to resource (relative to local site root)</li>
	 * </ul>
	 * 
	 * See <a href="https://github.com/jdereg/json-io/blob/master/user-guide.md">JSON-IO</a>
	 * @throws IOException 
	 */
	public void jsonExport() throws IOException {
		
		OutputStream outStream = null;
		String json;
		
		/*
		 * This is largely the same method used in text export.
		 * We will use PRETTY_PRINTING method in json-io library to make it more readable.
		 * 
		 */
		String outputDirectory = "./output/";
		String fileName = outputDirectory + timeString + "-summary.json";
		fileName = FilenameUtils.separatorsToSystem(fileName);
		
		File directory = new File(outputDirectory); // Remove and use method in constructor
		directory.mkdirs(); // Remove and use method in constructor
		File file = new File(fileName);
		outStream = new FileOutputStream(file);
		
		Map jsonArgs = new HashMap();
		jsonArgs.put(JsonWriter.PRETTY_PRINT, true);
		jsonArgs.put(JsonWriter.TYPE, false);
		jsonArgs.put(JsonWriter.SKIP_NULL_FIELDS, true);
		jsonArgs.put(JsonWriter.ENUM_PUBLIC_ONLY, true);
		

		
		Map<Class, List<String>> fields = new HashMap<>();
		List<String> webSiteExcludeFields = new ArrayList<>();
		
		//webSiteExcludeFields.add("assetTracker");
		webSiteExcludeFields.add("dateFormat");
		webSiteExcludeFields.add("date");
		webSiteExcludeFields.add("timeString");
		fields.put(Website.class, webSiteExcludeFields); 
				//excludeFields);
		
		List<String> pageExcludeFields = new ArrayList<>();
		
		pageExcludeFields.add("relPath");
		pageExcludeFields.add("sizeOfImages");
		pageExcludeFields.add("assetTracking");
		
		fields.put(Page.class, pageExcludeFields);
		
		jsonArgs.put(JsonWriter.FIELD_NAME_BLACK_LIST, fields);
		
		JsonWriter writer = new JsonWriter(outStream, jsonArgs);
		
		writer.write(this);
		//for(Page webpages : pages){
			// writer.write(webpages);		 
		//}
		 
		 
		 //for(AssetTracker asset : assetTracker) {
			// writer.write(asset);
		 //}
		 writer.close();
		 
		
		
		System.out.println("Exported JSON file " + fileName);
    
		
	}
}


