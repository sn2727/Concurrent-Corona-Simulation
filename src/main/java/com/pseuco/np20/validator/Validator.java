package com.pseuco.np20.validator;


/**
 * In interface for automated testing.
 *
 * <p>
 * We use this interface to gain insights into your program and automatically
 * validate some requirements of the project. You must not wrap the validator
 * in a monitor or use some other kind of synchronization. We guarantee that
 * it is safe to concurrently call every method of classes implementing this
 * interface. However, you need to ensure that the thread calling a validator
 * is not interrupted while the validator runs.
 * </p>
 *
 * <p>
 * For the purpose of this interface, patches are enumerated left-to-right and
 * top-to-bottom, i.e., the top-left patch has the id <em>0</em>, it's right
 * neighbor has the id <em>1</em>, and so on.
 * </p>
 */
public interface Validator {
    /**
     * Call this method before processing a tick on a patch.
     *
     * @param tick The tick that is about to be processed on the given patch.
     * @param patchId The id of the patch the tick is processed on.
     */
    void onPatchTick(int tick, int patchId);

    /**
     * Call this method before calling <em>tick</em> on a <em>Person</em>.
     *
     * @param tick The tick that is about to be processed on the given person.
     * @param patchId The id of the patch the tick is processed on.
     * @param personId The id of the person the tick is processed on.
     */
    void onPersonTick(int tick, int patchId, int personId);
}
