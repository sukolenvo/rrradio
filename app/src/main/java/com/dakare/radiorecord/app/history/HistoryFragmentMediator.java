package com.dakare.radiorecord.app.history;

import com.dakare.radiorecord.app.Station;

import java.util.List;

public interface HistoryFragmentMediator
{
    void onStationSelected(Station station);

    void moveBack(int level);

    void onDateSelected(Station station, String date);

    void onMusicSelected(List<HistoryMusicItem> items, int position, Station station);
}
