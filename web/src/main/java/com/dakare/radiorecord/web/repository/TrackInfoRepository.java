package com.dakare.radiorecord.web.repository;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackInfoRepository extends JpaRepository<TrackInfo, TrackInfoPK> {

    Page<TrackInfo> findBySongContainsAndPrefix(String song, String prefix, Pageable pageable);
}
