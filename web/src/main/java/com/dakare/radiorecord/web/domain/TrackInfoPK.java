package com.dakare.radiorecord.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackInfoPK implements Serializable {

    private String artist;
    private String song;
    private String prefix;
}
