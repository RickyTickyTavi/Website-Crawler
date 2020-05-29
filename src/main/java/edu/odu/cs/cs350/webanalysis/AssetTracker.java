package edu.odu.cs.cs350.webanalysis;

import java.util.*;
import java.lang.Integer;



public class AssetTracker{
    private HashMap<String, Set<String>> imageUsedOn = new HashMap<>();
    private HashMap<String, Double> archiveSizes = new HashMap<>();
    private HashMap<String, Double> videoSizes = new HashMap<>();
    private HashMap<String, Double> audioSizes = new HashMap<>();
    private HashMap<String, Double> uncategorizedSizes = new HashMap<>();
    
    /**
     * Default constructor for AssetTracker.
     * AssetTracker should always be created by taking in one argument,
     * the absolute path to the website.
     * 
     * When invoking the default constructor, the program will thrown an IllegalArgumentException
     */
    public AssetTracker(){
    	//throw new IllegalArgumentException(Website.defaultConstructorException + this.getClass().getSimpleName());
    }
    
    /**
     * This function places all images found within a page into a map.
     * The map serves to store all unique images with a counter for how
     * many times that image appears throughout a website. The map will
     * be exported to JSON.
     * @param	path	The absolute path to the website
     */
    public void addImage(String UID, String location) {
    	if(imageUsedOn.containsKey(UID))
    		imageUsedOn.get(UID).add(location);
    	else {
    		Set<String> locationList = new HashSet<>();
    		locationList.add(location);
    		imageUsedOn.put(UID, locationList);
    	}
    }

    /**
     * Add archive file to archiveCounter Map
     * @param	path	The absolute path to the website
     * @param	size	File size
     */
    public void addArchive(String path, Double size) {
    	archiveSizes.put(path, size);
    }
    
    /**
     * Add video to videoCounter Map
     * @param	path	The absolute path to the website
     * @param	size	File size
     */
    public void addVideo(String path, Double size) {
    	videoSizes.put(path, size);
    }
    
    /**
     * Add audio file to audioCounter Map
     * @param	path	The absolute path to the website
     * @param	size	File size
     */
    public void addAudio(String path, Double size) {
    	audioSizes.put(path, size);
    }
    
    /**
     * Add uncategorized file to uncategorizedCounter Map
     * @param	path	The absolute path to the website
     * @param	size	File size
     */
    public void addUncategorized(String path, Double size) {
    	uncategorizedSizes.put(path, size);
    }
    
    /**
     * Add AssetTracker values to this AssetTracker
     * @param	rhs	AssetTracker to compare with
     */
    public void addAssetTracker(AssetTracker rhs) {
    	for (Map.Entry<String, Set<String>> entry : rhs.imageUsedOn.entrySet()) {
    		if (imageUsedOn.containsKey(entry)){
    			Set<String> tempList = imageUsedOn.get(entry);
    			tempList.addAll(rhs.imageUsedOn.get(entry));
    			imageUsedOn.put(entry.getKey(), tempList);
    		}
    		else {
    			imageUsedOn.put(entry.getKey(), entry.getValue());
    		}
    	}
    	
    	for (Map.Entry<String, Double> entry : rhs.archiveSizes.entrySet()){
    		archiveSizes.putIfAbsent(entry.getKey(), entry.getValue());
    	}
    	
    	for (Map.Entry<String, Double> entry : rhs.videoSizes.entrySet()){
    		videoSizes.putIfAbsent(entry.getKey(), entry.getValue());
    	}
    	
    	for (Map.Entry<String, Double> entry : rhs.audioSizes.entrySet()){
    		audioSizes.putIfAbsent(entry.getKey(), entry.getValue());
    	}
    	
    	for (Map.Entry<String, Double> entry : rhs.uncategorizedSizes.entrySet()){
    		uncategorizedSizes.putIfAbsent(entry.getKey(), entry.getValue());
    	}
    }
    
    /**
     * Return the Map of images and their locations
     * @return	null	map of paths to images and a list of where each is found. 
     */
    public Map<String, Set<String>> getImageUsage(){
    	return imageUsedOn;
    }
    
    /**
     * Return the map of archive files and their sizes
     * @return	null	map of paths to asset and their respective sizes. 
     */
    public Map<String, Double> getArchiveSizes(){
    	return archiveSizes;
    }
    
    /**
     * Return the map of videos and their sizes
     * @return	null	map of paths to asset and their respective sizes. 
     */
    public Map<String, Double> getVideoSizes(){
    	return videoSizes;
    }
    
    /**
     * Return the map of audio files and their sizes
     * @return	null	map of paths to asset and their respective sizes. 
     */
    public Map<String, Double> getAudioSizes(){
    	return audioSizes;
    }
    
    /**
     * Return the map of uncategorized files and their sizes
     * @return	null	map of paths to asset and their respective sizes. 
     */
    public Map<String, Double> getUncategorizedSizes(){
    	return uncategorizedSizes;
    }
    
    /**
     * Test assetTracker for equality
     * 
     * @param	rhs	The AssetTracker to be tested against. 
     * 
     * @return	boolean	The truth value. 
     */
    @Override
    public boolean equals(Object rhs) {
    	AssetTracker check = (AssetTracker)rhs; 
    	if (this.getImageUsage().toString().equals(check.getImageUsage().toString()) &&
    			this.getArchiveSizes().equals(check.getArchiveSizes()) &&
    			this.getAudioSizes().equals(check.getAudioSizes()) &&
    			this.getVideoSizes().equals(this.getVideoSizes()) &&
    			this.getUncategorizedSizes().equals(check.getUncategorizedSizes())) {
    						return true; 
    				
    	}
    	return false;
    }
}
