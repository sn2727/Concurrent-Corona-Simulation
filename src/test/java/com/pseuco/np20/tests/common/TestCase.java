package com.pseuco.np20.tests.common;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pseuco.np20.model.Output;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.simulation.rocket.Rocket;
import com.pseuco.np20.simulation.slug.Slug;
import com.pseuco.np20.validator.DummyValidator;
import com.pseuco.np20.validator.InsufficientPaddingException;
import com.pseuco.np20.validator.Validator;

public class TestCase {
    private static final ClassLoader loader = TestCase.class.getClassLoader();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Scenario scenario;
    private final Output output;

    private TestCase(final String name, final String category) {
        try {
            this.scenario = objectMapper.readValue(loader.getResourceAsStream("scenarios/" + category + "/" + name + ".json"), Scenario.class);
            this.output = objectMapper.readValue(loader.getResourceAsStream("scenarios/" + category + "/" + name + ".result.json"), Output.class);
        } catch (final IOException error) {
            throw new RuntimeException(error);
        }
    }

    static public TestCase getPublic(String name) {
        return new TestCase(name, "public");
    }

    static public TestCase getSecret(String name) {
        return new TestCase(name, "secret");
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    public Output getExpectedOutput() {
        return this.output;
    }

    public Checker runSlug() {
        final Slug slug = new Slug(this.scenario);
        slug.run();
        final Output output = slug.getOutput();
        final Checker checker = new Checker();
        checker.check(output, this.output);
        for (String problem : checker.getProblems()) {
            System.err.println(problem);
        }
        assertFalse("invalid output", checker.hasProblems());
        return checker;
    }

    public Checker launchRocket(Validator validator, int padding) {
        try {
            final Rocket rocket = new Rocket(this.scenario, padding, validator);
            rocket.run();
            final Output output = rocket.getOutput();
            final Checker checker = new Checker();
            checker.check(output, this.output);
            for (String problem : checker.getProblems()) {
                System.err.println(problem);
            }
            assertFalse("invalid output", checker.hasProblems());
            return checker;
        } catch (InsufficientPaddingException error) {
            throw new RuntimeException(error);
        }
    }

    public Checker launchRocket(int padding) {
        return this.launchRocket(new DummyValidator(), padding);
    }


}