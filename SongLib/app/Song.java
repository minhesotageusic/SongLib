/*
 * Creator: Yunhao Xu, yx245
 * 			Minhesota Geusic, mtg110 
 * */

package SongLib.app;

public class Song {
	public String name;
	public String artist;
	public String album;
	public String year;
	public Song(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}
	public Song(String name, String artist, String album, String year) {
		this(name, artist);
		//if (year <= 0) throw new Exception("Year must be greater than 0");
		this.album = album;
		this.year = year;
	}
	public String toString() {
		return name + ", " + artist;
	}
	
}