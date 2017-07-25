package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JsonHelperTest {


    @Test
    @Ignore
    /**
     * Test requires Android framework.
     */
    public void testReadWrite() throws JSONException {
        PlaylistItem item1 = new PlaylistItem();
        item1.setLive(true);
        item1.setUrl("http://example.com/music.mp3");
        item1.setTitle("some title1");
        item1.setSubtitle("some title2");
        item1.setStation(Station.RADIO_RECORD);
        PlaylistItem item2 = new PlaylistItem(item1);
        item2.setLive(false);
        item2.setStation(Station.PIRATE_STATION);
        item2.setTitle("title for second test item");
        List<PlaylistItem> in = Arrays.asList(item1, item2);
        List<PlaylistItem> result = JsonHelper.readPlaylist(JsonHelper.writePlaylist(in));
        Assert.assertEquals(in, result);
    }
}