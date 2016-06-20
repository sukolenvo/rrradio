package com.dakare.radiorecord.app.player.playlist;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class PlaylistItem implements Parcelable
{
    private String title;
    private String subtitle;
    private String url;
    private Station station;
    private boolean live;

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
        this.subtitle = RecordApplication.getInstance().getString(quality.getNameRes());
        this.live = true;
    }

    public PlaylistItem(final Station station, final HistoryMusicItem historyMusicItem)
    {
        this.title = historyMusicItem.getArtist();
        this.station = station;
        this.url = encodeUrl(historyMusicItem.getUrl());
        this.subtitle = historyMusicItem.getSong();
        this.live = false;
    }

    private String encodeUrl(final String url)
    {
        return Uri.encode(url, ":/%");
    }

    public PlaylistItem(final Station station, final TopsMusicItem item)
    {
        this.title = item.getArtist();
        this.station = station;
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
        this.live = false;
    }

    public PlaylistItem(final SectionMusicItem item)
    {
        this.title = item.getArtist();
        this.station = Station.RADIO_RECORD;
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
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
