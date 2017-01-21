package com.dakare.radiorecord.app.player.sleep_mode;

import android.content.Context;
import com.dakare.radiorecord.app.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public enum SleepMode {
    OFF {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            return context.getString(R.string.sleep_mode_off);
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            return 0;
        }

        @Override
        public boolean isConfigurable() {
            return false;
        }
    },
    ELAPSED_S {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            if (sleepSettings.getHour() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_minutes, sleepSettings.getMinute());
            }
            if (sleepSettings.getMinute() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_hours, sleepSettings.getHour());
            }
            return context.getString(R.string.sleep_mode_elapsed, sleepSettings.getHour(), sleepSettings.getMinute());
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            long diff = initTs + sleepSettings.getInMs() - System.currentTimeMillis();
            if (diff < 0) {
                return 0;
            }
            return diff;
        }
    },
    ELAPSED_M {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            if (sleepSettings.getHour() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_minutes, sleepSettings.getMinute());
            }
            if (sleepSettings.getMinute() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_hours, sleepSettings.getHour());
            }
            return context.getString(R.string.sleep_mode_elapsed, sleepSettings.getHour(), sleepSettings.getMinute());
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            long diff = initTs + sleepSettings.getInMs() - System.currentTimeMillis();
            if (diff < 0) {
                return 0;
            }
            return diff;
        }
    },
    ELAPSED_L {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            if (sleepSettings.getHour() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_minutes, sleepSettings.getMinute());
            }
            if (sleepSettings.getMinute() == 0) {
                return context.getString(R.string.sleep_mode_elapsed_hours, sleepSettings.getHour());
            }
            return context.getString(R.string.sleep_mode_elapsed, sleepSettings.getHour(), sleepSettings.getMinute());
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            long diff = initTs + sleepSettings.getInMs() - System.currentTimeMillis();
            if (diff < 0) {
                return 0;
            }
            return diff;
        }
    },
    CHOSEN_1 {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            return context.getString(R.string.sleep_mode_chosen, sleepSettings.getHour(), sleepSettings.getMinute());
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            Calendar calendar = Calendar.getInstance();
            SleepSettings now = new SleepSettings(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            if (sleepSettings.getInMs() < now.getInMs()) {
                return TimeUnit.DAYS.toMillis(1) + sleepSettings.getInMs() - now.getInMs();
            }
            return sleepSettings.getInMs() - now.getInMs();
        }
    },
    CHOSEN_2 {
        @Override
        public String buildTitle(final Context context, final SleepSettings sleepSettings) {
            return context.getString(R.string.sleep_mode_chosen, sleepSettings.getHour(), sleepSettings.getMinute());
        }

        @Override
        public long nextSleepIn(final long initTs, final SleepSettings sleepSettings) {
            Calendar calendar = Calendar.getInstance();
            SleepSettings now = new SleepSettings(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            if (sleepSettings.getInMs() < now.getInMs()) {
                return TimeUnit.DAYS.toMillis(1) + sleepSettings.getInMs() - now.getInMs();
            }
            return sleepSettings.getInMs() - now.getInMs();
        }
    };

    public abstract String buildTitle(Context context, SleepSettings sleepSettings);

    public abstract long nextSleepIn(final long initTs, final SleepSettings sleepSettings);

    public boolean isConfigurable() {
        return true;
    }

}
