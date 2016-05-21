package com.dakare.radiorecord.app.player.playlist;

import android.os.Parcel;
import android.os.Parcelable;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.history.HistoryMusicItem;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PlaylistItem implements Parcelable
{
    private final String title;
    private final String subtitle;
    private final String url;
    private final Station station;
    private final boolean live;

    public PlaylistItem(final Parcel parcel)
    {
        title = parcel.readString();
        boolean[] booleans = new boolean[2];
        parcel.readBooleanArray(booleans);
        live = booleans[1];
        if (booleans[0])
        {
            subtitle = parcel.readString();
        } else
        {
            subtitle = null;
        }
        station = Station.valueOf(parcel.readString());
        url = parcel.readString();
    }

    public PlaylistItem(final Station station, final Quality quality)
    {
        this.title = station.getName();
        this.station = station;
        this.url = station.getStreamUrl(quality);
        this.subtitle = quality.getBitrate() + "kb";
        this.live = true;
    }

    public PlaylistItem(final Station station, final HistoryMusicItem historyMusicItem)
    {
        this.title = historyMusicItem.getArtist();
        this.station = station;
        this.url = historyMusicItem.getUrl();
        this.subtitle = historyMusicItem.getSong();
        this.live = false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeString(title);
        dest.writeBooleanArray(new boolean[] {subtitle != null, live});
        if (subtitle != null)
        {
            dest.writeString(subtitle);
        }
        dest.writeString(station.name());
        dest.writeString(url);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PlaylistItem createFromParcel(Parcel in) {
            return new PlaylistItem(in);
        }

        public PlaylistItem[] newArray(int size) {
            return new PlaylistItem[size];
        }
    };
}
