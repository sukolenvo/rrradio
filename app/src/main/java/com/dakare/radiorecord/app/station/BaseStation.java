package com.dakare.radiorecord.app.station;

import android.util.Log;
import com.dakare.radiorecord.app.quality.Quality;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum BaseStation {

    RADIO_RECORD("Radio Record", "/rr"),
    FUTURE_HOUSE("Future House", "/fut"),
    RECORD_CLUB("EDM", "/club"),
    MEGAMIX("Megamix", "/mix"),
    GOLD("Gold", "/gold"),
    TRANCEMISSION("Trancemission", "/tm"),
    PIRATE_STATION("Pirate Station", "/ps"),
    RECORD_DEEP("Deep", "/deep"),
    SYMPHONY("Симфония FM", "/symph"),
    ELECTRO("Electro", "/elect"),
    MIDTEMPO("Midtempo", "/mt"),
    MOOMBAHTON("Moombahton", "/mmbt"),
    JACKING("Jackin'/Garage", "/jackin"),
    PROGRESSIVE("Progressive", "/progr"),
    VIP_HOUSE("Vip House", "/vip"),
    MINIMAL_TECH("Minimal/Tech", "/mini"),
    TROPICAL("Tropical", "/trop"),
    RECORD_CHILL_OUT("Record Chill-Out", "/chil"),
    RUSSIAN_MIX("Russian Mix", "/rus"),
    SUPERDISCO_90("Супердиско 90-х", "/sd90"),
    MAYATNIK_FUKO("Маятник Фуко", "/mf"),
    FUTURE_BASS("Future Bass", "/fbass"),
    REMIX("Remix", "/rmx"),
    GASTARBAITER("Гастарбайтер", "/gast"),
    HARD_BASS("Hard Bass", "/hbass"),
    ANSHLAG("Аншлаг FM", "/ansh"),
    IBIZA("Innocence (Ibiza)", "/ibiza"),
    GOA("GOA/PSY", "/goa"),
    YO_FM("Black", "/yo"),
    RECORD_BREAKS("Breaks", "/brks"),
    PUMP("Old School", "/pump"),
    TECHNO("Techno", "/techno"),
    RECORD_TRAP("Trap", "/trap"),
    RECORD_BUDSTEP("Dubstep", "/dub"),
    RAVE_FM("Rave FM", "/rave"),
    RECORD_DANCECORE("Dancecore", "/dc"),
    NAFTALIN("Нафталин FM", "/naft"),
    RECORD_ROCK("Rock", "/rock"),
    SLOW_DANCE_FM("Медляк FM", "/mdl"),
    GOP_FM("Гоп FM", "/gop"),
    RECORD_HARDSTYLE("Hardstyle", "/teo");

    private final String name;
    private final String code;

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
