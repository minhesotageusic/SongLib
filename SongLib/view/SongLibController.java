/*
 * Creator: Yunhao Xu, yx245
 * 			Minhesota Geusic, mtg110 
 * */

package SongLib.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import SongLib.app.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

enum ErrorMessageType {
	INVALID_NAME_AND_OR_ARTIST, INVALID_YEAR, DUPLICATE_SONG, DEFAULT_ERROR
}

public class SongLibController {

	//permanent path of the song library file
	public static final String songFile = "SongLib/res/songLib.txt";
	
	private Stage mainStage;

	private ObservableList<Song> obsList;

	public static ArrayList<Song> songs;

	@FXML
	ListView<Song> list;

	// btn
	@FXML
	Button openAddBtn;
	@FXML
	Button openEditBtn;
	@FXML
	Button deleteBtn;
	@FXML
	Button addSongBtn;
	@FXML
	Button cancelAddSongBtn;

	// add song fields
	@FXML
	TextField newSongName;
	@FXML
	TextField newSongArtist;
	@FXML
	TextField newSongAlbum;
	@FXML
	TextField newSongYear;

	// song detail fields
	@FXML
	TextField songName;
	@FXML
	TextField songArtist;
	@FXML
	TextField songAlbum;
	@FXML
	TextField songYear;

	// song edit fields
	@FXML
	TextField editSongName;
	@FXML
	TextField editSongArtist;
	@FXML
	TextField editSongAlbum;
	@FXML
	TextField editSongYear;
	@FXML
	Button applyEditBtn;
	@FXML
	Button cancelEditBtn;

	//delete song fields
	@FXML
	TextField deleteSongName;
	@FXML
	TextField deleteSongArtist;
	@FXML
	TextField deleteSongAlbum;
	@FXML
	TextField deleteSongYear;
	@FXML
	Button confirmDeleteBtn;
	@FXML
	Button cancelDeleteSongBtn;
	
	// children panes
	@FXML
	Pane songDetailsPane;
	@FXML
	Pane addSongPane;
	@FXML
	Pane editSongPane;
	@FXML
	Pane deleteSongPane;
	
	public void OpenAdd(ActionEvent e) {
		showSongAdd();
	}

	public void OpenEdit(ActionEvent e) {
		Song selectedSong = list.getSelectionModel().getSelectedItem();
		if(selectedSong == null) {
			showSongDetails();
			return;
		}
		// set the edit field to be that of the selected song
		editSongName.setText(selectedSong.name);
		editSongArtist.setText(selectedSong.artist);
		editSongAlbum.setText(selectedSong.album);
		editSongYear.setText(selectedSong.year);
		// show song
		showSongEdit();
	}
	
	public void OpenDelete(ActionEvent e) {
		Song selectedSong = list.getSelectionModel().getSelectedItem();
		if(selectedSong == null) {
			showSongDetails();
			return;
		}
		deleteSongName.setText(selectedSong.name);
		deleteSongArtist.setText(selectedSong.artist);
		deleteSongAlbum.setText(selectedSong.album);
		deleteSongYear.setText(selectedSong.year);
		showSongDelete();
	}

	public void Add(ActionEvent e) {
		String _name = newSongName.getText();
		String _artist = newSongArtist.getText();
		String _album = newSongAlbum.getText();
		String _year = newSongYear.getText();

		if (_name == null)
			_name = "";
		if (_artist == null)
			_artist = "";
		if (_album == null)
			_album = "";
		if (_year == null)
			_year = "";

		// trim any leading and trailing white space
		_name.trim();
		_artist.trim();
		_album.trim();
		_year.trim();

		// check if artist name is empty
		if (_name.length() == 0 || _artist.length() == 0) {
			// cant have empty name or artist
			errorMessage(1, mainStage);
			return;
		}
		// check if year is valid
		if (_year.length() != 0 && !parseCheckYear(_year)) {
			// cant have non-digit character as year or a negative year
			errorMessage(2, mainStage);
			return;
		}
		// check if song conflict with another song in the list
		if (!isSongValid(_name, _artist)) {
			// show dialog there are confliction
			errorMessage(3, mainStage);
			return;
		}
		if(_name.contains("|") || _artist.contains("|") || _album.contains("|")) {
			errorMessage(4, mainStage);
			return;
		}
		// create a new song object and
		// added to the observable list
		Song newSong = null;
		newSong = new Song(_name, _artist, _album, _year);
		int index = BinarySearchForSlot(newSong);
		obsList.add(index, newSong);

		// list.setItems(obsList);
		// select the new song
		list.getSelectionModel().select(newSong);

		// clear the input field
		newSongName.clear();
		newSongArtist.clear();
		newSongAlbum.clear();
		newSongYear.clear();

		//push song to file
		
		// show the song details
		showSongDetails();
		displayInfo(list.getSelectionModel().getSelectedItem());
	}

