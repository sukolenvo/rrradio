package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.view.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Station {

    RADIO_RECORD("Radio Record", "/rr", 8100, 8101),
    MEGAMIX("Megamix", "/mix", 805, 805),
    RECORD_DEEP("Record Deep", "/deep", 8102, 8102),
    RECORD_CLUB("Record Club", "/club", 8102, 8102),
    FUTURE_HOUSE("Future House", "/fut", 805, 805),
    TRANCEMISSION("Trancemission", "/tm", 8102, 8102),
    RECORD_CHILL_OUT("Record Chill-Out", "/chil", 8102, 8102),
    MINIMAL_TECH("Minimal/Tech", "/mini", 805, 805),
    PIRATE_STATION("Pirate Station", "/ps", 8102, 8102),
    RUSSIAN_MIX("Russian Mix", "/rus", 8102, 8102),
    VIP_HOUSE("Vip House", "/vip", 8102, 8102),
    SUPERDISCO_90("Супердиско 90-х", "/sd90", 8102, 8102),
    RECORD_BREAKS("Record Breaks", "/brks", 8102, 8102),
    RECORD_BUDSTEP("Record Dubstep", "/dub", 8102, 8102),
    RECORD_DANCECORE("Record Dancecore", "/dc", 8102, 8102),
    RECORD_TECHNO("Record Techno", "/techno", 805, 805),
    RECORD_HARDSTYLE("Record Hardstyle", "/teo", 8102, 8102),
    RECORD_TRAP("Record Trap", "/trap", 8102, 8102),
    PUMP("Pump", "/pump", 8102, 8102),
    RECORD_ROCK("Record Rock", "/rock", 8102, 8102),
    SLOW_DANCE_FM("Медляк FM", "/mdl", 8102, 8102),
    GOP_FM("Гоп FM", "/gop", 8102, 8102),
    YO_FM("Yo!FM", "/yo", 8102, 8102),
    RAVE_FM("Rave FM", "/rave", 8102, 8102);

    private final String name;
    private final String code;
    private int portLow;
    private int portMedium;

    public String getStreamUrl(final Quality quality) {
        switch (quality) {
            case AAC:
                return "http://air2.radiorecord.ru:805" + code + "_aac";
            case AAC_64:
                return "http://air.radiorecord.ru:805" + code + "_aac_64";
            case LOW:
                return "http://air.radiorecord.ru:" + portLow + code + "_64";
            case MEDIUM:
                return "http://air.radiorecord.ru:" + portMedium + code + "_128";
            case HIGH:
                return "http://air2.radiorecord.ru:805" + code + "_320";
            default:
                throw new UnsupportedOperationException("This quality is not supported");
        }
    }

    public String getCodeAsParam() {
        return code.substring(1);
    }
}
