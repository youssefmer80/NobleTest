package com.thisisnoble.javatest.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thisisnoble.javatest.Event;
import com.thisisnoble.javatest.EventNotFoundException;
import com.thisisnoble.javatest.Orchestrator;
import com.thisisnoble.javatest.Processor;
import com.thisisnoble.javatest.ProcessorNotFoundException;
import com.thisisnoble.javatest.Publisher;

public class EventOrchestrator implements Orchestrator {

	private Map<String, Event> events;
	private Map<String, Event> eventsProcessed;
	private Set<Processor> processors;
	private Set<Processor> processorsTriggered;
	private Publisher publisher;

	public EventOrchestrator() {
		events = Collections.synchronizedMap(new HashMap<String, Event>());
		processors = Collections.synchronizedSet(new HashSet<Processor>());
		eventsProcessed = Collections.synchronizedMap(new HashMap<String, Event>());
		processorsTriggered = Collections.synchronizedSet(new HashSet<Processor>());

	}

	public Map<String, Event> getEvents() {
		return events;
	}

	public void setEvents(Map<String, Event> events) {
		this.events = events;
	}

	public Set<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(Set<Processor> processors) {
		this.processors = processors;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public void register(Processor processor) {
		// TODO Auto-generated method stub
		if(processor != null){
			processors.add(processor);
		}

	}

	@Override
	public void receive(Event event) {
		// TODO Auto-generated method stub
		if(event != null){
			events.put(event.getId(), event);
		}

	}

	@Override
	public void setup(Publisher publisher) {
		// TODO Auto-generated method stub
		if(publisher != null){
			this.publisher = publisher;
		}
	}

	/*
	 * this method how many events get processed
	 */
	public int getEventProcessed() {
		return eventsProcessed.size();
	}

	/*
	 * this method how many processors get triggered
	 */
	public int getProcessorsTriggered() {
		return processorsTriggered.size();
	}
	
	public void clearEventOrchestrator(){
		events.clear();
		eventsProcessed.clear();
		processors.clear();
		processorsTriggered.clear();
		publisher = null;
	}

	/*
	 * this method processes all the events received by the orchestrator and
	 * publish them by the publisher
	 */
	public synchronized void processAllEvents() {
		
		if(events == null || events.size() == 0){
			throw new EventNotFoundException("no events at all to be processed");
		}
		for (String key : events.keySet()) {
			Event event = events.get(key);
			if (!(event instanceof CompositeEvent)) {
				processAndPublish(event);
			} else {
				CompositeEvent comEvent = (CompositeEvent) event;
				Event eventParent = comEvent.getParent();
				processAndPublish(eventParent);
				Map<String, Event> children = comEvent.getChildern();
				for (String k : children.keySet()) {
					Event eve = children.get(k);
					processAndPublish(eve);
				}
			}
		}

	}

	/*
	 * this method process an event and publish it in case get processed
	 */
	private void processAndPublish(Event event) {
		Event processedEvent;
		processedEvent = processEvent(event);
		if (processedEvent != null) {
			eventsProcessed.put(processedEvent.getId(), processedEvent);
			publisher.publish(processedEvent);
		}
	}

	/*
	 * this method process an individual event
	 */
	private Event processEvent(Event event) {
	
		return isProcessorInterested(event);

	}

	/*
	 * this method return an event object after it gets processed by a processor
	 * who is interested in. in case there is no processor interested return the
	 * same event passed for processing
	 */
	private Event isProcessorInterested(Event event) {

		if (processors == null || processors.size() == 0) {
			throw new ProcessorNotFoundException(
					"no processors are found in the orchestrator");
		}
		for (Processor processor : processors) {

			if (processor.interestedIn(event)) {
				processorsTriggered.add(processor);
				processor.process(event);
				return event;
			}
		}
		return null;
	}

}
