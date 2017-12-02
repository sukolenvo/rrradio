package com.dakare.radiorecord.app;

import android.util.Log;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Station {

    RADIO_RECORD("Radio Record", "/rr", 8100, 8101),
    FUTURE_HOUSE("Future House", "/fut", 805, 805),
    RECORD_CLUB("EDM", "/club", 8102, 8102),
    MEGAMIX("Megamix", "/mix", 805, 805),
    GOLD("Gold", "/gold", 8102, 8102),
    TRANCEMISSION("Trancemission", "/tm", 8102, 8102),
    PIRATE_STATION("Pirate Station", "/ps", 8102, 8102),
    RECORD_DEEP("Deep", "/deep", 8102, 8102),
    VIP_HOUSE("Vip House", "/vip", 8102, 8102),
    MINIMAL_TECH("Minimal/Tech", "/mini", 805, 805),
    TROPICAL("Tropical", "/trop", 8102, 8102),
    RECORD_CHILL_OUT("Record Chill-Out", "/chil", 8102, 8102),
    RUSSIAN_MIX("Russian Mix", "/rus", 8102, 8102),
    SUPERDISCO_90("Супердиско 90-х", "/sd90", 8102, 8102),
    FUTURE_BASS("Future Bass", "/fbass", 8102, 8102),
    REMIX("Remix", "/rmx", 8102, 8102),
    GASTARBAITER("Гастарбайтер", "/gast", 8102, 8102),
    HARD_BASS("Hard Bass", "/hbass", 8102, 8102),
    ANSHLAG("Аншлаг FM", "/ansh", 8102, 8102),
    IBIZA("Ibiza", "/ibiza", 8102, 8102),
    GOA("GOA/PSY", "/goa", 8102, 8102),
    YO_FM("Black", "/yo", 8102, 8102),
    RECORD_BREAKS("Breaks", "/brks", 8102, 8102),
    PUMP("Old School", "/pump", 8102, 8102),
    TECHNO("Techno", "/techno", 9001, 9002),
    RECORD_TRAP("Trap", "/trap", 8102, 8102),
    RECORD_BUDSTEP("Dubstep", "/dub", 8102, 8102),
    RAVE_FM("Rave FM", "/rave", 8102, 8102),
    RECORD_DANCECORE("Dancecore", "/dc", 8102, 8102),
    NAFTALIN("Нафталин FM", "/naft", 8102, 8102),
    RECORD_ROCK("Rock", "/rock", 8102, 8102),
    SLOW_DANCE_FM("Медляк FM", "/mdl", 8102, 8102),
    GOP_FM("Гоп FM", "/gop", 8102, 8102),
    RECORD_HARDSTYLE("Hardstyle", "/teo", 8102, 8102);

    private final String name;
    private final String code;
    private final int portLow;
    private final int portMedium;

    public String getStreamUrl(final Quality quality) {
        switch (quality) {
            case AAC:
                return "http://air2.radiorecord.ru:9001" + code + "_aac";
            case AAC_64:
                return "http://air2.radiorecord.ru:9001" + code + "_aac_64";
            case MEDIUM:
                return "http://air2.radiorecord.ru:9002" + code + "_128";
            case HIGH:
                return "http://air2.radiorecord.ru:9003" + code + "_320";
            default:
                Log.w("Station",  quality + " quality is not supported");
                return getStreamUrl(Quality.AAC);
        }
    }

    public String getCodeAsParam() {
        return code.substring(1);
    }
}
