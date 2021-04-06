package com.pseuco.np20.tests;

import com.pseuco.np20.tests.common.TestCase;

import org.junit.Test;


public class TestCorrectness {
    @Test
    public void testWeLoveNP10() {
        TestCase.getPublic("we_love_np").launchRocket(10);
    }

    @Test
    public void testWeLoveNP15() {
        TestCase.getPublic("we_love_np").launchRocket(15);
    }

    @Test
    public void testWeLoveNP7() {
        TestCase.getPublic("we_love_np").launchRocket(7);
    }

    @Test
    public void testMediumScenario(){
        TestCase.getPublic("medium").launchRocket(20);
    }

    @Test
    public void testSmallScenario(){
        TestCase.getPublic("small").launchRocket(10);
    }

    @Test
    public void testMiniScenario(){
        TestCase.getPublic("mini").launchRocket(5);
    }

    @Test
    public void testVeryLargeScenario(){
        TestCase.getPublic("VeryLarge").launchRocket(25);
    }


}