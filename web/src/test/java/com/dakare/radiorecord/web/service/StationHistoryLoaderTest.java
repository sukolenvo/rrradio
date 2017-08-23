package com.dakare.radiorecord.web.service;

import com.dakare.radiorecord.web.service.history.StationHistoryLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class StationHistoryLoaderTest {

    @Autowired
    private StationHistoryLoader stationHistoryLoader;

    @Test
    public void load() throws Exception {
        Assert.assertTrue(stationHistoryLoader.load("rr").size() > 0);
    }

}