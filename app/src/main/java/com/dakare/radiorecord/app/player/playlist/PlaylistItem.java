package com.dakare.radiorecord.app.player.playlist;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.download.service.DownloadItem;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class PlaylistItem implements Parcelable {
    @Setter
    private String title;
    private String subtitle;
    private String url;
    private Station station;
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
            station = Station.valueOf(parcel.readString());
        } catch (IllegalArgumentException e) {
            Crashlytics.logException(e);
            station = Station.RADIO_RECORD;
        }
        url = parcel.readString();
    }

    public PlaylistItem(final Station station, final Quality quality) {
        this.title = station.getName();
        this.station = station;
        this.url = station.getStreamUrl(quality);
        this.subtitle = RecordApplication.getInstance().getString(quality.getNameRes());
        this.live = true;
    }

    public PlaylistItem(final Station station, final HistoryMusicItem historyMusicItem) {
        this.title = historyMusicItem.getArtist();
        if (station == null) {
            this.station = Station.RADIO_RECORD;
            Answers.getInstance().logCustom(new CustomEvent("error")
                                                    .putCustomAttribute("type", "Station is null")
                                                    .putCustomAttribute("type", "HistoryMusicItem"));
        } else {
            this.station = station;
        }
        this.url = encodeUrl(historyMusicItem.getUrl());
        this.subtitle = historyMusicItem.getSong();
        this.live = false;
    }

    public PlaylistItem(final Station station, final TopsMusicItem item) {
        this.title = item.getArtist();
        if (station == null) {
            this.station = Station.RADIO_RECORD;
            Answers.getInstance().logCustom(new CustomEvent("error")
                                                    .putCustomAttribute("type", "Station is null")
                                                    .putCustomAttribute("type", "TopsMusicItem"));
        } else {
            this.station = station;
        }
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
        this.live = false;
    }

    public PlaylistItem(final SectionMusicItem item) {
        this.title = item.getArtist();
        this.station = Station.RADIO_RECORD;
        this.url = encodeUrl(item.getUrl());
        this.subtitle = item.getSong();
        this.live = false;
    }

    public PlaylistItem(final DownloadItem item) {
        this.title = item.getTitle();
        this.station = Station.RADIO_RECORD;
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
        dest.writeString(station.name());
        dest.writeString(url);
    }

    public Station getStation() {
        return station == null ? Station.RADIO_RECORD : station;
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
