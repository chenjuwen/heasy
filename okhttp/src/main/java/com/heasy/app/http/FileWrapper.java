package com.heasy.app.http;

import java.io.File;

import okhttp3.MediaType;

public class FileWrapper {
	private String name;
	private String filename;
	private MediaType mediaType;
	private File file;
	
	public FileWrapper(){
		
	}
	
	public FileWrapper(String name, String filename, MediaType mediaType, File file){
		this.name = name;
		this.filename = filename;
		this.mediaType = mediaType;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
