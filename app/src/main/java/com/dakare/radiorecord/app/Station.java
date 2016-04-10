package com.dakare.radiorecord.app;

import lombok.Getter;

@Getter
public enum Station
{

    RADIO_RECORD("Radio Record", R.drawable.rr_ico, "rr"),
    MEGAMIX("Megamix", R.drawable.megamix_ico, "mix"),
    RECORD_DEEP("Record Deep", R.drawable.record_deep_ico, "deep"),
    RECORD_CLUB("Record Club", R.drawable.record_club_ico, "club"),
    FUTURE_HOUSE("Future House", R.drawable.future_house_ico, "fut"),
    TRANCEMISSION("Trancemission", R.drawable.trancemission_ico, "tm"),
    RECORD_CHILL_OUT("Record Chill-Out", R.drawable.record_chill_out_ico, "chil"),
    MINIMAL_TECH("Minimal/Tech", R.drawable.minimal_tech_ico, "mini"),
    PIRATE_STATION("Pirate Station", R.drawable.ps_ico, "ps"),
    RUSSIAN_MIX("Russian Mix", R.drawable.russian_mix_ico, "rus"),
    VIP_HOUSE("Vip House", R.drawable.vip_house_ico, "vip"),
    SUPERDISCO_90("Супердиско 90-х", R.drawable.superdisco_90_ico, "sd90"),
    RECORD_BREAKS("Record Breaks", R.drawable.record_breaks_ico, "brks"),
    RECORD_BUDSTEP("Record Dubstep", R.drawable.record_dubstep_ico, "dub"),
    RECORD_DANCECORE("Record Dancecore", R.drawable.recrod_dancecore_ico, "dc"),
    RECORD_TECHNO("Record Techno", R.drawable.record_techno_ico, "techno"),
    RECORD_HARDSTYLE("Record Hardstyle", R.drawable.record_hardstyle_ico, "teo"),
    RECORD_TRAP("Record Trap", R.drawable.record_trap_ico, "trap"),
    PUMP("Pump", R.drawable.pump_ico, "pump"),
    RECORD_ROCK("Record Rock", R.drawable.record_rock_ico, "rock"),
    SLOW_DANCE_FM("Медляк FM", R.drawable.slow_song_fm_ico, "mdl"),
    GOP_FM("Гоп FM", R.drawable.gop_fm_ico, "gop"),
    YO_FM("Yo!FM", R.drawable.yo_fm_ico, "yo"),
    RAVE_FM("Rave FM", R.drawable.rave_fm_ico, "rave");

    private final String name;
    private final int icon;
    private final String code;

    private Station(final String name, final int icon, final String code)
    {
        this.name = name;
        this.icon = icon;
        this.code = code;
    }
}
