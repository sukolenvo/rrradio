package com.dakare.radiorecord.web.service.tops;

import com.dakare.radiorecord.web.domain.TrackInfo;

import java.util.List;

public interface StationTopsLoader {

    List<TrackInfo> load(String prefix);
}
