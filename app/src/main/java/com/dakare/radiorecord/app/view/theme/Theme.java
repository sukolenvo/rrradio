package com.dakare.radiorecord.app.view.theme;

import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Theme {

    CLASSIC(R.string.classic_theme_name) {
        @Override
        public int getStationIcon(final Station station) {
            switch (station) {
                case RADIO_RECORD:
                    return R.drawable.rr_ico;
                case MEGAMIX:
                    return R.drawable.megamix_ico;
                case RECORD_DEEP:
                    return R.drawable.record_deep_ico;
                case RECORD_CLUB:
                    return R.drawable.record_club_ico;
                case FUTURE_HOUSE:
                    return R.drawable.future_house_ico;
                case TRANCEMISSION:
                    return R.drawable.trancemission_ico;
                case RECORD_CHILL_OUT:
                    return R.drawable.record_chill_out_ico;
                case MINIMAL_TECH:
                    return R.drawable.minimal_tech_ico;
                case PIRATE_STATION:
                    return R.drawable.ps_ico;
                case RUSSIAN_MIX:
                    return R.drawable.russian_mix_ico;
                case VIP_HOUSE:
                    return R.drawable.vip_house_ico;
                case SUPERDISCO_90:
                    return R.drawable.superdisco_90_ico;
                case RECORD_BREAKS:
                    return R.drawable.record_breaks_ico;
                case RECORD_BUDSTEP:
                    return R.drawable.record_dubstep_ico;
                case RECORD_DANCECORE:
                    return R.drawable.recrod_dancecore_ico;
                case RECORD_HARDSTYLE:
                    return R.drawable.record_hardstyle_ico;
                case RECORD_TRAP:
                    return R.drawable.record_trap_ico;
                case PUMP:
                    return R.drawable.old_school_ico;
                case RECORD_ROCK:
                    return R.drawable.record_rock_ico;
                case SLOW_DANCE_FM:
                    return R.drawable.slow_song_fm_ico;
                case GOP_FM:
                    return R.drawable.gop_fm_ico;
                case TROPICAL:
                    return R.drawable.tropic_ico;
                case NAFTALIN:
                    return R.drawable.naftalin_ico;
                case YO_FM:
                    return R.drawable.black_ico;
                case RAVE_FM:
                    return R.drawable.rave_fm_ico;
                case GOA:
                    return R.drawable.goa_ico;
                case GOLD:
                    return R.drawable.gold_ico;
                default:
                    return R.drawable.rr_ico;
            }
        }
    },
    LIGHT(R.string.light_theme_name) {
        @Override
        public int getStationIcon(final Station station) {
            switch (station) {
                case RADIO_RECORD:
                    return R.drawable.icon_light_rr;
                case MEGAMIX:
                    return R.drawable.icon_light_mix;
                case RECORD_DEEP:
                    return R.drawable.icon_light_deep;
                case RECORD_CLUB:
                    return R.drawable.icon_light_club;
                case FUTURE_HOUSE:
                    return R.drawable.icon_light_house;
                case TRANCEMISSION:
                    return R.drawable.icon_light_trans;
                case RECORD_CHILL_OUT:
                    return R.drawable.icon_light_chill;
                case MINIMAL_TECH:
                    return R.drawable.icon_light_min;
                case PIRATE_STATION:
                    return R.drawable.icon_light_pirate;
                case RUSSIAN_MIX:
                    return R.drawable.icon_light_rus_mix;
                case VIP_HOUSE:
                    return R.drawable.icon_light_vip;
                case SUPERDISCO_90:
                    return R.drawable.icon_light_super_90;
                case RECORD_BREAKS:
                    return R.drawable.icon_light_breaks;
                case RECORD_BUDSTEP:
                    return R.drawable.icon_light_dubstep;
                case RECORD_DANCECORE:
                    return R.drawable.icon_light_dancecore;
                case RECORD_HARDSTYLE:
                    return R.drawable.icon_light_hardstyle;
                case RECORD_TRAP:
                    return R.drawable.icon_light_trap;
                case PUMP:
                    return R.drawable.icon_light_old_school;
                case RECORD_ROCK:
                    return R.drawable.icon_light_rock;
                case SLOW_DANCE_FM:
                    return R.drawable.icon_light_medl;
                case GOP_FM:
                    return R.drawable.icon_light_gop;
                case TROPICAL:
                    return R.drawable.icon_light_tropical;
                case NAFTALIN:
                    return R.drawable.icon_light_naftalin;
                case YO_FM:
                    return R.drawable.icon_light_black;
                case RAVE_FM:
                    return R.drawable.icon_light_rave;
                case FUTURE_BASS:
                    return R.drawable.icon_light_fbass;
                case REMIX:
                    return R.drawable.icon_light_remix;
                case GASTARBAITER:
                    return R.drawable.icon_light_gastarbaiter;
                case HARD_BASS:
                    return R.drawable.icon_light_hbass;
                case ANSHLAG:
                    return R.drawable.icon_light_anshlag;
                case IBIZA:
                    return R.drawable.icon_light_ibiza;
                case GOA:
                    return R.drawable.icon_light_goa;
                case GOLD:
                    return R.drawable.icon_light_gold;
                case TECHNO:
                    return R.drawable.icon_light_techno;
                default:
                    return R.drawable.icon_light_rr;
            }
        }
    },
    DARK(R.string.dark_theme_name) {
        @Override
        public int getStationIcon(final Station station) {
            switch (station) {
                case RADIO_RECORD:
                    return R.drawable.icon_dark_rr;
                case MEGAMIX:
                    return R.drawable.icon_dark_mix;
                case RECORD_DEEP:
                    return R.drawable.icon_dark_deep;
                case RECORD_CLUB:
                    return R.drawable.icon_dark_club;
                case FUTURE_HOUSE:
                    return R.drawable.icon_dark_house;
                case TRANCEMISSION:
                    return R.drawable.icon_dark_trans;
                case RECORD_CHILL_OUT:
                    return R.drawable.icon_dark_chill;
                case MINIMAL_TECH:
                    return R.drawable.icon_dark_min;
                case PIRATE_STATION:
                    return R.drawable.icon_dark_pirate;
                case RUSSIAN_MIX:
                    return R.drawable.icon_dark_rus_mix;
                case VIP_HOUSE:
                    return R.drawable.icon_dark_vip;
                case SUPERDISCO_90:
                    return R.drawable.icon_dark_super_90;
                case RECORD_BREAKS:
                    return R.drawable.icon_dark_breaks;
                case RECORD_BUDSTEP:
                    return R.drawable.icon_dark_dubstep;
                case RECORD_DANCECORE:
                    return R.drawable.icon_dark_dancecore;
                case RECORD_HARDSTYLE:
                    return R.drawable.icon_dark_hardstyle;
                case RECORD_TRAP:
                    return R.drawable.icon_dark_trap;
                case PUMP:
                    return R.drawable.icon_dark_old_school;
                case RECORD_ROCK:
                    return R.drawable.icon_dark_rock;
                case SLOW_DANCE_FM:
                    return R.drawable.icon_dark_medl;
                case GOP_FM:
                    return R.drawable.icon_dark_gop;
                case TROPICAL:
                    return R.drawable.icon_dark_tropical;
                case NAFTALIN:
                    return R.drawable.icon_dark_naftalin;
                case YO_FM:
                    return R.drawable.icon_dark_black;
                case RAVE_FM:
                    return R.drawable.icon_dark_rave;
                case FUTURE_BASS:
                    return R.drawable.icon_dark_fbass;
                case REMIX:
                    return R.drawable.icon_dark_remix;
                case GASTARBAITER:
                    return R.drawable.icon_dark_gastarbaiter;
                case HARD_BASS:
                    return R.drawable.icon_dark_hbass;
                case ANSHLAG:
                    return R.drawable.icon_dark_anshlag;
                case IBIZA:
                    return R.drawable.icon_dark_ibiza;
                case GOA:
                    return R.drawable.icon_dark_goa;
                case GOLD:
                    return R.drawable.icon_dark_gold;
                case TECHNO:
                    return R.drawable.icon_dark_techno;
                default:
                    return R.drawable.icon_dark_rr;
            }
        }
    };

    private final int nameRes;

    public abstract int getStationIcon(final Station station);
}
