package com.performetriks.gatlytron.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, (c) Copyright 2019 
 * @license MIT-License
 **************************************************************************************************************/
public class GatlytronFiles {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronFiles.class);
	
	private static Cache<String, String> stringFileCache = 
						CacheBuilder.newBuilder()
							.initialCapacity(100)
							.maximumSize(1000)
							.expireAfterAccess(10, TimeUnit.HOURS)
							.build();
		
	
	private static Cache<String, byte[]> byteFileCache = 
			CacheBuilder.newBuilder()
				.initialCapacity(100)
				.maximumSize(1000)
				.expireAfterAccess(10, TimeUnit.HOURS)
				.build();
		

	private static boolean cacheFiles = true;
	
	/** Only packages in this list can be accessed with readPackageResource*().*/
	private static final ArrayList<String> allowedPackages = new ArrayList<String>();
		
	static String[] cachedFiles = new String[15];
	static int fileCounter = 0;
		
	/***********************************************************************
	 * Returns a list of filenames in a directory
	 * 
	 * @param dir the directory.
	 * @param includeDirs toogle if directories should be listed as well.
	 * 
	 * @return Set of filenames
	 * 
	 ***********************************************************************/
	public static Set<String> listFilesInFolder(String dir, boolean includeDirs) {
	    try (Stream<Path> stream = Files.list(Paths.get(dir))) {
	        
//	    	if(!includeDirs) { 
//	    		stream.filter(file -> !Files.isDirectory(file));
//	    	}
		          
	    	return stream
	    	  .filter(file -> (includeDirs || !Files.isDirectory(file)))
	          .map(Path::getFileName)
	          .map(Path::toString)
	          .collect(Collectors.toSet());
	    } catch (IOException e) {
	    	logger
					.error("Could not list files in directory: "+dir, e);
	    	
	    	return new HashSet<>();
		}
	}
	
	/***********************************************************************
	 * Returns the file content of the given file path as a string.
	 * If it fails to read the file it will handle the exception and
	 * will add an alert to the given request.
	 * A file once loaded will 
	 * 
	 * @param request the request that is currently handled
	 * @param path the path 
	 * @param filename the name of the file 
	 * 
	 * @return String content of the file or null if an exception occurred.
	 * 
	 ***********************************************************************/
	public static String getFileContent(String path, String filename){
		return getFileContent(path + "/" + filename);
	}
	
	/***********************************************************************
	 * Returns the file content of the given file path as a string.
	 * If it fails to read the file it will handle the exception and
	 * will add an alert to the given request.
	 * A file once loaded will 
	 * 
	 * @param request the request that is currently handled
	 * @param path the path 
	 * 
	 * @return String content of the file or null if an exception occurred.
	 * 
	 ***********************************************************************/
	public static String getFileContent( String path){
		
		if( GatlytronFiles.stringFileCache.asMap().containsKey(path) && cacheFiles){
			logger.trace("Read file content from cache");
			return GatlytronFiles.stringFileCache.getIfPresent(path);
		}else{
			logger.trace("Read from disk into cache");
			
			try( BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) ){
				
				StringBuilder contentBuffer = new StringBuilder();
				String line;
				
				while( (line = reader.readLine()) != null) {
					contentBuffer.append(line);
					contentBuffer.append("\n");
				}

				String content = contentBuffer.toString();
				GatlytronFiles.stringFileCache.put(path, content);
				
				// remove UTF-8 byte order mark if present
				content = content.replace("\uFEFF", "");
				
				return content;
				
			} catch (IOException e) {
				//TODO: Localize message
				logger
					.error("Could not read file: "+path, e);
				
				return null;
			}
			
		}
	}

	
	/***********************************************************************
	 * Write a string to a file.
	 * 
	 * 
	 * @param request the request that is currently handled
	 * @param path the path 
	 * @param content to be written
	 *   
	 * @return String content of the file or null if an exception occurred.
	 * 
	 ***********************************************************************/
	public static void writeFileContent(String path, String content){
		
		try{
			Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		} catch (IOException e) {
			//TODO: Localize message
			logger
				.error("Could not write file: "+path, e);
		}
			
	}
	
	/*************************************************************
	 * Add a package to the allowed packages that can be accessed
	 * by the readPackageResource*() methods.
	 * 
	 * @param packageName the name of the package e.g. "com.example.resources"
	 *************************************************************/
	public static void addAllowedPackage(String packageName) {
		allowedPackages.add(packageName);
	}
	
	/*************************************************************
	 * Remove a package from the allowed packages that can be accessed
	 * by the readPackageResource*() methods.
	 * 
	 * @param packageName the name of the package e.g. "com.example.resources"
	 *************************************************************/
	public static void removeAllowedPackage(String packageName) {
		allowedPackages.remove(packageName);
	}
	
	/*************************************************************
	 * Check if the package resource is allowed to be accessed.
	 * 
	 * @param packagePath the name of the package e.g. "com.example.resources"
	 *************************************************************/
	public static boolean isAllowedResource(String packagePath) {
		
		boolean isAllowed = false;
		for(String allowed : allowedPackages) {
			if (packagePath.startsWith(allowed)) {
				isAllowed = true;
				break;
			}
		}	
		return isAllowed;
	}
	
	/*************************************************************
	 * Read a resource from the package and caches the file.
	 * @param packageName e.g. "com.xresch.cfw.resources.js.bootstrap.js"
	 * @return content as string or null if not found or not accessible.
	 *************************************************************/
	public static String readPackageResource(String packageName, String filename) {
		String fileContent = null;
		
		if(isAllowedResource(packageName)) {
			
			packageName = packageName.replaceAll("\\.", "/");
			String resourcePath = packageName + "/" + filename;
			
			if(cacheFiles){
				
				try {
					fileContent = stringFileCache.get(resourcePath, new Callable<String>() {

						@Override
						public String call() throws Exception {
							InputStream in = GatlytronFiles.class.getClassLoader().getResourceAsStream(resourcePath);
							String contents = readContentsFromInputStream(in);
							
							if(contents == null) {
								contents = ""; 
								logger.warn("The loaded resource is null: "+resourcePath);
							}
							return contents;
						}
						
					});
				} catch (ExecutionException e) {
					logger.error("Error while loading package resource from cache: "+resourcePath, e);
				}
			}else{
				
				InputStream in = GatlytronFiles.class.getClassLoader().getResourceAsStream(resourcePath);
				fileContent = readContentsFromInputStream(in);
			}
		}else {
			logger.error("Not allowed to read resource from package: "+packageName, new Exception());
		}
		return fileContent;

	}
	
	/*************************************************************
	 * Read a resource from the package.
	 * @param path
	 * @return content as string or null if not found or not accessible.
	 *************************************************************/
	public static byte[] readPackageResourceAsBytes(String packageName, String filename) {
		
		byte[] fileContent = null;
		
		if(isAllowedResource(packageName)) {
			
			packageName = packageName.replaceAll("\\.", "/");
			String resourcePath = packageName + "/" + filename;
			
			if(cacheFiles){
				try {
					fileContent = byteFileCache.get(resourcePath, new Callable<byte[]>() {

						@Override
						public byte[] call() throws Exception {
							InputStream in = GatlytronFiles.class.getClassLoader().getResourceAsStream(resourcePath);
							return readBytesFromInputStream(in);
						}
						
					});
				} catch (ExecutionException e) {
					logger.error("Error while loading package resource from cache: "+resourcePath, e);
				}

			}else{
				InputStream in = GatlytronFiles.class.getClassLoader().getResourceAsStream(resourcePath);
				fileContent = readBytesFromInputStream(in);
			}
		}else {
			logger.error("Not allowed to read resource from package: "+packageName);
		}
		
		return fileContent;

	}

	/*************************************************************
	 * 
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static String readContentsFromInputStream(InputStream inputStream) {
		
		if(inputStream == null) {
			return null;
		}
		
		
		String line = "";
		StringBuilder buffer = new StringBuilder();
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)) ){
			
			while( (line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
				//line = reader.readLine();
			}
			
			// remove last \n
			if(buffer.length() > 0) { buffer.deleteCharAt( buffer.length()-1 ); }
			 
		} catch (IOException e) {
			logger.error("IOException: ", e);
			e.printStackTrace();
		}
		
		String result = buffer.toString();
	
		// remove UTF-8 byte order mark if present
		result = result.replace("\uFEFF", "");
		
		return result;
	}

	/*************************************************************
	 * 
	 * @param path
	 * @return content as string or null if not found.
	 *************************************************************/
	public static byte[] readBytesFromInputStream(InputStream inputStream) {
		
		if(inputStream == null) {
			return null;
		}
		
		StringBuilder stringBuffer = new StringBuilder();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			byte[] buffer = new byte[1];
			//DataInputStream dis = new DataInputStream(inputStream);
			
			for (int nChunk = inputStream.read(buffer); nChunk!=-1; nChunk = inputStream.read(buffer))
			{
				stringBuffer.append(buffer);
				os.write(buffer);
			} 
			 
		} catch (IOException e) {
			logger.error("IOException: ", e);
			e.printStackTrace();
		}
			
		return os.toByteArray();
	}


	
	/***********************************************************************
	 * Caches a file in memory.
	 * @param filename the name of the file
	 * @param fileContent a String representation of the file.
	 * @return nothing
	 ***********************************************************************/
	public static void cacheFileContent(String filename, String fileContent) {
		GatlytronFiles.stringFileCache.put(filename, fileContent);
	}
	
	/***********************************************************************
	 * Check if the files is in the cache.
	 * @param filename the name of the file
	 * @return true or false
	 ***********************************************************************/
	public static boolean isFileCached(String filename) {
		return GatlytronFiles.stringFileCache.asMap().containsKey(filename);
	}
	
	/***********************************************************************
	 * Retrieve a cached file by it's index.
	 * @param index the index of the file to be retrieved
	 ***********************************************************************/
	public static String getCachedFile(String filename) {
		return GatlytronFiles.stringFileCache.asMap().get(filename);
	}
	
	/***********************************************************************
	 * Check if the files exists.
	 * @param filename the name of the file
	 * @return true or false
	 ***********************************************************************/
	public static boolean isFile(String path, String filename) {
		
		return isFile(path+File.separator+filename);
	}
	
	/***********************************************************************
	 * Check if the files exists.
	 * @param filename the name of the file
	 * @return true or false
	 ***********************************************************************/
	public static boolean isFile(String filePath) {
		File file = new File(filePath);
		
		return file.isFile();
	}
	
	/***********************************************************************
	 * Copy files from sourceFolder to targetFolder.
	 * Does not delete existing files in target folder.
	 * Define if existing files with same name should be overridden using 
	 * the parameter doOverride.
	 * @param sourceFolderPath
	 * @param targetFolderPath
	 * @param doOverride
	 * @return true or false
	 ***********************************************************************/
	public static void mergeFolderInto(String sourceFolderPath, String targetFolderPath, boolean doOverride) {
		File targetFolder = new File(targetFolderPath);
		File sourceFolder = new File(sourceFolderPath);
		
		mergeFolderInto(sourceFolder, targetFolder, doOverride);
	}
	/***********************************************************************
	 * Copy files from sourceFolder to targetFolder.
	 * Does not delete existing files in target folder.
	 * Define if existing files with same name should be overridden using 
	 * the parameter doOverride.
	 * @param sourceFolder
	 * @param targetFolder
	 * @param doOverride
	 * @return true or false
	 ***********************************************************************/
	public static void mergeFolderInto(File sourceFolder, File targetFolder, boolean doOverride) {
		
		//------------------------------------
		// Create Target Folder
		if(!targetFolder.isDirectory()) {
			targetFolder.mkdirs();
		}
		
		//------------------------------------
		// Iterate Files and copy
		for(File file : sourceFolder.listFiles()) {
			recursiveCopy(file, targetFolder, doOverride);
		}
		
	}
	
	/***********************************************************************
	 * Recursively copies files into a target folder.
	 * If file is a file, copies the file to the target.
	 * If file is a directory, copies the whole directory.
	 * Define with the doOverride parameter if you want to override existing
	 * files or not.
	 * @param fileToCopy file or folder to copy
	 * @param targetFolder to copy the file to
	 * @param doOverride define if existing files should be overridden.
	 * 
	 ***********************************************************************/
	public static void recursiveCopy(File fileToCopy, File targetFolder, boolean doOverride) {
		
		String targetPath = targetFolder.getPath();
		String filename = fileToCopy.getName();
		String newFileName = targetPath + File.separator + filename;
		File targetFile = new File(newFileName);
		
		if(fileToCopy.isDirectory()) {
			
			if(!targetFile.isDirectory()) {  targetFile.mkdirs(); }
			
			for(File file : fileToCopy.listFiles()) {
				recursiveCopy(file, targetFile, doOverride);
			}
		}else {
			
			if(doOverride || !targetFile.exists()) {
				
				try {
					
					Files.copy(Paths.get(fileToCopy.getPath()), 
							Paths.get(newFileName), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					logger
						.error("Error copying file:"+e.getMessage(), e);
				}
			}
		}
	}
	
	/***********************************************************************
	 * Recursively copies files into a target folder.
	 * If file is a file, copies the file to the target.
	 * If file is a directory, copies the whole directory.
	 * Define with the doOverride parameter if you want to override existing
	 * files or not.
	 * @param fileToCopy file or folder to copy
	 * @param targetFolder to copy the file to
	 * @param doOverride define if existing files should be overridden.
	 * 
	 ***********************************************************************/
	public static void copyFile(String sourceFilePath, String targetFilePath, boolean doOverride) {
		
		if(doOverride || !(new File(targetFilePath).exists()) ) {
			
			try {
				
				Files.copy(Paths.get(sourceFilePath), 
						Paths.get(targetFilePath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger
					.error("Error copying file:"+e.getMessage(), e);
			}
		}
	}
	
}
