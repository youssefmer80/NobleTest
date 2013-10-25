package com.thisnoble.javatest;

import com.thisnoble.javatest.Event;
import com.thisnoble.javatest.Processor;

public interface Publisher {

	void publish(Event event);
}
