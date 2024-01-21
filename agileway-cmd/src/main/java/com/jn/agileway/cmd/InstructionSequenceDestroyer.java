package com.jn.agileway.cmd;

/**
 * Destroys all registered {@link java.lang.Process} after a certain event,
 * typically when the VM exits
 * @see com.jn.langx.commandline.ShutdownHookProcessDestroyer
 *
 */
public interface InstructionSequenceDestroyer {

    /**
     * Returns {@code true} if the specified
     * {@link java.lang.Process} was
     * successfully added to the list of processes to be destroy.
     *
     * @param process
     *      the process to add
     * @return {@code true} if the specified
     *      {@link java.lang.Process} was
     *      successfully added
     */
    boolean add(InstructionSequence process);

    /**
     * Returns {@code true} if the specified
     * {@link java.lang.Process} was
     * successfully removed from the list of processes to be destroy.
     *
     * @param process
     *            the process to remove
     * @return {@code true} if the specified
     *      {@link java.lang.Process} was
     *      successfully removed
     */
    boolean remove(InstructionSequence process);

    /**
     * Returns the number of registered processes.
     *
     * @return the number of register process
     */
    int size();
}
