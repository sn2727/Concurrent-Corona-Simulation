package com.pseuco.np20.tests;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import com.pseuco.np20.tests.common.TestCase;
import com.pseuco.np20.validator.Validator;

import org.junit.Test;

public class TestValidator {
    private static class TestValidatorValidator implements Validator {
        private final int numberOfPatches;

        private final Map<Integer, Integer> tickCounters = new HashMap<>();
        private final Map<Integer, Integer> lastPersons = new HashMap<>();

        private boolean protocolViolation = false;

        public TestValidatorValidator(int numberOfPatches) {
            this.numberOfPatches = numberOfPatches;
        }

        public boolean getProtocolViolation() {
            for (int patchId = 0; patchId < this.numberOfPatches; patchId++) {
                if (!this.tickCounters.containsKey(patchId)) {
                    return true;
                }
            }
            return this.protocolViolation;
        }

        @Override
        public synchronized void onPatchTick(int tick, int patchId) {
            final int expectedTick = this.tickCounters.getOrDefault(patchId, 0);
            if (tick != expectedTick) {
                this.protocolViolation = true;
            }
            this.tickCounters.put(patchId, expectedTick + 1);
            this.lastPersons.remove(patchId);
        }

        @Override
        public synchronized void onPersonTick(int tick, int patchId, int personId) {
            final int lastPersonId = this.lastPersons.getOrDefault(patchId, -1);
            if (personId <= lastPersonId) {
                this.protocolViolation = true;
            }
            this.lastPersons.put(patchId, personId);
        }
    }


    static public void runTest(String name, int padding) {
        final TestCase testCase = TestCase.getPublic(name);
        final int numberOfPatches = testCase.getScenario().getNumberOfPatches();
        final TestValidatorValidator validator = new TestValidatorValidator(numberOfPatches);
        testCase.launchRocket(validator, padding);
        assertFalse("validator protocol violated", validator.getProtocolViolation());
    }

    @Test
    public void testValidator10() {
        TestValidator.runTest("we_love_np", 10);
    }

    @Test
    public void testValidator15() {
        TestValidator.runTest("we_love_np", 15);
    }
}