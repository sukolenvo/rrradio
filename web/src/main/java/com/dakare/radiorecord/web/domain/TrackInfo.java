package com.dakare.radiorecord.web.domain;

import com.dakare.radiorecord.web.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Data
@Entity
@IdClass(TrackInfoPK.class)
@Table(name = "tracks", indexes = @Index(name = "track_index", columnList = "artist,song"))
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackInfo {

    @Id
    private String artist;
    @Id
    private String song;
    @Id
    private String prefix;
    private String image600;

    public void setArtist(String artist) {
        this.artist = Utils.normalize(artist);
    }

    public void setSong(String song) {
        this.song = Utils.normalize(song);
    }

    public void setPrefix(String prefix) {
        this.prefix = Utils.normalize(prefix);
    }

    public boolean checkValid() {
        return !StringUtils.isEmpty(image600) && !StringUtils.isEmpty(song)
                && !StringUtils.isEmpty(artist) && !StringUtils.isEmpty(prefix);
    }
}
