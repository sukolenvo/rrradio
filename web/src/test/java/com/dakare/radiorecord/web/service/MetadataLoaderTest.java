package com.dakare.radiorecord.web.service;

import com.dakare.radiorecord.web.service.metadata.MetadataLoader;
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
public class MetadataLoaderTest {

    @Autowired
    private MetadataLoader metadataLoader;

    @Test
    public void load() throws Exception {
        Assert.assertFalse(metadataLoader.load().isEmpty());
    }

}