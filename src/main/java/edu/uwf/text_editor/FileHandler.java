package edu.uwf.text_editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class FileHandler {
	private boolean isDirty;
	File fileCurrentlyOpen;
	
	public FileHandler() {
		setIsDirty(false);
		setFileCurrentlyOpen(null);
	}
	
	
	public String openFile(File file) {
		try {
			setFileCurrentlyOpen(file);
			
			Path path = file.toPath();
			String fileText = Files.readString(path);
			
			setIsDirty(false);
			return fileText;
		}
		catch(IOException exception) {
			System.err.println("Error opening or reading from file: " + exception.getMessage());
		}
		return "Error";
	}
	
	public void saveFile(String textAreaText) {
		//File was just written and was not opened
		try {
			Files.writeString(fileCurrentlyOpen.toPath(), textAreaText);
			setIsDirty(false);
			System.out.println("File saved");
		}
		catch(IOException exception) {
			System.err.println("Error saving file: " + exception.getMessage());
		}
	}
	
	
	public void setFileCurrentlyOpen(File file) {
		fileCurrentlyOpen = file;
	}
	
	public boolean getIfFileExistsForThisText() {
		if(fileCurrentlyOpen == null) {
			return false;
		}
		return true;
	}
	
	public void setIsDirty(boolean isDirty) {
		this.isDirty = isDirty;
		System.out.println("Text is Dirty: " + isDirty);
	}
	
	public boolean getIsDirty() {
		return isDirty;
	}
	
	public String getFileName() {
		return fileCurrentlyOpen.getName();
	}

}
