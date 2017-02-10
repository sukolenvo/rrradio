package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.view.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Station {

    RADIO_RECORD("Radio Record", R.drawable.icon_dark_rr, R.drawable.icon_light_rr, "/rr", 8100, 8101),
    MEGAMIX("Megamix", R.drawable.icon_dark_mix, R.drawable.icon_light_mix, "/mix", 805, 805),
    RECORD_DEEP("Record Deep", R.drawable.icon_dark_deep, R.drawable.icon_light_deep, "/deep", 8102, 8102),
    RECORD_CLUB("Record Club", R.drawable.icon_dark_club, R.drawable.icon_light_club, "/club", 8102, 8102),
    FUTURE_HOUSE("Future House", R.drawable.icon_dark_house, R.drawable.icon_light_house, "/fut", 805, 805),
    TRANCEMISSION("Trancemission", R.drawable.icon_dark_trans, R.drawable.icon_light_trans, "/tm", 8102, 8102),
    RECORD_CHILL_OUT("Record Chill-Out", R.drawable.icon_dark_chill, R.drawable.icon_light_chill, "/chil", 8102, 8102),
    MINIMAL_TECH("Minimal/Tech", R.drawable.icon_dark_min, R.drawable.icon_light_min, "/mini", 805, 805),
    PIRATE_STATION("Pirate Station", R.drawable.icon_dark_pirate, R.drawable.icon_light_pirate, "/ps", 8102, 8102),
    RUSSIAN_MIX("Russian Mix", R.drawable.icon_dark_rus_mix, R.drawable.icon_light_rus_mix, "/rus", 8102, 8102),
    VIP_HOUSE("Vip House", R.drawable.icon_dark_vip, R.drawable.icon_light_vip, "/vip", 8102, 8102),
    SUPERDISCO_90("Супердиско 90-х", R.drawable.icon_dark_super_90, R.drawable.icon_light_super_90, "/sd90", 8102, 8102),
    RECORD_BREAKS("Record Breaks", R.drawable.icon_dark_breaks, R.drawable.icon_light_breaks, "/brks", 8102, 8102),
    RECORD_BUDSTEP("Record Dubstep", R.drawable.icon_dark_dubstep, R.drawable.icon_light_dubstep, "/dub", 8102, 8102),
    RECORD_DANCECORE("Record Dancecore", R.drawable.icon_dark_dancecore, R.drawable.icon_light_dancecore, "/dc", 8102, 8102),
    RECORD_TECHNO("Record Techno", R.drawable.icon_dark_techno, R.drawable.icon_light_techno, "/techno", 805, 805),
    RECORD_HARDSTYLE("Record Hardstyle", R.drawable.icon_dark_hardstyle, R.drawable.icon_light_hardstyle, "/teo", 8102, 8102),
    RECORD_TRAP("Record Trap", R.drawable.icon_dark_trap, R.drawable.icon_light_trap, "/trap", 8102, 8102),
    PUMP("Pump", R.drawable.icon_dark_pump, R.drawable.icon_light_pump, "/pump", 8102, 8102),
    RECORD_ROCK("Record Rock", R.drawable.icon_dark_rock, R.drawable.icon_light_rock, "/rock", 8102, 8102),
    SLOW_DANCE_FM("Медляк FM", R.drawable.icon_dark_medl, R.drawable.icon_light_medl, "/mdl", 8102, 8102),
    GOP_FM("Гоп FM", R.drawable.icon_dark_gop, R.drawable.icon_light_gop, "/gop", 8102, 8102),
    YO_FM("Yo!FM", R.drawable.icon_dark_yo, R.drawable.icon_light_yo, "/yo", 8102, 8102),
    RAVE_FM("Rave FM", R.drawable.icon_dark_rave, R.drawable.icon_light_rave, "/rave", 8102, 8102);

    private final String name;
    private final int iconDark;
    private final int iconLight;
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

    public int getIcon() {
        return PreferenceManager.getInstance(RecordApplication.getInstance()).getTheme() == Theme.DARK ? iconDark : iconLight;
    }
}
