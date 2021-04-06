package com.pseuco.np20.tests;

import com.pseuco.np20.tests.common.TestCase;

import org.junit.Test;


public class TestSlug {
    @Test
    public void testWeLoveNP() {
        TestCase.getPublic("we_love_np").runSlug();
    }
}