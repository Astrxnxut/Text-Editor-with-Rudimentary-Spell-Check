package edu.uwf.text_editor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellCheck {
	
	final private Dictionary dictionary;
	
	SpellCheck(){
		spellCheckEnabled = false;
		dictionary = new Dictionary();
	}
	
	public void setSpellCheckEnabled(Boolean spellCheckEnabled) {
		System.out.println("Spell Check Enabled: " + spellCheckEnabled);
		this.spellCheckEnabled = spellCheckEnabled;
	}
	
	public boolean isValidWord(String word) {
		return dictionary.doesWordExist(word.toLowerCase());
	}
	
	public Set<String> getSuggestions(String errorWord){
		String lowerCaseErrorWord = errorWord.toLowerCase();
		Set<String> suggestions = new HashSet<String>();
		
		//Check for missing letter
		suggestions.addAll(getMissingLetterSuggestions(lowerCaseErrorWord));
		
		//Check for extra letter
		suggestions.addAll(getExtraLetterSuggestions(lowerCaseErrorWord));
		
		//Check for reversed adjacent letters
		suggestions.addAll(getReversedAdjacentLetterSuggestions(lowerCaseErrorWord));
		
		//Check for wrong letter
		suggestions.addAll(getWrongLetterSuggestions(lowerCaseErrorWord));
	
		return suggestions;
	}
	
	private Set<String> getMissingLetterSuggestions(String errorWord){
		Set<String> missingLetterSuggestions = new HashSet<String>();
		final String[] ALPHABET = {"a","b","c","d","e","f","g","h","i",
				 				   "j","k","l","m","n","o","p","q","r",
				 				   "s","t","u","v","w","x","y","z"};
		
		//Iterates through each position of the word
		for(int i = 0; i <= errorWord.length(); ++i) {
			
			//Iterates through each letter of the alphabet
			for(int a = 0; a < ALPHABET.length; ++a) {
				String suggestionWord = "";
				
				//We place letter in the middle of end of word
				if(i > 0 && i < errorWord.length()) {
					suggestionWord = errorWord.substring(0,i) + ALPHABET[a] + errorWord.substring(i);
				}
				//We place letter at the end of the word
				else if(i == errorWord.length()) {
					suggestionWord = errorWord.concat(ALPHABET[a]);
				}
				//We place letter at the beginning of the word
				else {
					suggestionWord = ALPHABET[a].concat(errorWord);
				}
				
				//If the errorWord with the added letter exists,
				//we add it to the suggestions
				if(dictionary.doesWordExist(suggestionWord)) {
					missingLetterSuggestions.add(suggestionWord);
				}
			}
		}
		
		return missingLetterSuggestions;
	}
	
	private Set<String> getExtraLetterSuggestions(String errorWord){
		Set<String> extraLetterSuggestions = new HashSet<String>();
		
		for(int i = 0; i < errorWord.length(); ++i) {
			String suggestionWord = "";
			
			//Removing letter in the middle of word
			if(i > 0 && i < errorWord.length() - 1) {
				suggestionWord = errorWord.substring(0,i) + errorWord.substring(i + 1);
			}
			//Removing letter at the end of word
			else if(i == errorWord.length() - 1) {
				suggestionWord = errorWord.substring(0,i);
			}
			//Removing letter at the beginning of word
			else
			{
				suggestionWord = errorWord.substring(i + 1);
			}
			
			if(dictionary.doesWordExist(suggestionWord) == true) {
				extraLetterSuggestions.add(suggestionWord);
			}
		}
		return extraLetterSuggestions;
	}
	
	private Set<String> getReversedAdjacentLetterSuggestions(String errorWord){
		Set<String> reversedAdjacentLetterSuggestions = new HashSet<String>();
		/*
		 * length minus 2 because we will be swapping like this
		 * 0 and 1, 1 and 2, 2 and 3. Lets say 3 is the final index
		 * We want to stop before that cause we perform a 3 and 4 swap
		 * and because index 4 doesnt exist, we'd throw an OutOfBounds Exception
		 */
		for(int i  = 0; i <= errorWord.length() - 2; ++i) {
			String suggestionWord = "";
			
			char firstLetter = errorWord.charAt(i);
			char secondLetter = errorWord.charAt(i + 1);
			
			//We are reversing characters in the middle
			if(i > 0 && i < errorWord.length() - 2) {
				suggestionWord = errorWord.substring(0,i) + secondLetter + firstLetter + errorWord.substring(i + 2);
			}
			//We are reversing characters are the end of the word
			else if(i == errorWord.length() - 2) {
				suggestionWord = errorWord.substring(0,i) + secondLetter + firstLetter;
			}
			//We are reversing characters at the beginning of the word
			else {
				
				//No need to error check cause substring(length of word) will return a empty string, ""
				//Have to put an empty string at the front or the chars will add each others ASCII code together
				suggestionWord = "" + secondLetter + firstLetter + errorWord.substring(i + 2);
			}
			
			if(dictionary.doesWordExist(suggestionWord) == true) {
				reversedAdjacentLetterSuggestions.add(suggestionWord);
			}
		}
		
		return reversedAdjacentLetterSuggestions;
	}
	
	private Set<String> getWrongLetterSuggestions(String errorWord){
		Set<String> wrongLetterSuggestions = new HashSet<String>();
		
		final String[] ALPHABET = {"a","b","c","d","e","f","g","h","i",
				   "j","k","l","m","n","o","p","q","r",
				   "s","t","u","v","w","x","y","z"};
		
		//Iterates through each letter of errorWord
		for(int i = 0; i < errorWord.length(); ++i) {
			
			//Iterates through each letter in alphabet
			for(int a = 0; a < ALPHABET.length; ++a) {
				String suggestion = "";
				
				//Replacing a char in the middle of word
				if(i > 0 && i < errorWord.length() - 1) {
					suggestion = errorWord.substring(0,i) + ALPHABET[a] + errorWord.substring(i + 1);
				}
				//Replacing a char at the end of word
				else if(i == errorWord.length() - 1) {
					suggestion = errorWord.substring(0,i) + ALPHABET[a];
				}
				//Replacing char at the beginning of word
				else {
					suggestion = ALPHABET[a] + errorWord.substring(i + 1);
				}
				
				if(dictionary.doesWordExist(suggestion) == true) {
					wrongLetterSuggestions.add(suggestion);
				}
			}
		}
		return wrongLetterSuggestions;
	}
	
//	public void testMethodForSuggestions(String wordToFindSuggestionFor){
//		System.out.println("Suggestions for '" + wordToFindSuggestionFor + "':");
//		for(String suggestion : getSuggestions(wordToFindSuggestionFor)){
//			System.out.println(suggestion);
//		}
//	}
	
	public boolean getSpellCheckEnabled() {
		return spellCheckEnabled;
	}
	private boolean spellCheckEnabled;
}
