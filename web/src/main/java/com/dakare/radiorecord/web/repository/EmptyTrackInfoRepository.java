package com.dakare.radiorecord.web.repository;

import com.dakare.radiorecord.web.domain.EmptyTrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmptyTrackInfoRepository extends JpaRepository<EmptyTrackInfo, TrackInfoPK> {

    Page<EmptyTrackInfo> findByHiddenFalse(Pageable pageable);
}
