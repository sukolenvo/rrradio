package com.dakare.radiorecord.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@IdClass(TrackInfoPK.class)
@Table(name = "tracks",  indexes = @Index(name = "track_index", columnList = "artist,song"))
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackInfo {

    @Id
    private String artist;
    @Id
    private String song;
    @Id
    private String prefix;
    private String image600;
}
