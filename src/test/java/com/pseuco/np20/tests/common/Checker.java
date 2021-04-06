package com.pseuco.np20.tests.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pseuco.np20.model.Output;
import com.pseuco.np20.model.PersonInfo;
import com.pseuco.np20.model.Statistics;
import com.pseuco.np20.model.TraceEntry;


/**
 * Compares an output to a given reference.
 */
public class Checker {
    private final List<String> problems = new LinkedList<>();

    public Checker() {

    }

    public List<String> getProblems() {
        return this.problems;
    }

    public boolean hasProblems() {
        return this.problems.size() != 0;
    }

    private void addProblem(final String message, final Object... values) {
        this.problems.add(String.format(message, values));
    }

    public void check(final Output output, final Output expected) {
        this.compareTrace(output.getTrace(), expected.getTrace());
        this.compareStatistics(output.getStatistics(), expected.getStatistics());
    }

    private void compareStatistics(
        final Map<String, List<Statistics>> statistics,
        final Map<String, List<Statistics>> expected
    ) {
        final Set<String> queryKeys = new HashSet<>();
        queryKeys.addAll(statistics.keySet());
        queryKeys.addAll(expected.keySet());
        for (String queryKey : queryKeys) {
            if (!expected.containsKey(queryKey)) {
                this.addProblem("non-existent query `%s`", queryKey);
                continue;
            }
            if (!statistics.containsKey(queryKey)) {
                this.addProblem("no statistics for query `%s`", queryKey);
                continue;
            }
            final List<Statistics> entries = statistics.get(queryKey);
            final List<Statistics> expectedEntries = expected.get(queryKey);
            if (entries.size() != expectedEntries.size()) {
                this.addProblem(
                    "expected statistics trace of length %d but got %d",
                    expectedEntries.size(), entries.size()
                );
            }
            final Iterator<Statistics> entriesIterator = entries.iterator();
            final Iterator<Statistics> expectedIterator = expectedEntries.iterator();
            int tick = 0;
            while (entriesIterator.hasNext() && expectedIterator.hasNext()) {
                final Statistics gotStatistics = entriesIterator.next();
                final Statistics expectedStatistics = expectedIterator.next();
                if (!gotStatistics.equals(expectedStatistics)) {
                    this.addProblem(
                        "statistics for query `%s` incorrect in tick %d (expected: %s, got: %s)",
                        queryKey, tick, expectedStatistics, gotStatistics
                    );
                }
                tick += 1;
            }
        }
    }

    private void compareTrace(final List<TraceEntry> trace, final List<TraceEntry> expected) {
        if (trace.size() != expected.size()) {
            this.addProblem(
                "expected trace of length %d but got trace of length %d",
                expected.size(), trace.size()
            );
        }
        final Iterator<TraceEntry> traceIterator = trace.iterator();
        final Iterator<TraceEntry> expectedIterator = expected.iterator();
        int tick = 0;
        while (traceIterator.hasNext() && expectedIterator.hasNext()) {
            this.comparePopulation(
                traceIterator.next().getPopulation(),
                expectedIterator.next().getPopulation(),
                tick
            );
            tick += 1;
        }
    }

    private void comparePopulation(final List<PersonInfo> population, final List<PersonInfo> expected, final int tick) {
        if (population.size() != expected.size()) {
            this.addProblem(
                "expected population of size %d but got population of size %d in tick %d",
                expected.size(), population.size(), tick
            );
        }
        final Iterator<PersonInfo> populationIterator = population.iterator();
        final Iterator<PersonInfo> expectedIterator = expected.iterator();
        int personId = 0;
        while (populationIterator.hasNext() && expectedIterator.hasNext()) {
            this.comparePersonInfo(
                populationIterator.next(),
                expectedIterator.next(),
                tick,
                personId
            );
            personId += 1;
        }
    }

    private void comparePersonInfo(final PersonInfo personInfo, final PersonInfo expected, final int tick, final int personId) {
        if (!personInfo.equals(expected)) {
            this.addProblem(
                "person information mismatch in tick %d for person with id %d",
                tick, personId
            );
        }
    }
}