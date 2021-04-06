package com.pseuco.np20.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a person on the grid.
 *
 * Note that this is a mere data container holding information about a person but
 * does not contain any simulation functionality.
 */
public class PersonInfo {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("pos")
    final XY position;

    @JsonProperty("rngState")
    private final byte[] seed;

    @JsonProperty("infectionState")
    private final InfectionState infectionState;

    @JsonProperty("direction")
    private final Direction direction;

    /**
     * Constructs a person with the provided information.
     *
     * @param name The name of the person.
     * @param position The position of the person.
     * @param seed The seed (used for the RNG) of the person.
     * @param infectionState The infection state of the person.
     * @param direction The direction the person is moving in.
     */
    @JsonCreator
    public PersonInfo(
        @JsonProperty( value = "name", required = true)
        final String name,
        @JsonProperty( value = "pos", required = true)
        final XY position,
        @JsonProperty( value = "rngState", required = true)
        final byte[] seed,
        @JsonProperty( value = "infectionState", required = true)
        final InfectionState infectionState,
        @JsonProperty( value = "direction", required = true)
        final Direction direction
    ) {
        this.name = name;
        this.position = position;
        this.seed = seed;
        this.infectionState = infectionState;
        this.direction = direction;
    }

    /**
     * Returns the name of the person.
     *
     * @return The name of the person.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the position of the person.
     *
     * @return The position of the person.
     */
    public XY getPosition() {
        return this.position;
    }

    /**
     * Returns the seed (used for the RNG) of the person.
     *
     * @return The seed (used for the RNG) of the person.
     */
    public byte[] getSeed() {
        return this.seed;
    }

    /**
     * Returns the infection state of the person.
     *
     * @return The infection state of the person.
     */
    public InfectionState getInfectionState() {
        return this.infectionState;
    }

    /**
     * Returns the direction the person is moving in.
     *
     * @return The direction the person is moving in.
     */
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PersonInfo)) {
            return false;
        }
        PersonInfo personInfo = (PersonInfo) other;
        return (
            this.name.equals(personInfo.name)
            && this.position.equals(personInfo.position)
            && Arrays.equals(this.seed, personInfo.seed)
            && this.infectionState.equals(personInfo.infectionState)
            && this.direction.equals(personInfo.direction)
        );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + this.name.hashCode();
        hash = 31 * hash + this.position.hashCode();
        hash = 31 * hash + this.seed.hashCode();
        hash = 31 * hash + this.infectionState.hashCode();
        hash = 31 * hash + this.direction.hashCode();
        return hash;
    }
}