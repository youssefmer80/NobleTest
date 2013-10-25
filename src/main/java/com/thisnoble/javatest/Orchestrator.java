package com.thisnoble.javatest;

public interface Orchestrator {

    void register(Processor processor);

    void receive(Event event);
}
