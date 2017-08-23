package com.dakare.radiorecord.web.service.metadata;

import com.dakare.radiorecord.web.domain.TrackInfo;

import java.io.IOException;
import java.util.List;

public interface MetadataLoader {

    List<TrackInfo> load() throws IOException;
}
