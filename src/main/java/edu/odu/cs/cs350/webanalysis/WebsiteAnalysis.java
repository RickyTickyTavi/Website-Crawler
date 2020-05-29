package edu.odu.cs.cs350.webanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebsiteAnalysis {
	
	/**
	 * Examines a local website and derives statistics based on specific HTML
	 * elements identified by the requirements. The program will then create
	 * three output files:
	 * <ul>
	 * 		<li>JSON (.json)</li>
	 *		<li>Excel (.xlsx)</li>
	 *		<li>Text (.txt)</li>
	 * </ul>
	 * These output files will contain specific statistics as per requirements.
	 * 
	 * @author Richard Burleson		rburl001@odu.edu
	 * @author Dennis Houghton		dhoug002@odu.edu
	 * @author Mohamad Khalifa		mkhal001@odu.edu
	 * @author Brian Klarman		bklar001@odu.edu
	 * @author Jay Speidell			jspei004@odu.edu
	 * 
	 * @param	args		String array containing path to website and URLs
	 * 						that need to be resolved.
	 * 			args[0]		The local path to the website
	 * 			args[1..n]	Optional URLs that need to be resolved
	 * 
	 * @throws	IOException	if the user does not supply any paths or URLs
	 */
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//BufferedReader reader =
       //         new BufferedReader(new InputStreamReader(System.in));
     //String[] args = reader.readLine();
      
		
		if(args.length == 0) {
			WebsiteAnalysis.usageMessage();
		}
		
		
		Website website = new Website(args);
		////textexport()
		////excelexport()
		///JSONexport()
		
	}
	
	/**
	 * Message displayed to user instructing them how to supply the program
	 * with arguments that will be used to process data in their local website.
	 * 
	 * This message appears if the user did not supply any command-line
	 * arguments when initiating the program.
	 */
	public static void usageMessage() {
		System.out.print("Usage: WebsiteAnalysis Path URL1 [URL2 URL3...]\n"
				+  "	Path: Path to the local copy of website to be analyzed\n"
				+  "	URL1, 2, 3...: URLs to be translated to local directory structure.");
	}

}
