package com.dakare.radiorecord.web.repository;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackInfoRepository extends JpaRepository<TrackInfo, TrackInfoPK>{
}
