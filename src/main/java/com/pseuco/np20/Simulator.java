package com.pseuco.np20;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pseuco.np20.simulation.rocket.Rocket;
import com.pseuco.np20.simulation.rocket.Starship;
import com.pseuco.np20.simulation.common.Simulation;
import com.pseuco.np20.simulation.slug.Slug;
import com.pseuco.np20.validator.DummyValidator;
import com.pseuco.np20.validator.InsufficientPaddingException;
import com.pseuco.np20.model.Scenario;


/**
 * Implements the <em>main</em> method and the command line interface.
 */
public class Simulator {
    public class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }

    @Parameter(names = "-scenario", required = true, converter = FileConverter.class)
    private File scenarioFile;

    @Parameter(names = "-out", required = true, converter = FileConverter.class)
    private File outputFile;

    @Parameter(names = "-padding")
    private int padding = 10;

    @Parameter(names = "-slug")
    private boolean slug = false;

    @Parameter(names = "-rocket")
    private boolean rocket = false;

    @Parameter(names = "-starship")
    private boolean starship = false;

    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
        final Simulator app = new Simulator();
        JCommander.newBuilder().addObject(app).args(args).build();
        app.run();
    }

    public void run() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Scenario scenario = objectMapper.readValue(this.scenarioFile, Scenario.class);

        System.out.println("Scenario: " + scenario.getName());
        System.out.println("Ticks: " + scenario.getTicks());

        Simulation simulation;
        if (this.starship) {
            simulation = new Starship(scenario);
        } else if (this.rocket) {
            try {
                simulation = new Rocket(scenario, this.padding, new DummyValidator());
            } catch (InsufficientPaddingException error) {
                throw new RuntimeException(error);
            }
        } else {
            simulation = new Slug(scenario);
        }

        final long startTime = System.nanoTime();
        simulation.run();
        final long endTime = System.nanoTime();

        System.out.println("Time: " + (endTime - startTime) / 1000000 + "ms");

        objectMapper.writeValue(this.outputFile, simulation.getOutput());
    }
}
