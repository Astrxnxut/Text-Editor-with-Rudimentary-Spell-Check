package edu.uwf.text_editor;

import java.util.HashSet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.stream.Stream;

public class Dictionary {
	HashSet<String> dictionary;
	String wordFileName = "src/main/resources/Words.txt";
	
	Dictionary(){
		loadWordsIntoDictionary();
	}
	
	private void loadWordsIntoDictionary() {
		dictionary = new HashSet<String>();
		
		try(Stream<String> inputFile = Files.lines(Paths.get(wordFileName))){
			inputFile.forEach(line ->{
				dictionary.add(line.toLowerCase());
			});
		}
		catch(IOException exception) {
			System.err.println("Error accessing Words.txt file: " + exception.getMessage());
		}
		
		System.out.println("Dictionary Size: " + dictionary.size());
	}
	
	public boolean doesWordExist(String word) {
		if(dictionary.contains(word.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public HashSet<String> getDictionary(){
		return dictionary;
	}
}
