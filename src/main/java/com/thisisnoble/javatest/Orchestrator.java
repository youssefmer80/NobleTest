package com.thisisnoble.javatest;

import com.thisisnoble.javatest.Event;
import com.thisisnoble.javatest.Processor;
import com.thisisnoble.javatest.Publisher;

public interface Orchestrator {

    void register(Processor processor);

    void receive(Event event);

    void setup(Publisher publisher);
}
