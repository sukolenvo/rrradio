package com.dakare.radiorecord.app.load.history;

import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.StationClickListener;

import java.util.List;

public interface HistoryFragmentMediator extends StationClickListener
{
    void moveBack(int level);

    void onDateSelected(Station station, String date);

    void onMusicSelected(List<HistoryMusicItem> items, int position, Station station);
}
