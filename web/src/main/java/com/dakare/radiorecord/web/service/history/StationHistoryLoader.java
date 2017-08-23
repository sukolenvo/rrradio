package com.dakare.radiorecord.web.service.history;

import com.dakare.radiorecord.web.domain.HistoryRecordItem;

import java.util.List;

public interface StationHistoryLoader {

    List<HistoryRecordItem> load(String prefix);
}
