package edu.uwf.text_editor;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.uwf.text_editor.FileHandler;

public class TextEditorApp extends Application {
	
	//File Handler
	final FileHandler fileHandler = new FileHandler();
	
	//Spell Check
	final SpellCheck spellCheck = new SpellCheck();
	
	//Layouts
	VBox mainLayout;
	
	//Pause
	PauseTransition pause;
	
	//Menu Bar
	MenuBar menu;
	
	//Menu
	Menu fileNameText;
	Menu savedText;
	Menu fileMenu;
	Menu spellCheckBox;
	Menu spellCheckSuggestions;

	//Menu Item
	MenuItem open;
	MenuItem save;
	MenuItem saveAs;
	MenuItem exit;
	
	//Text Area
	TextArea textField;
	
	//Suggestion Ignore Set
	Set<String> ignoreList;
	
	//Suggestion Words already added
	Set<String> alreadyAddedList;
	
	@Override
	public void start(Stage stage) {
		
		ignoreList = new HashSet<String>();
		alreadyAddedList = new HashSet<String>();
		
		mainLayout = new VBox();
		pause = new PauseTransition(Duration.seconds(5));
		//Add all javafx components
		
		
		fileNameText = new Menu("Untitled");
		fileNameText.setDisable(true);
		fileNameText.setStyle("-fx-opacity: 1.0;");
		
		savedText = new Menu("");
		savedText.setDisable(true);
		savedText.setStyle("-fx-opacity: 1.0;");
		
		spellCheckSuggestions = new Menu("");
		spellCheckSuggestions.setDisable(true);
		
		MenuItem dummyMenuItem = new MenuItem();
		spellCheckBox = new Menu("Start Spell Check");
		spellCheckBox.setStyle("-fx-text-base-color: black; -fx-opacity: 0.4");
		
		spellCheckBox.getItems().add(dummyMenuItem);
		spellCheckBox.setOnShowing(event ->{
			Platform.runLater(() -> spellCheckBox.hide());
			
			if(spellCheck.getSpellCheckEnabled() == false && textField.getText().length() > 0) {
				
				Platform.runLater(() -> {
					spellCheckBox.setDisable(true);
					spellCheckBox.setStyle("-fx-text-base-color: #00BFFF; -fx-opacity: 1.0");
					spellCheckBox.setText("Spell Check Active");
				});
				spellCheck.setSpellCheckEnabled(true);
				
				spellCheckSuggestions.getItems().clear();
				alreadyAddedList.clear();
				ignoreList.clear();
				
				//Create thread to run spell checker on
				Thread thread = new Thread(()-> {
					System.out.println("Spell Check Executing");
					
					try{
						Thread.sleep(1500);
					}
					catch(InterruptedException exception) {
						
					}
					
					final String textToIterateThrough = textField.getText();
					String wordToCheck = "";
					
					for(int i = 0; i < textToIterateThrough.length(); ++i) {
						char character = textToIterateThrough.charAt(i);
						
						if(Character.isLetter(character) == true) {
							wordToCheck += character;
							
							//Checks to see if we are at the end of the iterate string
							if(i + 1 == textToIterateThrough.length()) {
								if(wordToCheck.length() > 1 && 
								   alreadyAddedList.contains(wordToCheck.toLowerCase()) == false &&
								   spellCheck.isValidWord(wordToCheck.toLowerCase()) == false) {
									
									ComboBox<String> suggestionBox = new ComboBox<String>();
									List<String> rawList = new ArrayList<>();
									rawList.addAll(spellCheck.getSuggestions(wordToCheck.toLowerCase()));
									suggestionBox.setItems(FXCollections.observableArrayList(rawList));
									
									suggestionBox.setValue(wordToCheck);
									
									final String wordToReplace = wordToCheck;
									CustomMenuItem suggestionItem = new CustomMenuItem(suggestionBox);
									suggestionItem.setHideOnClick(false);
									
									suggestionBox.setOnAction(new EventHandler<ActionEvent>(){
										public void handle(ActionEvent event) {
											findWordInTextFieldAndReplace(wordToReplace,suggestionBox.getValue());
											
											Platform.runLater(()->{
												int selectedItem = suggestionBox.getSelectionModel().getSelectedIndex();
												
												if(selectedItem == -1 || suggestionBox.getValue().equals(wordToReplace)) {
													return;
												}
												suggestionBox.getItems().remove(selectedItem);
												spellCheckSuggestions.getItems().remove(suggestionItem);
												
												if (spellCheckSuggestions.getItems().isEmpty()) {
									                spellCheckSuggestions.setText("No Misspellings Found");
									                spellCheckSuggestions.setDisable(true);
									            }
											});
										}
									});
									
									//Logic for adding suggestion to menu item
									
									spellCheckSuggestions.getItems().add(suggestionItem);
									alreadyAddedList.add(wordToCheck.toLowerCase());
								}
							}
						}
						//If character is not a letter
						else {
							if(wordToCheck.length() > 1 && 
							   alreadyAddedList.contains(wordToCheck.toLowerCase()) == false &&
							   spellCheck.isValidWord(wordToCheck.toLowerCase()) == false) {
								
								ComboBox<String> suggestionBox = new ComboBox<String>();
								List<String> rawList = new ArrayList<>();
								rawList.addAll(spellCheck.getSuggestions(wordToCheck.toLowerCase()));
								suggestionBox.setItems(FXCollections.observableArrayList(rawList));
								
								suggestionBox.setValue(wordToCheck);
								
								final String wordToReplace = wordToCheck;
								CustomMenuItem suggestionItem = new CustomMenuItem(suggestionBox);
								suggestionItem.setHideOnClick(false);
								
								suggestionBox.setOnAction(new EventHandler<ActionEvent>(){
									public void handle(ActionEvent event) {
										findWordInTextFieldAndReplace(wordToReplace,suggestionBox.getValue());
										
										Platform.runLater(()->{
											int selectedItem = suggestionBox.getSelectionModel().getSelectedIndex();
											
											if(selectedItem == -1 || suggestionBox.getValue().equals(wordToReplace)) {
												return;
											}
											suggestionBox.getItems().remove(selectedItem);
											spellCheckSuggestions.getItems().remove(suggestionItem);
											
											if (spellCheckSuggestions.getItems().isEmpty()) {
								                spellCheckSuggestions.setText("No Misspellings Found");
								                spellCheckSuggestions.setDisable(true);
								            }
										});
									}
								});
								
								//Logic for adding suggestion to menu item
								
								spellCheckSuggestions.getItems().add(suggestionItem);
								alreadyAddedList.add(wordToCheck.toLowerCase());
							}
							wordToCheck = "";
						}
					}
					
					System.out.println("Spell Check Done Executing");
					
					Platform.runLater(() -> {
						spellCheckSuggestions.setText("Spell Check Suggestions");
						spellCheckSuggestions.setDisable(false);
						spellCheckBox.setStyle("-fx-text-base-color: black; -fx-opacity: 0.4");
						spellCheckBox.setText("Start Spell Check");
						spellCheckBox.setDisable(false);
					});
					spellCheck.setSpellCheckEnabled(false);
				});
				
				thread.start();
			}
		});
		
		//Making the text field
		textField = new TextArea();
		textField.textProperty().addListener((observable,oldValue,newValue) ->{
			if(fileHandler.getIsDirty() == false) {
				fileHandler.setIsDirty(true);
				savedText.setText("Not saved");
			}
		});
		
		VBox.setVgrow(textField, Priority.ALWAYS);
		textField.setMaxWidth(Double.MAX_VALUE);
		
		//Add menu items to a menu bar for the top of the application
		menu = new MenuBar();
		fileMenu = new Menu("File");
		
		
		open = new MenuItem("Open");
		open.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeOpenLogic(stage);
			}
		});
		
		save = new MenuItem("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeSaveLogic(stage);
			}
		});
		
		saveAs = new MenuItem("Save As..");
		saveAs.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeSaveAsLogic(stage);
			}
		});
		
		exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeExitLogic(stage);
			}
		});
		
		
		fileMenu.getItems().addAll(open,save,saveAs,exit);
		menu.getMenus().addAll(fileMenu,fileNameText,savedText,spellCheckBox, spellCheckSuggestions);
		
		
		//Add layouts to the main layout
		mainLayout.getChildren().add(menu);
		mainLayout.getChildren().add(textField);
		
		//Create the scene
		Scene scene = new Scene(mainLayout,800,600);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void executeSaveLogic(Stage stage) {
		//File already exists for file
		if(fileHandler.getIfFileExistsForThisText() == true) {
			fileHandler.saveFile(textField.getText());
		}
		//File does not exist and we need to make one to save
		else {
			executeSaveAsLogic(stage);
			return;
		}
		
		savedText.setText("Saved");
		fileNameText.setText(fileHandler.getFileName());
		
		pause.setOnFinished(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				savedText.setText("");
			}
		});
		
		pause.playFromStart();
	}
	
	private void findWordInTextFieldAndReplace(String wordToReplace, String replacementWord) {
		final String oldText = textField.getText();
		final String regex = "\\b" + wordToReplace + "\\b";
		final String newText = oldText.replaceAll(regex,replacementWord);
		textField.setText(newText);
		
//		fileHandler.setIsDirty(true);
//		savedText.setText("Not saved");
	}
	
	private void findIfWordHasSuggestionAndIgnore(String wordToIgnore) {
		
	}
	
	private void executeSaveAsLogic(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter(
				"Text Files (*.txt)"
				, "*.txt"));
		
		fileChooser.setTitle("Save Text File");
		
		java.io.File file = fileChooser.showSaveDialog(stage);
		
		if(file != null) {
			fileHandler.setFileCurrentlyOpen(file);
			fileHandler.saveFile(textField.getText());
			savedText.setText("Saved");
			fileNameText.setText(fileHandler.getFileName());
		}
	}
	
	private void executeOpenLogic(Stage stage) {
		if(fileHandler.getIsDirty() == true) {
			showSaveAlertForOpen(stage);
		}
		else {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new ExtensionFilter(
					"Text Files (*.txt)"
					, "*.txt"));
			
			fileChooser.setTitle("Open Text File");
			java.io.File file = fileChooser.showOpenDialog(stage);
			
			if(file != null) {
				textField.setText(fileHandler.openFile(file));
				savedText.setText("");
				fileHandler.setIsDirty(false);
				fileNameText.setText(fileHandler.getFileName());
			}
		}
	}
	
	private void executeExitLogic(Stage stage) {
		if(fileHandler.getIsDirty() == true) {
			showSaveAlertForExit(stage);
		}
		else
		{
			System.exit(0);
		}
	}
	
	private boolean showSaveAlertForExit(Stage stage) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Unsaved Changes");
		alert.setHeaderText("You have unsaved changes in your current document");
		alert.setContentText("Would you like to save this document?");
		
		ButtonType saveButton = new ButtonType("Save then exit");
		ButtonType dontSaveButton = new ButtonType("Don't save then exit");
		ButtonType cancelButton = new ButtonType("Cancel");
		
		alert.getButtonTypes().setAll(saveButton,dontSaveButton,cancelButton);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.isPresent() == true) {
			if(result.get() == saveButton) {
				executeSaveLogic(stage);
				executeExitLogic(stage);
			}
			else if(result.get() == dontSaveButton) {
				System.exit(0);
			}
		}
		return false;
	}
	
	private boolean showSaveAlertForOpen(Stage stage) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Unsaved Changes");
		alert.setHeaderText("You have unsaved changes in your current document");
		alert.setContentText("Would you like to save this document?");
		
		ButtonType saveButton = new ButtonType("Save then open file");
		ButtonType dontSaveButton = new ButtonType("Don't save then open file");
		ButtonType cancelButton = new ButtonType("Cancel");
		
		alert.getButtonTypes().setAll(saveButton,dontSaveButton,cancelButton);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.isPresent() == true) {
			if(result.get() == saveButton) {
				executeSaveLogic(stage);
				executeOpenLogic(stage);
			}
			else if(result.get() == dontSaveButton) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new ExtensionFilter(
						"Text Files (*.txt)"
						, "*.txt"));
				
				fileChooser.setTitle("Open Text File");
				java.io.File file = fileChooser.showOpenDialog(stage);
				
				if(file != null) {
					textField.setText(fileHandler.openFile(file));
					savedText.setText("");
					fileHandler.setIsDirty(false);
					fileNameText.setText(fileHandler.getFileName());
				}
			}
		}
		return false;
	}
}
