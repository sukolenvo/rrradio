package com.dakare.radiorecord.app.load.history;

import com.dakare.radiorecord.app.StationClickListener;
import com.dakare.radiorecord.app.station.DynamicStation;

public interface HistoryFragmentMediator extends StationClickListener {
    void moveBack(int level);

    void onDateSelected(DynamicStation station, String date);

}
