package com.dakare.radiorecord.app.load.top;

import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.StationClickListener;

import java.util.List;

public interface TopsFragmentMediator extends StationClickListener
{

    void onItemsSelected(List<TopsMusicItem> items, int position, Station station);
}
