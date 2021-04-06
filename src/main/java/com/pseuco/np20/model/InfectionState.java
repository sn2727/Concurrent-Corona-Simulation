package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the state of a person including how long it has been in that state.
 */
public class InfectionState {
    /**
     * Represents the state of a person.
     */
    public enum State {
        @JsonProperty("healthy")
        SUSCEPTIBLE,

        @JsonProperty("infected")
        INFECTED,

        @JsonProperty("infectious")
        INFECTIOUS,

        @JsonProperty("recovered")
        RECOVERED
    }

    @JsonProperty("type")
    private final State state;

    @JsonProperty("since")
    private final int inStateSince;

    /**
     * Constructs an object with the given information.
     *
     * @param state The state the person in in.
     * @param inStateSince How long the person has been in the state.
     */
    @JsonCreator
    public InfectionState(
        @JsonProperty( value = "type", required = true)
        final State state,
        @JsonProperty( value = "since", defaultValue = "0")
        final int inStateSince
    ) {
        this.state = state;
        this.inStateSince = inStateSince;
    }

    /**
     * Returns the state the person is in.
     *
     * @return The state the person is in.
     */
    public State getState() {
        return this.state;
    }

    /**
     * Returns how long the person has been in the state.
     *
     * @return How long the person has been in the state.
     */
    public int getInStateSince() {
        return this.inStateSince;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InfectionState)) {
            return false;
        }
        InfectionState infectionState = (InfectionState) other;
        return (
            this.state == infectionState.state
            && this.inStateSince == infectionState.inStateSince
        );
    }

    @Override
    public int hashCode() {
        return 31 * this.inStateSince + this.state.hashCode();
    }
}