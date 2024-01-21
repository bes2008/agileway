package com.jn.agileway.cmd;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Destroys all registered {@code Process}es when the VM exits.
 */
public class ShutdownHookProcessDestroyer implements InstructionSequenceDestroyer, Runnable {

    /**
     * the list of currently running processes
     */
    private final Vector<ProcessAdapter> processes = new Vector<ProcessAdapter>();

    /**
     * The thread registered at the JVM to execute the shutdown handler
     */
    private ProcessDestroyerImpl destroyProcessThread = null;

    /**
     * Whether or not this ProcessDestroyer has been registered as a shutdown hook
     */
    private boolean added = false;

    /**
     * Whether or not this ProcessDestroyer is currently running as shutdown hook
     */
    private volatile boolean running = false;

    private class ProcessDestroyerImpl extends Thread {

        private boolean shouldDestroy = true;

        public ProcessDestroyerImpl() {
            super("ProcessDestroyer Shutdown Hook");
        }

        @Override
        public void run() {
            if (shouldDestroy) {
                ShutdownHookProcessDestroyer.this.run();
            }
        }

        public void setShouldDestroy(final boolean shouldDestroy) {
            this.shouldDestroy = shouldDestroy;
        }
    }

    /**
     * Constructs a {@code ProcessDestroyer} and obtains
     * {@code Runtime.addShutdownHook()} and
     * {@code Runtime.removeShutdownHook()} through reflection. The
     * ProcessDestroyer manages a list of processes to be destroyed when the VM
     * exits. If a process is added when the list is empty, this
     * {@code ProcessDestroyer} is registered as a shutdown hook. If
     * removing a process results in an empty list, the
     * {@code ProcessDestroyer} is removed as a shutdown hook.
     */
    public ShutdownHookProcessDestroyer() {
    }

    /**
     * Registers this {@code ProcessDestroyer} as a shutdown hook, uses
     * reflection to ensure pre-JDK 1.3 compatibility.
     */
    private void addShutdownHook() {
        if (!running) {
            destroyProcessThread = new ProcessDestroyerImpl();
            Runtime.getRuntime().addShutdownHook(destroyProcessThread);
            added = true;
        }
    }

    /**
     * Removes this {@code ProcessDestroyer} as a shutdown hook, uses
     * reflection to ensure pre-JDK 1.3 compatibility
     */
    private void removeShutdownHook() {
        if (added && !running) {
            final boolean removed = Runtime.getRuntime().removeShutdownHook(
                    destroyProcessThread);
            if (!removed) {
                System.err.println("Could not remove shutdown hook");
            }
            /*
             * start the hook thread, a unstarted thread may not be eligible for
             * garbage collection Cf.: http://developer.java.sun.com/developer/
             * bugParade/bugs/4533087.html
             */

            destroyProcessThread.setShouldDestroy(false);
            destroyProcessThread.start();
            // this should return quickly, since it basically is a NO-OP.
            try {
                destroyProcessThread.join(20000);
            } catch (final InterruptedException ie) {
                // the thread didn't die in time
                // it should not kill any processes unexpectedly
            }
            destroyProcessThread = null;
            added = false;
        }
    }

    /**
     * Returns whether or not the ProcessDestroyer is registered as as shutdown
     * hook
     *
     * @return true if this is currently added as shutdown hook
     */
    public boolean isAddedAsShutdownHook() {
        return added;
    }

    /**
     * Returns {@code true} if the specified {@code Process} was
     * successfully added to the list of processes to destroy upon VM exit.
     *
     * @param process the process to add
     * @return {@code true} if the specified {@code Process} was
     * successfully added
     */
    public boolean add(final InstructionSequence process) {
        synchronized (processes) {
            // if this list is empty, register the shutdown hook
            if (processes.isEmpty()) {
                addShutdownHook();
            }
            processes.addElement((ProcessAdapter) process);
            return processes.contains(process);
        }
    }

    /**
     * Returns {@code true} if the specified {@code Process} was
     * successfully removed from the list of processes to destroy upon VM exit.
     *
     * @param process the process to remove
     * @return {@code true} if the specified {@code Process} was
     * successfully removed
     */
    public boolean remove(final InstructionSequence process) {
        synchronized (processes) {
            final boolean processRemoved = processes.removeElement(process);
            if (processRemoved && processes.isEmpty()) {
                removeShutdownHook();
            }
            return processRemoved;
        }
    }

    /**
     * Returns the number of registered processes.
     *
     * @return the number of register process
     */
    public int size() {
        return processes.size();
    }

    /**
     * Invoked by the VM when it is exiting.
     */
    public void run() {
        synchronized (processes) {
            running = true;
            final Enumeration<ProcessAdapter> e = processes.elements();
            while (e.hasMoreElements()) {
                final ProcessAdapter process = e.nextElement();
                try {
                    process.destroy();
                } catch (final Throwable t) {
                    System.err.println("Unable to terminate process during process shutdown");
                }
            }
        }
    }
}
