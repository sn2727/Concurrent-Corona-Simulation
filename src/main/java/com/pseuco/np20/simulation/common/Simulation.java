package com.pseuco.np20.simulation.common;

import com.pseuco.np20.model.Output;


/**
 * A common interface to be implemented by simulation engines.
 */
public interface Simulation extends Runnable {
    public Output getOutput();
}