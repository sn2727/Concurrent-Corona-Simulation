package com.pseuco.np20.simulation.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;

import com.pseuco.np20.model.InfectionState;
import com.pseuco.np20.model.Parameters;
import com.pseuco.np20.model.PersonInfo;
import com.pseuco.np20.model.Direction;
import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.XY;


/**
 * Represents a person and contains the necessary simulation functionality.
 */
public class Person {
    /**
     * A comparator comparing persons by their ids.
     */
    public static class PersonIDComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    }

    private static class RNG {
        private final MessageDigest messageDigest;

        private final Parameters parameters;

        private byte[] digest;

        public RNG(byte[] seed, Parameters parameters) {
            try {
                this.messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException error) {
                throw new RuntimeException(error);
            }
            this.parameters = parameters;
            this.digest = seed;
        }

        public void tick() {
            this.digest = messageDigest.digest(this.digest);
        }

        public byte[] getDigest() {
            return this.digest;
        }

        public int getUnsignedByte(int position) {
            return this.digest[position] & 0xFF;
        }

        public boolean isCoughing() {
            return this.getUnsignedByte(0) < this.parameters.getCoughThreshold();
        }

        public boolean isBreathing() {
            return this.getUnsignedByte(1) < this.parameters.getBreathThreshold();
        }

        public Direction getAcceleration() {
            final int index = this.getUnsignedByte(2) / this.parameters.getAccelerationDivisor();
            if (index >= Direction.values().length) {
                return Direction.NONE;
            }
            return Direction.values()[index];
        }
    }

    private final int id;

    private final Context context;
    private final Parameters parameters;

    private final RNG rng;

    private String name = "";

    private XY position;
    private XY ghostPosition = null;

    private Direction direction = Direction.NONE;

    private InfectionState.State state = InfectionState.State.SUSCEPTIBLE;
    private int inStateSince = 0;

    /**
     * Constructs a person with the given information.
     *
     * @param id The id of the person.
     * @param context The context the person is simulated in.
     * @param parameters The parameters of the simulation.
     * @param info The remaining information about the person.
     */
    public Person(int id, Context context, Parameters parameters, PersonInfo info) {
        this.id = id;
        this.context = context;
        this.parameters = parameters;
        this.rng = new RNG(info.getSeed(), this.parameters);
        this.position = info.getPosition();
        this.direction = info.getDirection();
        this.state = info.getInfectionState().getState();
        this.inStateSince = info.getInfectionState().getInStateSince();
        this.name = info.getName();
    }

    /**
     * Returns the id of the person.
     *
     * @return The id of the person.
     */
    public int getId() {
        return this.id;
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
     * Returns the current position of the person.
     *
     * @return The current position of the person.
     */
    public XY getPosition() {
        return this.position;
    }

    /**
     * Returns the position of the person's ghost if it has one.
     *
     * @return The position of the person's ghost if it has one.
     */
    public XY getGhostPosition() {
        return this.ghostPosition;
    }

    /**
     * Returns whether the person has a ghost.
     *
     * @return Whether the person has a ghost.
     */
    public boolean hasGhost() {
        return this.ghostPosition != null;
    }

    /**
     * Returns the current infection state of the person.
     *
     * @return The current infection state of the person.
     */
    public InfectionState.State getState() {
        return this.state;
    }

    /**
     * Sets the infection state of the person and resets the <em>inStateSince</em>-counter.
     *
     * @param state The new state the person is in.
     */
    private void setState(InfectionState.State state) {
        this.state = state;
        this.inStateSince = 0;
    }

    /**
     * Returns whether the person is susceptible.
     *
     * @return Whether the person is susceptible.
     */
    public boolean isSusceptible() {
        return this.state == InfectionState.State.SUSCEPTIBLE;
    }

    /**
     * Returns whether the person is infected.
     *
     * @return Whether the person is infected.
     */
    public boolean isInfected() {
        return this.state == InfectionState.State.INFECTED;
    }

    /**
     * Returns whether the person is infectious.
     *
     * @return Whether the person is infectious.
     */
    public boolean isInfectious() {
        return this.state == InfectionState.State.INFECTIOUS;
    }

    /**
     * Returns whether the person has recovered.
     *
     * @return Whether the person has recovered.
     */
    public boolean isRecovered() {
        return this.state == InfectionState.State.RECOVERED;
    }

    /**
     * Returns whether the person is breathing.
     *
     * @return Whether the person is breathing.
     */
    public boolean isBreathing() {
        return this.rng.isBreathing();
    }

    /**
     * Returns whether the person is coughing.
     *
     * @return Whether the person is coughing.
     */
    public boolean isCoughing() {
        return this.rng.isCoughing();
    }

    /**
     * Infects the person with the virus.
     */
    public void infect() {
        if (this.isSusceptible()) {
            this.setState(InfectionState.State.INFECTED);
        }
    }

    /**
     * Returns an info object based on the persons current state.
     *
     * @return An info object based on the persons current state.
     */
    public PersonInfo getInfo() {
        return new PersonInfo(
            this.name,
            this.position,
            this.rng.getDigest(),
            new InfectionState(this.state, this.inStateSince),
            this.direction
        );
    }

    /**
     * Clones the person and changes its context to the given context.
     *
     * @param context The context of the clone.
     * @return The cloned person.
     */
    public Person clone(Context context) {
        return new Person(this.id, context, this.parameters, this.getInfo());
    }

    /**
     * Removes the ghost of the person.
     */
    public void bustGhost() {
        this.ghostPosition = null;
    }

    /**
     * Simulates a tick on the person.
     */
    public void tick() {
        this.rng.tick();

        this.inStateSince++;

        this.ghostPosition = this.position;

        if (this.isInfected() && this.inStateSince >= this.parameters.getIncubationTime()) {
            this.setState(InfectionState.State.INFECTIOUS);
        } else if (this.isInfectious() && this.inStateSince >= this.parameters.getRecoveryTime()) {
            this.setState(InfectionState.State.RECOVERED);
        }

        final Direction acceleration = this.rng.getAcceleration();
        final XY velocity = this.direction.getVector().add(acceleration.getVector()).limit(-1, 1);
        final XY position = this.position.add(velocity);

        // check whether we would would bump into a wall
        if (!this.context.getGrid().contains(position)) {
            this.direction = Direction.NONE;
            return;
        }

        // check whether we would bump into an obstacle
        if (
            this.context.getObstacles().stream().anyMatch(
                (Rectangle rectangle) -> rectangle.contains(position)
            )
        ) {
            this.direction = Direction.NONE;
            return;
        }

        // check whether we would bump into another person
        if (
            this.context.getPopulation().stream().anyMatch(
                (Person person) ->
                    person.getPosition().equals(position)
                    || (
                        person.hasGhost()
                        && person.getGhostPosition().equals(position)
                    )
            )
        ) {
            this.direction = Direction.NONE;
            return;
        }

        this.direction = Direction.fromVector(velocity);
        this.position = position;
    }
}