package com.dakare.radiorecord.app.load.history;

import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.StationClickListener;

public interface HistoryFragmentMediator extends StationClickListener {
    void moveBack(int level);

    void onDateSelected(Station station, String date);

}
