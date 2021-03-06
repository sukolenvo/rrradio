package com.dakare.radiorecord.app.player.playlist;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.download.service.DownloadItem;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.station.DynamicStation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaylistItem implements Parcelable {
    private String title;
    private String subtitle;
    private String url;
    private DynamicStation station;
    private boolean live;

    public PlaylistItem(final PlaylistItem item) {
        this.title = item.title;
        this.subtitle = item.subtitle;
        this.url = item.url;
        this.station = item.station;
        this.live = item.live;
    }

    public PlaylistItem(final Parcel parcel) {
        title = parcel.readString();
        boolean[] booleans = new boolean[2];
        parcel.readBooleanArray(booleans);
        live = booleans[1];
        if (booleans[0]) {
            subtitle = parcel.readString();
        } else {
            subtitle = null;
        }
        try {
            station = DynamicStation.deserialize(parcel.readString());
        } catch (IllegalArgumentException e) {
            station = DynamicStation.DEFAULT;
        }
        url = parcel.readString();
    }

    public PlaylistItem(final DynamicStation station, final Quality quality) {
        this.title = station.getName();
        this.station = station;
        this.url = station.getStreamUrl(quality);
        this.subtitle = RecordApplication.getInstance().getString(quality.getNameRes());
        this.live = true;
    }

    public PlaylistItem(final DynamicStation station, final HistoryMusicItem historyMusicItem) {
        this.title = historyMusicItem.getArtist();
        if (station == null) {
            this.station = DynamicStation.DEFAULT;
        } else {
            this.station = station;
        }
        this.url = encodeUrl(historyMusicItem.getUrl());
        this.subtitle = historyMusicItem.getSong();
        this.live = false;
    }

    public PlaylistItem(final DynamicStation station, final TopsMusicItem item) {
        this.title = item.getArtist();
        if (station == null) {
            this.station = DynamicStation.DEFAULT;
        } else {
            this.station = station;
        }
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
        this.live = false;
    }

    public PlaylistItem(final SectionMusicItem item) {
        this.title = item.getArtist();
        this.station = DynamicStation.DEFAULT;
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
        this.live = false;
    }

    public PlaylistItem(final DownloadItem item) {
        this.title = item.getTitle();
        this.station = DynamicStation.DEFAULT;
        this.url = item.getFileUri().toString();
        this.subtitle = item.getSubtitle();
        this.live = false;
    }

    private String encodeUrl(final String url) {
        return Uri.encode(url, ":/%");
    }

    public boolean isDownloadable() {
        return !live && !url.startsWith("file://");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeBooleanArray(new boolean[]{subtitle != null, live});
        if (subtitle != null) {
            dest.writeString(subtitle);
        }
        dest.writeString(station.serialize());
        dest.writeString(url);
    }

    public DynamicStation getStation() {
        return station == null ? DynamicStation.DEFAULT : station;
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
