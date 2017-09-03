package com.dakare.radiorecord.web.controller.event;

import lombok.Data;

@Data
public class FailedTrackRequestEvent {

    private String artist;
    private String song;
    private String station;
}
