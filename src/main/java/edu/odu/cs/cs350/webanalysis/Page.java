package edu.odu.cs.cs350.webanalysis;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.net.URL; 

public class Page {
	private String path;
	private String urlPath; //NULL value
	private String relPath;
    private int numLocalImages = 0;
    private int numExternImages = 0;
    private ArrayList<String> imagePaths = new  ArrayList<String>();
    private ArrayList<String> scriptPaths = new  ArrayList<String>();
    private ArrayList<String> cssPaths = new  ArrayList<String>();
    private int numIntraLinks = 0;
    private int numInterLinks = 0;
    private int numExternLinks = 0;
    private double sizeOfImages;
    private AssetTracker assetTracking;
    
    enum Classification{
    	INTERNAL,EXTERNAL;
    }
    
    public static Comparator<Page> compareByPath = (Page lhs, Page rhs) -> lhs.getPagePath().compareTo(rhs.getPagePath());
        
    

///have jsoup extract image details in constructor
    /**
     * Default constructor for Page.
     * Page should always be created by taking in one argument,
     * the absolute path to the website
     * 
     * When invoking the default constructor, the program will throw an IllegalArgumentException
     */
    public Page() throws IllegalArgumentException { //I'm not sure we need to add throws here, as it is a runtime error, but im not sure.
    	throw new IllegalArgumentException(Website.defaultConstructorException + this.getClass().getSimpleName());
    }
    
    /**
     * The only constructor that will be used for Page. 
     * This constructor will use JSoup to extract elements 
     * from the linked file containing HTML and populate 
     * the data members. 
     * <p>
     * The path should already be confirmed to be an HTML 
     * file before being passed to this constructor. 
     * 
     * @param	fullDirectoryP	The path to this page.
     * @param	rootDirectoryP	The root directory
     * @param	URLs			Array of URLs
     */
	//Change constructor to something like a single single string array
    //Index[0] will be full path, Index[1..n] will at least one or more URL to be translated
    
