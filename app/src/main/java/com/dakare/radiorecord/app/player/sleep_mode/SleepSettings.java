package com.dakare.radiorecord.app.player.sleep_mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class SleepSettings {

    private int hour;
    private int minute;

    public String marchal() {
        return hour + ":" + minute;
    }

    public static SleepSettings unmarshal(String value) {
        SleepSettings result = new SleepSettings();
        String[] fields = value.split(":", 2);
        result.hour = Integer.parseInt(fields[0]);
        result.minute = Integer.parseInt(fields[1]);
        return result;
    }

    public long getInMs() {
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
    }
}