	public void Edit(ActionEvent e) {
		int index = 0;
		boolean editValid;

		String _editName = editSongName.getText();
		String _editArtist = editSongArtist.getText();
		String _editAlbum = editSongAlbum.getText();
		String _editYear = editSongYear.getText();
		// get the current selected item as that is the item
		// we are editing
		Song selectedSong = list.getSelectionModel().getSelectedItem();
		// set edit value to empty if no value were detected
		if (_editName == null)
			_editName = "";
		if (_editArtist == null)
			_editArtist = "";
		if (_editAlbum == null)
			_editAlbum = "";
		if (_editYear == null)
			_editYear = "";

		// remove any white space
		_editName.trim();
		_editArtist.trim();
		_editAlbum.trim();
		_editYear.trim();

		// check if artist name is empty
		if (_editName.length() == 0 || _editArtist.length() == 0) {
			// cant have empty name or artist
			errorMessage(1, mainStage);
			return;
		}
		// check if year is valid
		if (_editYear.length() != 0 && !parseCheckYear(_editYear)) {
			// cant have non-digit character as year or a negative year
			errorMessage(2, mainStage);
			return;
		}
		if(_editName.contains("|") || _editArtist.contains("|") || _editAlbum.contains("|")) {
			errorMessage(4, mainStage);
			return;
		}
		
		// temporary remove selected song
		obsList.remove(selectedSong);
		editValid = isSongValid(_editName, _editArtist);

		// check if song conflict with another song in the list
		if (!editValid) {
			index = BinarySearchForSlot(selectedSong);
			obsList.add(index, selectedSong);
			// show dialog there are conflictions
			errorMessage(3, mainStage);
			return;
		}

		// apply edit
		selectedSong.name = _editName;
		selectedSong.artist = _editArtist;
		selectedSong.album = _editAlbum;
		selectedSong.year = _editYear;
		// add selected song back to the list
		index = BinarySearchForSlot(selectedSong);
		obsList.add(index, selectedSong);

		list.getSelectionModel().select(index);
	}

	public void Delete(ActionEvent e) {
		// get the index of selected song in the list view
		if (list.getSelectionModel().isEmpty()) return;
		int selectedId = list.getSelectionModel().getSelectedIndex();
		
		
		// remove the selected song from the observable list
		obsList.remove(selectedId);
		if (obsList.size() > selectedId && obsList.get(selectedId) != null) {
			list.getSelectionModel().select(selectedId);
			displayInfo(obsList.get(selectedId));
		}
		else if (selectedId > 0 && obsList.get(selectedId-1) != null) {
			list.getSelectionModel().select(selectedId-1);
			displayInfo(obsList.get(selectedId-1));
		}
		else displayInfo(null); // display all blank

		deleteSongName.clear();
		deleteSongArtist.clear();
		deleteSongAlbum.clear();
		deleteSongYear.clear();
		
		showSongDetails();
	}

