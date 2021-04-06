package com.pseuco.np20.simulation.slug;

import com.pseuco.np20.simulation.common.Context;
import com.pseuco.np20.simulation.common.Person;
import com.pseuco.np20.simulation.common.Simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pseuco.np20.model.Output;
import com.pseuco.np20.model.PersonInfo;
import com.pseuco.np20.model.Query;
import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.Statistics;
import com.pseuco.np20.model.TraceEntry;
import com.pseuco.np20.model.XY;


/**
 * The sequential reference implementation.
 */
public class Slug implements Simulation, Context {
    private final Scenario scenario;

    private final List<Person> population = new LinkedList<>();

    private final List<TraceEntry> trace = new LinkedList<>();
    private final Map<String, List<Statistics>> statistics = new HashMap<>();

    public Slug(Scenario scenario) {
        this.scenario = scenario;
        this.populate();
        this.initializeStatistics();
        this.extendOutput();
    }

    private void populate() {
        // we populate the context with persons based on the respective info objects
        int id = 0;
        for (PersonInfo personInfo : this.scenario.getPopulation()) {
            this.population.add(
                new Person(id, this, this.scenario.getParameters(), personInfo)
            );
            id++;
        }
    }

    private void initializeStatistics() {
        // we initialize the map we use to collect the necessary statistics
        for (String queryKey : this.scenario.getQueries().keySet()) {
            this.statistics.put(queryKey, new LinkedList<>());
        }
    }

    @Override
    public Rectangle getGrid() {
        return this.scenario.getGrid();
    }

    @Override
    public List<Rectangle> getObstacles() {
        return this.scenario.getObstacles();
    }

    @Override
    public List<Person> getPopulation() {
        return this.population;
    }

    @Override
    public Output getOutput() {
        return new Output(this.scenario, this.trace, this.statistics);
    }

    private void extendStatistics() {
        // we collect statistics based on the current SI²R values
        for (Map.Entry<String, Query> entry : this.scenario.getQueries().entrySet()) {
            final Query query = entry.getValue();
            this.statistics.get(entry.getKey()).add(new Statistics(
                this.population.stream().filter(
                    (Person person) ->
                        person.isSusceptible()
                        && query.getArea().contains(person.getPosition())
                ).count(),
                this.population.stream().filter(
                    (Person person) ->
                        person.isInfected()
                        && query.getArea().contains(person.getPosition())
                ).count(),
                this.population.stream().filter(
                    (Person person) ->
                        person.isInfectious()
                        && query.getArea().contains(person.getPosition())
                ).count(),
                this.population.stream().filter(
                    (Person person) ->
                        person.isRecovered()
                        && query.getArea().contains(person.getPosition())
                ).count()
            ));
        }
    }

    private void extendOutput() {
        // we extend the statists and the trace for the current tick
        if (this.scenario.getTrace()) {
            this.trace.add(
                new TraceEntry(
                    this.population.stream()
                    .map(Person::getInfo)
                    .collect(Collectors.toList())
                )
            );
        }

        this.extendStatistics();
    }

    private void tick() {
        for (Person person : this.population) {
            // if this were a patch, the `onPersonTick` method should be called here
            person.tick();
        }

        // bust the ghosts of all persons
        this.population.stream().forEach(Person::bustGhost);

        // now compute how the infection spreads between the population
        for (int i = 0; i < this.population.size(); i++) {
            for (int j = i + 1; j < this.population.size(); j++) {
                final Person iPerson = this.population.get(i);
                final Person jPerson = this.population.get(j);
                final XY iPosition = iPerson.getPosition();
                final XY jPosition = jPerson.getPosition();
                final int deltaX = Math.abs(iPosition.getX() - jPosition.getX());
                final int deltaY = Math.abs(iPosition.getY() - jPosition.getY());
                final int distance = deltaX + deltaY;
                if (distance <= this.scenario.getParameters().getInfectionRadius()) {
                    if (iPerson.isInfectious() && iPerson.isCoughing() && jPerson.isBreathing()) {
                        jPerson.infect();
                    }
                    if (jPerson.isInfectious()&& jPerson.isCoughing() && iPerson.isBreathing()) {
                        iPerson.infect();
                    }
                }
            }
        }

        // we need to collect statistics and extend the recorded trace
        this.extendOutput();
    }

    @Override
    public void run() {
        for (int tick = 0; tick < this.scenario.getTicks(); tick++) {
            // if this were a patch, the `onPatchTick` method should be called here
            this.tick();
        }
    }
}