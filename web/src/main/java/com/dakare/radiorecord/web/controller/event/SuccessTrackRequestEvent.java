package com.dakare.radiorecord.web.controller.event;

import lombok.Data;

@Data
public class SuccessTrackRequestEvent {

    private String artist;
    private String song;
    private String station;
}
