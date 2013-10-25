package com.thisnoble.javatest;

import com.thisnoble.javatest.Event;

public interface Processor {

    boolean interestedIn(Event event);

	void process(Event event);
}
