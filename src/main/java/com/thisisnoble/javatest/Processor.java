package com.thisisnoble.javatest;

import com.thisisnoble.javatest.Event;

public interface Processor {

    boolean interestedIn(Event event);

	void process(Event event);
}
