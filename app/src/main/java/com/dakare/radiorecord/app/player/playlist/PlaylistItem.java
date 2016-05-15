package com.dakare.radiorecord.app.player.playlist;

import android.os.Parcel;
import android.os.Parcelable;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.Getter;

@Getter
public class PlaylistItem implements Parcelable
{
    private static final long NO_DURATION = -1L;

    private final String title;
    private final String subtitle;
    private final long duration;
    private final String url;
    private final Station station;

    public PlaylistItem(final Parcel parcel)
    {
        title = parcel.readString();
        if (parcel.readByte() == 0)
        {
            subtitle = null;
        } else
        {
            subtitle = parcel.readString();
        }
        duration = parcel.readLong();
        station = Station.valueOf(parcel.readString());
        url = parcel.readString();
    }

    public PlaylistItem(final Station station, final Quality quality)
    {
        this.title = station.getName();
        this.subtitle = null;
        this.duration = NO_DURATION;
        this.station = station;
        this.url = station.getStreamUrl(quality);
    }

    public boolean hasDuration()
    {
        return duration != NO_DURATION;
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
        dest.writeByte((byte) (subtitle == null ? 0 : 1));
        if (subtitle != null)
        {
            dest.writeString(subtitle);
        }
        dest.writeLong(duration);
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
