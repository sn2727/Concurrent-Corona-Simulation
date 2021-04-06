package com.pseuco.np20.validator;


/**
 * A dummy validator that does nothing.
 */
public class DummyValidator implements Validator {
    @Override
    public void onPatchTick(int tick, int patchId) {
        // do nothing
    }

    @Override
    public void onPersonTick(int tick, int patchId, int personId) {
        // do nothing
    }
}