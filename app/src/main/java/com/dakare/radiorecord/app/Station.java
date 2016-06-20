package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.quality.Quality;
import lombok.Getter;

@Getter
public enum Station
{

    RADIO_RECORD("Radio Record", R.drawable.rr_ico, "/rr", 8100, 8101),
    MEGAMIX("Megamix", R.drawable.megamix_ico, "/mix", 805, 805),
    RECORD_DEEP("Record Deep", R.drawable.record_deep_ico, "/deep", 8102, 8102),
    RECORD_CLUB("Record Club", R.drawable.record_club_ico, "/club", 8102, 8102),
    FUTURE_HOUSE("Future House", R.drawable.future_house_ico, "/fut", 805, 805),
    TRANCEMISSION("Trancemission", R.drawable.trancemission_ico, "/tm", 8102, 8102),
    RECORD_CHILL_OUT("Record Chill-Out", R.drawable.record_chill_out_ico, "/chil", 8102, 8102),
    MINIMAL_TECH("Minimal/Tech", R.drawable.minimal_tech_ico, "/mini", 805, 805),
    PIRATE_STATION("Pirate Station", R.drawable.ps_ico, "/ps", 8102, 8102),
    RUSSIAN_MIX("Russian Mix", R.drawable.russian_mix_ico, "/rus", 8102, 8102),
    VIP_HOUSE("Vip House", R.drawable.vip_house_ico, "/vip", 8102, 8102),
    SUPERDISCO_90("Супердиско 90-х", R.drawable.superdisco_90_ico, "/sd90", 8102, 8102),
    RECORD_BREAKS("Record Breaks", R.drawable.record_breaks_ico, "/brks", 8102, 8102),
    RECORD_BUDSTEP("Record Dubstep", R.drawable.record_dubstep_ico, "/dub", 8102, 8102),
    RECORD_DANCECORE("Record Dancecore", R.drawable.recrod_dancecore_ico, "/dc", 8102, 8102),
    RECORD_TECHNO("Record Techno", R.drawable.record_techno_ico, "/techno", 805, 805),
    RECORD_HARDSTYLE("Record Hardstyle", R.drawable.record_hardstyle_ico, "/teo", 8102, 8102),
    RECORD_TRAP("Record Trap", R.drawable.record_trap_ico, "/trap", 8102, 8102),
    PUMP("Pump", R.drawable.pump_ico, "/pump", 8102, 8102),
    RECORD_ROCK("Record Rock", R.drawable.record_rock_ico, "/rock", 8102, 8102),
    SLOW_DANCE_FM("Медляк FM", R.drawable.slow_song_fm_ico, "/mdl", 8102, 8102),
    GOP_FM("Гоп FM", R.drawable.gop_fm_ico, "/gop", 8102, 8102),
    YO_FM("Yo!FM", R.drawable.yo_fm_ico, "/yo", 8102, 8102),
    RAVE_FM("Rave FM", R.drawable.rave_fm_ico, "/rave", 8102, 8102);

    private final String name;
    private final int icon;
    private final String code;
    private int portLow;
    private int portMedium;

    Station(final String name, final int icon, final String code, final int portLow, final int portMedium)
    {
        this.name = name;
        this.icon = icon;
        this.code = code;
        this.portLow = portLow;
        this.portMedium = portMedium;
    }

    public String getStreamUrl(final Quality quality)
    {
        switch (quality)
        {
            case AAC:
                return "http://air2.radiorecord.ru:805" + code + "_aac";
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

    public String getCodeAsParam()
    {
        return code.substring(1);
    }
}
