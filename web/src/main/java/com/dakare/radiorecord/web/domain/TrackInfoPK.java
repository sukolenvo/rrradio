package com.dakare.radiorecord.web.domain;

import com.dakare.radiorecord.web.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TrackInfoPK implements Serializable {

    private String artist;
    private String song;
    private String prefix;

    public TrackInfoPK(String artist, String song, String prefix) {
        this.artist = Utils.normalize(artist);
        this.song = Utils.normalize(song);
        this.prefix = Utils.normalize(prefix);
    }

    public void setArtist(String artist) {
        this.artist = Utils.normalize(artist);
    }

    public void setSong(String song) {
        this.song = Utils.normalize(song);
    }

    public void setPrefix(String prefix) {
        this.prefix = Utils.normalize(prefix);
    }
}
