package com.dakare.radiorecord.app.station;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.view.theme.Theme;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PredefinedStation extends AbstractStation {

    private final BaseStation baseStation;

    @Override
    public String getName() {
        return baseStation.getName();
    }

    @Override
    public String getCode() {
        return baseStation.getCode();
    }

    @Override
    public Bitmap getNotificationStationIcon() {
        return BitmapFactory.decodeResource(RecordApplication.getInstance().getResources(), getNotificationStationRes());
    }

    private int getNotificationStationRes() {
        switch (baseStation) {
            case RADIO_RECORD:
                return R.drawable.icon_alt_rr;
            case FUTURE_HOUSE:
                return R.drawable.icon_alt_fhouse;
            case RECORD_CLUB:
                return R.drawable.icon_alt_edm;
            case MEGAMIX:
                return R.drawable.icon_alt_megamix;
            case GOLD:
                return R.drawable.icon_alt_gold;
            case TRANCEMISSION:
                return R.drawable.icon_alt_trans;
            case PIRATE_STATION:
                return R.drawable.icon_alt_pirate;
            case RECORD_DEEP:
                return R.drawable.icon_alt_deep;
            case SYMPHONY:
                return R.drawable.icon_alt_symph;
            case ELECTRO:
                return R.drawable.icon_alt_electo;
            case MIDTEMPO:
                return R.drawable.icon_alt_mt;
            case MOOMBAHTON:
                return R.drawable.icon_alt_mmbh;
            case JACKING:
                return R.drawable.icon_alt_jackin;
            case PROGRESSIVE:
                return R.drawable.icon_alt_progr;
            case VIP_HOUSE:
                return R.drawable.icon_alt_vip;
            case MINIMAL_TECH:
                return R.drawable.icon_alt_mtech;
            case TROPICAL:
                return R.drawable.icon_alt_tropical;
            case RECORD_CHILL_OUT:
                return R.drawable.icon_alt_chil_out;
            case RUSSIAN_MIX:
                return R.drawable.icon_alt_rmix;
            case SUPERDISCO_90:
                return R.drawable.icon_alt_sdisco;
            case MAYATNIK_FUKO:
                return R.drawable.icon_alt_mf;
            case FUTURE_BASS:
                return R.drawable.icon_alt_fbass;
            case REMIX:
                return R.drawable.icon_alt_remix;
            case GASTARBAITER:
                return R.drawable.icon_alt_gastarbaiter;
            case HARD_BASS:
                return R.drawable.icon_alt_hbass;
            case ANSHLAG:
                return R.drawable.icon_alt_anshlag;
            case IBIZA:
                return R.drawable.icon_alt_ibiza;
            case GOA:
                return R.drawable.icon_alt_goa;
            case YO_FM:
                return R.drawable.icon_alt_black;
            case RECORD_BREAKS:
                return R.drawable.icon_alt_breaks;
            case PUMP:
                return R.drawable.icon_alt_old_school;
            case TECHNO:
                return R.drawable.icon_alt_techno;
            case RECORD_TRAP:
                return R.drawable.icon_alt_trap;
            case RECORD_BUDSTEP:
                return R.drawable.icon_alt_dubstep;
            case RAVE_FM:
                return R.drawable.icon_alt_rave;
            case RECORD_DANCECORE:
                return R.drawable.icon_alt_dancecore;
            case NAFTALIN:
                return R.drawable.icon_alt_naftalin;
            case RECORD_ROCK:
                return R.drawable.icon_alt_rock;
            case SLOW_DANCE_FM:
                return R.drawable.icon_alt_medlyak;
            case GOP_FM:
                return R.drawable.icon_alt_gop;
            case RECORD_HARDSTYLE:
                return R.drawable.icon_alt_hstyle;
            default:
                return R.drawable.icon_alt_rr;
        }
    }

    @Override
    public Bitmap getStationIcon(Theme theme) {
        return BitmapFactory.decodeResource(RecordApplication.getInstance().getResources(), getStationRes(theme));
    }

    private int getStationRes(Theme theme) {
        switch (theme) {
            case LIGHT:
                return getLightIconRes();
            case DARK:
                return getDarkIconRes();
            case CLASSIC:
            default:
                return getClassicIconRes();
        }
    }

    private int getLightIconRes() {
        switch (baseStation) {
            case RADIO_RECORD:
                return R.drawable.icon_light_rr;
            case MEGAMIX:
                return R.drawable.icon_light_mix;
            case RECORD_DEEP:
                return R.drawable.icon_light_deep;
            case RECORD_CLUB:
                return R.drawable.icon_light_edm;
            case FUTURE_HOUSE:
                return R.drawable.icon_light_fhouse;
            case TRANCEMISSION:
                return R.drawable.icon_light_transmission;
            case RECORD_CHILL_OUT:
                return R.drawable.icon_light_chill_out;
            case MINIMAL_TECH:
                return R.drawable.icon_light_mtech;
            case PIRATE_STATION:
                return R.drawable.icon_light_ps;
            case RUSSIAN_MIX:
                return R.drawable.icon_light_rmix;
            case SYMPHONY:
                return R.drawable.icon_light_symph;
            case ELECTRO:
                return R.drawable.icon_light_elect;
            case MIDTEMPO:
                return R.drawable.icon_light_mt;
            case MOOMBAHTON:
                return R.drawable.icon_light_mmbh;
            case JACKING:
                return R.drawable.icon_light_jackin;
            case PROGRESSIVE:
                return R.drawable.icon_light_progr;
            case VIP_HOUSE:
                return R.drawable.icon_light_vip;
            case SUPERDISCO_90:
                return R.drawable.icon_light_superdisco;
            case RECORD_BREAKS:
                return R.drawable.icon_light_breaks;
            case RECORD_BUDSTEP:
                return R.drawable.icon_light_dubstep;
            case RECORD_DANCECORE:
                return R.drawable.icon_light_dancecore;
            case RECORD_HARDSTYLE:
                return R.drawable.icon_light_hstyle;
            case RECORD_TRAP:
                return R.drawable.icon_light_trap;
            case PUMP:
                return R.drawable.icon_light_old_school;
            case RECORD_ROCK:
                return R.drawable.icon_light_rock;
            case SLOW_DANCE_FM:
                return R.drawable.icon_light_medlyak;
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
            case MAYATNIK_FUKO:
                return R.drawable.icon_light_mf;
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

    private int getDarkIconRes() {
        switch (baseStation) {
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
            case SYMPHONY:
                return R.drawable.icon_dark_symph;
            case ELECTRO:
                return R.drawable.icon_dark_elect;
            case MIDTEMPO:
                return R.drawable.icon_dark_mt;
            case MOOMBAHTON:
                return R.drawable.icon_dark_mmbh;
            case JACKING:
                return R.drawable.icon_dark_jackin;
            case PROGRESSIVE:
                return R.drawable.icon_dark_progr;
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
            case MAYATNIK_FUKO:
                return R.drawable.icon_dark_mf;
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

    private int getClassicIconRes() {
        switch (baseStation) {
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
            case SYMPHONY:
                return R.drawable.icon_symph;
            case ELECTRO:
                return R.drawable.icon_electro;
            case MIDTEMPO:
                return R.drawable.icon_mt;
            case MOOMBAHTON:
                return R.drawable.icon_mmbh;
            case JACKING:
                return R.drawable.icon_jackin;
            case PROGRESSIVE:
                return R.drawable.icon_progr;
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
            case TECHNO:
                return R.drawable.techno_ico;
            case IBIZA:
                return R.drawable.ibiza_ico;
            case MAYATNIK_FUKO:
                return R.drawable.icon_mf;
            case FUTURE_BASS:
                return R.drawable.fbass_ico;
            case REMIX:
                return R.drawable.remix_ico;
            case GASTARBAITER:
                return R.drawable.gastarbaiter_ico;
            case HARD_BASS:
                return R.drawable.hbass_ico;
            case ANSHLAG:
                return R.drawable.anshlag_ico;
            default:
                return R.drawable.rr_ico;
        }
    }

    @Override
    public String serialize() {
        return baseStation.name();
    }

    public static AbstractStation[] values() {
        AbstractStation[] result = new AbstractStation[BaseStation.values().length];
        for (int i = 0; i < BaseStation.values().length; i++) {
            result[i] = new PredefinedStation(BaseStation.values()[i]);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredefinedStation that = (PredefinedStation) o;
        return baseStation == that.baseStation;
    }

    @Override
    public int hashCode() {
        return baseStation.hashCode();
    }
}
