package com.dakare.radiorecord.web.domain;

import com.dakare.radiorecord.web.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Data
@Entity
@IdClass(TrackInfoPK.class)
@Table(name = "empty_tracks")
public class EmptyTrackInfo {

    @Id
    private String artist;
    @Id
    private String song;
    @Id
    private String prefix;
    private long count;
    @Column(columnDefinition = "boolean default 'false'")
    private boolean hidden;

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
