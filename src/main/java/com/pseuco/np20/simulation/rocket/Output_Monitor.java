package com.pseuco.np20.simulation.rocket;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pseuco.np20.model.Query;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.Statistics;
import com.pseuco.np20.simulation.common.Person;

public class Output_Monitor {

    private final Map<Integer, List<Person>> trace ;    // Stats wie slug
    private final Map<String, List<Statistics>> statistics ; // Stats wie slug
    private Scenario scenario;
    //private int ID ;


    public Output_Monitor(/*int ID,*/ Scenario scenario) {
        //this.ID = ID;
        // Stat save
        this.trace = new HashMap<>();
        this.statistics = new HashMap<>();
        this.scenario = scenario;
        //Stats Init
        initializeStatistics();
    }

    private synchronized void initializeStatistics() {
        // we initialize the map we use to collect the necessary statistics
        for (Entry<String, Query> queryKey : this.scenario.getQueries().entrySet()) {
            this.statistics.put(queryKey.getKey(), new LinkedList<>());
        }
    }

    // This save the population so every Person on the patch
    public synchronized void set_trace(List<Person> population, int tick, patch patch) {
        // Check if smth is wrong
        if (this.trace.get(tick) != null || population == null) {
            System.out.println("Set_trace stats override");
        }

        LinkedList<Person> l = new LinkedList<>();
        for(Person p : population){ // -> Personen clonen
            l.add(p.clone(patch));
        }
        this.trace.put(tick, l);    // -> abspeichern des aktuellen ticks in einer Hashmap
    }

    // As in slugg this save the Statistics from the Persons, but do a little more at the beginning
    public synchronized void set_statistics(List<Person> population) {
        //wie slugg stats abspeichern
        for (Map.Entry<String, Query> entry : scenario.getQueries().entrySet()) {
            final Query query = entry.getValue();
            this.statistics.get(entry.getKey())
                    .add(new Statistics(
                            population.stream()
                                    .filter((Person person) -> person.isSusceptible()
                                            && query.getArea().contains(person.getPosition()))
                                    .count(),
                            population.stream()
                                    .filter((Person person) -> person.isInfected()
                                            && query.getArea().contains(person.getPosition()))
                                    .count(),
                            population.stream()
                                    .filter((Person person) -> person.isInfectious()
                                            && query.getArea().contains(person.getPosition()))
                                    .count(),
                            population.stream().filter((Person person) -> person.isRecovered()
                                    && query.getArea().contains(person.getPosition())).count()));
        }
    }

    public synchronized Map<Integer, List<Person>> get_stat_trace() {
        // Check if smth in save mechanics are broken
        for (Entry<Integer, List<Person>> entry : trace.entrySet()) {
            if (entry.getValue() == null){
                System.out.println("get_trace is null at: "+entry.getKey());
            }
        }
        return this.trace;
    }

    public synchronized Map<String, List<Statistics>> get_stat_statistics() {
        // just give the statistics back
        return this.statistics;
	}

}