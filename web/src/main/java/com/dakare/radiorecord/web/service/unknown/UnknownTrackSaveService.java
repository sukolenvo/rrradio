package com.dakare.radiorecord.web.service.unknown;

import com.dakare.radiorecord.web.controller.event.FailedTrackRequestEvent;
import com.dakare.radiorecord.web.domain.EmptyTrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import com.dakare.radiorecord.web.repository.EmptyTrackInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;

@AllArgsConstructor
public class UnknownTrackSaveService {

    private EmptyTrackInfoRepository emptyTrackInfoRepository;

    @EventListener
    public void handleFailedTrackRequest(FailedTrackRequestEvent failedTrackRequestEvent) {
        TrackInfoPK id = new TrackInfoPK(failedTrackRequestEvent.getArtist(),
                failedTrackRequestEvent.getSong(),
                failedTrackRequestEvent.getStation());
        EmptyTrackInfo emptyTrackInfo = emptyTrackInfoRepository.findOne(id);
        if (emptyTrackInfo == null) {
            emptyTrackInfo = new EmptyTrackInfo();
            emptyTrackInfo.setArtist(id.getArtist());
            emptyTrackInfo.setPrefix(id.getPrefix());
            emptyTrackInfo.setSong(id.getSong());
            emptyTrackInfo.setCount(1);
            emptyTrackInfoRepository.save(emptyTrackInfo);
        } else {
            emptyTrackInfo.setCount(emptyTrackInfo.getCount() + 1);
            emptyTrackInfoRepository.save(emptyTrackInfo);
        }
    }
}
