package com.dakare.radiorecord.app.history;

import com.dakare.radiorecord.app.Station;

public interface HistoryFragmentMediator
{
    void onStationSelected(Station station);

    void moveBack(int level);

    void onDateSelected(Station station, String date);
}