    public Page(String fullDirectoryP, String rootDirectoryP, String[] URLs) {
    	
    	path = fullDirectoryP;

        relPath = new File(rootDirectoryP).toURI().relativize(new File(path).toURI()).getPath();
		
        assetTracking = new AssetTracker();
        
        try {
			String thisImage;
			File htmlFile; 
			File imageFile;  
			htmlFile = new File(path); 
			Document doc = Jsoup.parse(htmlFile, "UTF-8", "http:://filler.com/"); 
			
			Elements linkedImages = doc.select("img");
			for (Element linkedImage : linkedImages) {
				thisImage = linkedImage.attr("src"); 
				if (thisImage.contains("http") && isExternal(thisImage, URLs)) {
					numExternImages++; 
				} else {
					numLocalImages++;
					assetTracking.addImage(thisImage, relPath);
					thisImage = rootDirectoryP + thisImage; 
					imageFile = new File(thisImage);
					sizeOfImages = sizeOfImages + (imageFile.length() / 1048576.0); 
				}
				imagePaths.add(thisImage);
			}
			
			String thisScript; 
			Elements scriptFiles = doc.getElementsByAttributeValueMatching("type", "text/javascript");
			for (Element script : scriptFiles) {
				thisScript = script.attr("src"); 
				if  (!thisScript.contains("http")) {
					thisScript = rootDirectoryP + thisScript; 
				} 
				scriptPaths.add(thisScript); 
				
				///something along the lines of
			}
			
			String thisStyle; 
			Elements styleFiles = doc.getElementsByAttributeValueMatching("type", "text/css");
			for (Element style : styleFiles) {
				thisStyle = style.attr("href"); 
				if  (!thisStyle.contains("http")) {
					thisStyle = rootDirectoryP + thisStyle; 
				}
				cssPaths.add(thisStyle);
			}
			
//			Ask Kennedy about how to parse for archive files since they all fall under application
//			String thisArchive;
//			Elements archiveFiles = doc.getElementsByAttributeValueMatching("archive");
			
			// For testing purposes, add your own video file to directory
			String thisVideo;
			Elements videoFiles = doc.getElementsByAttributeValueMatching("type", "video/");
			for(Element video : videoFiles) {
				thisVideo = video.attr("src");
				thisVideo = rootDirectoryP + thisVideo;
				File videoFile = new File(thisVideo);
				
				assetTracking.addVideo(thisVideo, videoFile.length() / 1048576.0);
			}
			
			
			// For testing purposes, add your own audio file to directory
			String thisAudio;
			Elements audioFiles = doc.getElementsByAttributeValueMatching("type", "audio/");
			for(Element audio : audioFiles) {
				thisAudio = audio.attr("src");
				thisAudio = rootDirectoryP + thisAudio;
				File audioFile = new File(thisAudio);
				
				assetTracking.addAudio(thisAudio, audioFile.length() / 1048576.0);
			}
			
//			Ask Kennedy about how to parse for archive files since they all fall under application
			// For testing purposes, add your own non-cat file to directory
			String thisNonCategorized;
			Elements nonCatFiles = doc.getElementsByAttributeValueMatching("type", "application/");
			
			for(Element nonCat : nonCatFiles) {
				thisNonCategorized = nonCat.attr("src");
				thisNonCategorized = rootDirectoryP + thisNonCategorized;
				File nonCatFile = new File(thisNonCategorized);
				
				assetTracking.addUncategorized(thisNonCategorized, nonCatFile.length() / 1048576.0);
			}
			
			
			String thisLink; 
			Elements linkyMcLinkface = doc.select("a");
			File tempFile; 
			for (Element link : linkyMcLinkface) {
				thisLink = link.attr("href"); 
				if (!thisLink.contains(rootDirectoryP)) {
					thisLink = rootDirectoryP + thisLink; 
				}
				if (thisLink.contains("#")) {
					thisLink = thisLink.split("#")[0];  // # will cause an error with File.exists() 
				}
				if (thisLink.contains("?")) {
					thisLink = thisLink.split("?")[0]; // ? will cause an error with File.exists() 
				}
				tempFile = new File(thisLink); 
				if (thisLink.contains("http") && isExternal(thisLink, URLs)) {
				//if (thisLink.contains("http")) {
					numExternLinks++;
				}else if (tempFile.exists()){
					if (thisLink.contains(relPath)) {
						numIntraLinks++; 
					} else {
						numInterLinks++; 
					}
				}
				// else ignore broken link
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
    
    /**
     * Private function that validates URL formats.
     * 
     * @param	path	a string containing a link
     * @return 	bool	a boolean 
     */
    private boolean validUrl(String path) {
    	try {
    		new URL(path).toURI(); 
    		return true;
    	}
    	catch (Exception e) {
    		return false; 
    	}
    }
	
    /**
     * Private function that returns true if a link doesn't match any website URLs
     * 
     * @param	link	link to be checked
     * @return 	bool	true if URL is external. False if URL is internal. 
     */
    private boolean isExternal(String link, String[] URLs) {
    	boolean external = true;
    		for (String url : URLs) {
        		if (link.contains(url)) {
        			external = false;
        		}
        	}	
    	return external;
    }
    
    /**
     *  Returns the path to this page as a string. 
     * 
     * @return 	path	the path to this page as a string. 
     */
	public String getPath() {

		return this.path;
	}
	
    /**
     *  Returns the relative path to this page as a string. 
     * 
     * @return	relPath	the relative path to this page as a string. 
     */
	public String getPagePath() {

		return this.relPath;
	}
	
    /**
     *  Returns the URL path to this page as a string. 
     * 
     * @return urlPath	the URL path to this page as a string. 
     */
	public String getUrlPath() {

		return this.urlPath;
	}
	
    /**
     *  Return the total number of images used on the page as an integer. 
     * 
     * @return	this.images.size()	the total number of images as an integer. 
     */
	public int getNumImages() {
		
		//return this.images.size();
		return imagePaths.size();
	}
	
    /**
     *  Return the number of internal images used on the page as an integer. 
     * 
     * @return	numLocalImages	the number of internal images as an integer. 
     */
	public int getNumLocalImages() {
		
		//return this.numLocalImages;
		return numLocalImages;
	}
	
    /**
     *  Return the number of external images used on the page as an integer. 
     * 
     * @return	numExternImages	the number of external images as an integer. 
     */
	public int getNumExternImages() {
		
		//return this.numExternImages;
		return numExternImages;
	}
	
    /**
     *  Return the number of scripts used on the page as an integer. 
     * 
     * @return	scripts.size()	the number of scripts as an integer. 
     */
	public int getNumScripts() {
		
		//int copyofSize = new int(this.scripts.size());
		//return copyofSize;
		return scriptPaths.size();
	}
	
    /**
     *  Return the number of style sheets (CSS files) used on the 
     *  page as an integer. 
     * 
     * @return	styles.size()	the number of styles as an integer. 
     */
	public int getNumStyles() {
		
		//int copyofSize = new int(this.styles.size());
		//return copyofSize;
		return cssPaths.size();
	}
	
    /**
     *  Return a list of the images used on the page. 
     *  It's a list of strings. 
     * 
     * @return	images	a list of the images used on the page.
     */
	public ArrayList<String> getImages() {
		
		return imagePaths;
	}
	
    /**
     *  Return a list of the scripts used on the page. 
     *  It's a list of strings. 
     * 
     * @return	scripts	a list of the scripts used on the page.
     */
	public ArrayList<String> getScripts() {
		
		return scriptPaths;
	}
	
    /**
     *  Return a list of the stylesheets used on the page. 
     *  It's a list of strings. 
     * 
     * @return	styles	a list of the stylesheets used on the page.
     */
	public ArrayList<String> getStyles() {
		
		return cssPaths;
	}
	
    /**
     *  Returns the number of intra-page links as an integer. 
     * 
     * @return	numIntraLinks	the number of intra-page links as an integer. 
     */
	public int getNumIntraLinks() {
		
		//return this.numIntraLinks;
		return numIntraLinks;
	}
	
    /**
     *  Returns the number of inter-page links as an integer. 
     * 
     * @return	numInterLinks	the number of inter-page links as an integer. 
     */
	public int getNumInterLinks() {
		
		//return this.numInterLinks;
		return numInterLinks;
	}
	
    /**
     *  Returns the number of external links on the page as an integer. 
     *  
	 *  // TODO Test getNumExternLinks()
     *  // TODO Implement getNumExternLinks()
     * 
     * @return	numExternLinks	the number of external links s an integer. 
     */
	public int getNumExternLinks() {
		
		//return this.numExternLinks;
		return numExternLinks;
	}
	
    /**
     *  Returns the total size of all images on the page as a double. 
     *  
     * 
     * @return 		the total size of all images on the page as a double. 
     */
	public double getSizeOfImages() {
		
		return this.sizeOfImages;
	}
	
    /**
     *  Return the page level asset tracking object. 
     * 
     * @return assetTracker	the page level asset tracking object. 
     */
	public AssetTracker getAssetsInfo() {
		
		return assetTracking;
	}



}


