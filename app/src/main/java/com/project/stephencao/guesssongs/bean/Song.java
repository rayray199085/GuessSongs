package com.project.stephencao.guesssongs.bean;

public class Song {
    private String songName;
    private String fileName;
    private int songNameLength;

    public char[] getSongNameCharacters() {
        return songName.toCharArray();
    }

    public int getSongNameLength() {
        return songNameLength;
    }

    public void setSongNameLength(int songNameLength) {
        this.songNameLength = songNameLength;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
