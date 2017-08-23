package com.dakare.radiorecord.web;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void normalize() throws Exception {
        String line = null;
        Assert.assertEquals(null, Utils.normalize(line));
        line = "русский Автор";
        Assert.assertEquals(line, Utils.normalize(line));
        line = "asd asd123";
        Assert.assertEquals(line, Utils.normalize(line));
        line = "123// asd";
        Assert.assertEquals("123 asd", Utils.normalize(line));
        line = "123-/ asd";
        Assert.assertEquals("123 asd", Utils.normalize(line));
        line = "123     asd";
        Assert.assertEquals("123 asd", Utils.normalize(line));
    }

}