	// default to enable song details of any currently
	// selected song
	public void Cancel(ActionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn == cancelEditBtn) {
			editSongName.clear();
			editSongArtist.clear();
			editSongAlbum.clear();
			editSongYear.clear();
		} else if (btn == cancelAddSongBtn) {
			newSongName.clear();
			newSongArtist.clear();
			newSongAlbum.clear();
			newSongYear.clear();
		} else if (btn == cancelDeleteSongBtn) {
			deleteSongName.clear();
			deleteSongArtist.clear();
			deleteSongAlbum.clear();
			deleteSongYear.clear();
		}
		showSongDetails();
		displayInfo(list.getSelectionModel().getSelectedItem());
	}

	public void Select(MouseEvent e) {
		// enable song details pane
		showSongDetails();
		displayInfo(list.getSelectionModel().getSelectedItem());
	}

	public void start(Stage owner) {
		mainStage = owner;
		LoadData();
	}

	private void displayInfo(Song song) {
		// clear the previous input
		songName.setText("");
		songArtist.setText("");
		songAlbum.setText("");
		songYear.setText("");

		// if song does not exit, it should mean that the list is empty
		if (song == null)
			return;
		// display the name and artist of the song
		songName.setText(song.name);
		songArtist.setText(song.artist);
		if (song.album != null && song.album.length() != 0)
			songAlbum.setText(song.album);
		if (song.year != null && song.year.length() != 0)
			songYear.setText(song.year);
	}

	private void errorMessage(int option, Stage owner) {
		String errorMessage = "";
		switch (option) {
		case 1:
			errorMessage = "Please enter both name and artist";
			break;
		case 2:
			errorMessage = "Year is not valid";
			break;
		case 3:
			errorMessage = "The song is duplicated";
			break;
		default:
			errorMessage = "Invalid input. Please enter one more time";
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(owner);
		alert.setHeaderText(errorMessage);
		alert.showAndWait();

	}

	// return the slot to insert the given song into the list
	private int BinarySearchForSlot(Song song) {
		int left = 0;
		int right = obsList.size() - 1;

		while (left < right) {
			int mid = (left + right) / 2;
			if (compare(obsList.get(mid), song) == 1) {
				right = mid;
			} else {
				left = mid + 1;
			}
		}
		if (obsList.isEmpty())
			return 0;
		if (compare(obsList.get(left), song) == 1)
			return left;
		return left + 1;

	}

	// compare two given song by name and artist
	private int compare(Song song_in_list, Song new_song) {

		String name_list = song_in_list.name.toLowerCase(), new_name = new_song.name.toLowerCase();

		// if the output is 1, the newly added song precedes the song in the list; if it
		// is 0, the other way.
		int output;
		int result = name_list.compareTo(new_name);
		if (result == 0) {
			output = song_in_list.artist.toLowerCase().compareTo(new_song.artist.toLowerCase()) > 0 ? 1 : 0;
		} else if (result > 0)
			output = 1;
		else
			output = 0;

		return output;
	}

	// not used delete
	private void sort() {
		// sort by alphabetical order using
		// bubble sort
		for (int i = 0; i < obsList.size(); i++) {
			for (int c = 0; c < obsList.size(); c++) {
				int name_r = obsList.get(i).name.compareTo(obsList.get(c).name);
				int artist_r = obsList.get(i).artist.compareTo(obsList.get(c).artist);
				// sort by name then sort by artist
				if (name_r < 0 || (name_r == 0 && artist_r < 0)) {
					Song s = obsList.get(i);
					obsList.set(i, obsList.get(c));
					obsList.set(c, s);
				}
			}
		}
	}

	// check if the given song name and artist name
	// matches with any of the song in the list
	// return false if a match is found
	// return true otherwise
	private boolean isSongValid(String name, String artist) {
		// check for matching name and artist
		// if similar return false, else true
		for (Song s : obsList) {
			if (s.name.equals(name) && s.artist.equals(artist))
				return false;
		}
		return true;
	}

	// check if the given year is only number
	// and it is not a negative number
	private boolean parseCheckYear(String year) {
		for (int i = 0; i < year.length(); i++) {
			if (!Character.isDigit(year.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// toggle song detail visibility
	private void showSongDetails() {
		songDetailsPane.setVisible(true);
		addSongPane.setVisible(false);
		editSongPane.setVisible(false);
		deleteSongPane.setVisible(false);
	}

	// toggle add song visibility
	private void showSongAdd() {
		addSongPane.setVisible(true);
		songDetailsPane.setVisible(false);
		editSongPane.setVisible(false);
		deleteSongPane.setVisible(false);
	}

	// toggle edit song visibility
	private void showSongEdit() {
		editSongPane.setVisible(true);
		songDetailsPane.setVisible(false);
		addSongPane.setVisible(false);
		deleteSongPane.setVisible(false);
	}
	
	private void showSongDelete() {
		deleteSongPane.setVisible(true);
		editSongPane.setVisible(false);
		songDetailsPane.setVisible(false);
		addSongPane.setVisible(false);
	}
	
	public void stop(){
	    try {
	    	FileWriter fw = new FileWriter(songFile);
	    	for(Song s : obsList) {
	    		fw.write(s.name + "," + s.artist + ",");
	    		if(s.album.length() == 0) {
	    			fw.write(" ,");
	    		}else {
	    			fw.write(s.album + ",");
	    		}
	    		if(s.year.length() == 0) {
	    			fw.write(" ");
	    		}else {
	    			fw.write(s.year);
	    		}
	    		fw.write("\r\n");
	    	}
	    	fw.close();
	    }catch(IOException e) {
	    	//changes could not be made
	    }
	}
	
	// load data from a text file
	private void LoadData() {
		ArrayList<Song> songs = new ArrayList<Song>();
		Song selectedSong = null;
		File file = null;
		Scanner scanner = null;
		StringTokenizer token = null;
		String delim = ",";
		try {
			// read the text file storing our song
			file = new File(songFile);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String s = scanner.nextLine();
					String name = null, artist = null, album = "", year = "";
					token = new StringTokenizer(s, delim);
					name = token.nextToken();
					artist = token.nextToken();
					album = token.nextToken().trim();
					year = token.nextToken().trim();
					songs.add(new Song(name, artist, album, year));
				}
			}
		} catch (IOException e) {
		}
		obsList = FXCollections.observableArrayList(songs);
		list.setItems(obsList);
		list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// sort the song by alphabetical order name, then artist name
		sort();

		// select the first song
		if (!obsList.isEmpty()) {
			// select the first song if the list is not empty
			list.getSelectionModel().select(0);
			selectedSong = list.getSelectionModel().getSelectedItem();
		}

		showSongDetails();
		displayInfo(selectedSong);
	}
}
