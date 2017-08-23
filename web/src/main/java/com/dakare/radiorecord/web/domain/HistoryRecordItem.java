package com.dakare.radiorecord.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "history_tracks", indexes = @Index(name = "title_index", columnList = "title,song"))
public class HistoryRecordItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long trackId;
    private long time;
    @Length(max = 3000)
    private String mp3;
    @Length(max = 500)
    private String title;
    @Length(max = 500)
    private String song;
    @Length(max = 3000)
    private String image600;
    @Length(max = 10)
    private String station;
}
