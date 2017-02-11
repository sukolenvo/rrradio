package com.dakare.radiorecord.app;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ParsingTest {

    @Test
    public void testSectionsLoading() throws IOException {
        Assert.assertFalse(new TestSectionFragment("Record Megamix").load().isEmpty());
        Assert.assertFalse(new TestSectionFragment("Кремов и Хрусталев").load().isEmpty());
        Assert.assertFalse(new TestSectionFragment("Свежаки").load().isEmpty());
        Assert.assertFalse(new TestSectionFragment("Superchart").load().isEmpty());
    }

    @Test
    public void testCategoryTops() throws IOException {
        for (Station station : Station.values()) {
            Assert.assertFalse(new TestTopsMusicFragment(station).load().isEmpty());
        }
    }

    @Test
    public void testHistoryDates() throws IOException {
        for (Station station : Station.values()) {
            Assert.assertFalse(new TestHistoryDateFragment(station).load().isEmpty());
        }
    }

    @Test
    public void testHistoryMusic() throws IOException {
        for (Station station : Station.values()) {
            Assert.assertFalse(new TestHistoryMusicFragment(station).load().isEmpty());
        }
    }
}