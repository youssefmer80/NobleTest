package com.thisisnoble.javatest;

import static com.thisisnoble.javatest.util.TestIdGenerator.marginEventId;
import static com.thisisnoble.javatest.util.TestIdGenerator.riskEventId;
import static com.thisisnoble.javatest.util.TestIdGenerator.shipEventId;
import static com.thisisnoble.javatest.util.TestIdGenerator.tradeEventId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;

import com.thisisnoble.javatest.events.MarginEvent;
import com.thisisnoble.javatest.events.RiskEvent;
import com.thisisnoble.javatest.events.ShippingEvent;
import com.thisisnoble.javatest.events.TradeEvent;
import com.thisisnoble.javatest.impl.CompositeEvent;
import com.thisisnoble.javatest.impl.EventOrchestrator;
import com.thisisnoble.javatest.processors.MarginProcessor;
import com.thisisnoble.javatest.processors.RiskProcessor;
import com.thisisnoble.javatest.processors.ShippingProcessor;
import com.thisisnoble.javatest.processors.TradeProcessor;

public class SimpleOrchestratorTest  {
	
	private TestPublisher testPublisher;
	private EventOrchestrator orchestrator;
	
    @Test
    public void tradeEventShouldTriggerAllProcessors() {
        
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator)setupOrchestrator();
        orchestrator.setup(testPublisher);
        
        assertEquals(4, orchestrator.getProcessors().size());
            
        Event te1 = new TradeEvent(tradeEventId(), 1000.0);
        Event event1 =  prepareEventOneTriggerAllProcessors(te1);
        
        orchestrator.receive(event1);
        safeSleep(100);
        testPublisher.publish(event1);
        
        assertEquals(1, orchestrator.getEvents().size());
        
        CompositeEvent comevent1 =  (CompositeEvent) testPublisher.getLastEvent();

        assertEquals("tradeEvt", ((TradeEvent) comevent1.getParent()).getId());
        assertEquals(1000.0, ((TradeEvent) comevent1.getParent()).getNotional(),0.01);
        assertEquals(3, comevent1.size());
        
        RiskEvent re1 = comevent1.getChildById("tradeEvt-riskEvt");
        assertNotNull(re1);
        assertEquals(50.0, re1.getRiskValue(), 0.01);
        
        MarginEvent me1 = comevent1.getChildById("tradeEvt-marginEvt");
        assertNotNull(me1);
        assertEquals(10.0, me1.getMargin(), 0.01);
        
        ShippingEvent se1 = comevent1.getChildById("tradeEvt-shipEvt");
        assertNotNull(se1);
        assertEquals(200.0, se1.getShippingCost(), 0.01);
        
        
        Event te2 = new TradeEvent("te2", 800.0);
        Event event2 =  prepareEventTwoTriggerAllProcessors(te2);
        
        orchestrator.receive(event2);
        safeSleep(100);
        testPublisher.publish(event2);
        
        assertEquals(2, orchestrator.getEvents().size());
        
        CompositeEvent comevent2 =  (CompositeEvent) testPublisher.getLastEvent();

        assertEquals("te2", ((TradeEvent) comevent2.getParent()).getId());
        assertEquals(800.0, ((TradeEvent) comevent2.getParent()).getNotional(),0.01);
        assertEquals(3, comevent2.size());
        
        RiskEvent re2 = comevent2.getChildById("te2-riskEvt");
        assertNotNull(re2);
        assertEquals(70.0, re2.getRiskValue(), 0.01);
        
        MarginEvent me2 = comevent2.getChildById("te2-marginEvt");
        assertNotNull(me2);
        assertEquals(15.0, me2.getMargin(), 0.01);
        
        ShippingEvent se2 = comevent2.getChildById("te2-shipEvt");
        assertNotNull(se2);
        assertEquals(300.0, se2.getShippingCost(), 0.01);
        
        orchestrator.processAllEvents();
        assertEquals(8, orchestrator.getEventProcessed());
        assertEquals(4, orchestrator.getProcessorsTriggered());
        
        orchestrator.clearEventOrchestrator();

    }



    @Test
    public void shippingEventShouldTriggerOnly2Processors() {
    	
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator)setupOrchestrator();
        orchestrator.setup(testPublisher);
        
        assertEquals(4, orchestrator.getProcessors().size());
        
        
        Event te1 = new TradeEvent(tradeEventId(), 880.0);
        Event event1 =  prepareEventOneTriggerTwoProcessors(te1);
        
        orchestrator.receive(event1);
        safeSleep(100);
        testPublisher.publish(event1);
        
        assertEquals(1, orchestrator.getEvents().size());
        
        CompositeEvent comevent1 =  (CompositeEvent) testPublisher.getLastEvent();

        assertEquals("tradeEvt", ((TradeEvent) comevent1.getParent()).getId());
        assertEquals(880.0, ((TradeEvent) comevent1.getParent()).getNotional(),0.01);
        assertEquals(2, comevent1.size());
        
        RiskEvent re1 = comevent1.getChildById("tradeEvt-riskEvt");
        assertNotNull(re1);
        assertEquals(70.0, re1.getRiskValue(), 0.01);
        
        RiskEvent re2 = comevent1.getChildById("tradeEvt-riskEvt2");
        assertNotNull(re2);
        assertEquals(80.0, re2.getRiskValue(), 0.01);
        
        orchestrator.processAllEvents();
        assertEquals(3, orchestrator.getEventProcessed());
        assertEquals(2, orchestrator.getProcessorsTriggered());
        
        
        
        orchestrator.clearEventOrchestrator();
        
        orchestrator =  (EventOrchestrator)setupOrchestrator();
        orchestrator.setup(testPublisher);
        Event se2 = new ShippingEvent("ShippingEvent1",200.0);
        Event se3 = new ShippingEvent("ShippingEvent2",300.0);
        Event se4 = new ShippingEvent("ShippingEvent3",310.0);
        Event te2 = new TradeEvent("TradeEvent",790);
        
        orchestrator.receive(se2);
        safeSleep(100);
        testPublisher.publish(se2);
        
        orchestrator.receive(se3);
        safeSleep(100);
        testPublisher.publish(se3);
        
        orchestrator.receive(se4);
        safeSleep(100);
        testPublisher.publish(se4);
        
        orchestrator.receive(te2);
        safeSleep(100);
        testPublisher.publish(te2);
        
        assertEquals(4, orchestrator.getEvents().size());
        
        orchestrator.processAllEvents();
        assertEquals(4, orchestrator.getEventProcessed());
        assertEquals(2, orchestrator.getProcessorsTriggered());
        
        
    }
    
    @Test(expected = EventNotFoundException.class)
    public void NoEventsToProcess(){
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator)setupOrchestrator();
        orchestrator.setup(testPublisher);
        
        orchestrator.processAllEvents();
       
    }
    
    @Test(expected = EventNotFoundException.class)
    public void addNullEventToProcess(){
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator)setupOrchestrator();
        orchestrator.setup(testPublisher);
        orchestrator.receive(null);
        orchestrator.processAllEvents();
       
    }
    
    @Test(expected = ProcessorNotFoundException.class)
    public void NoProcessorsToProcessEvents(){
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator) createOrchestrator();
        orchestrator.setup(testPublisher);
        
        Event se1 = new ShippingEvent("ShippingEvent1",200.0);
        Event se2 = new ShippingEvent("ShippingEvent2",300.0);
        
        orchestrator.receive(se1);
        orchestrator.receive(se2);
        
        orchestrator.processAllEvents();
       
    }
    


	@Test(expected = ProcessorNotFoundException.class)
    public void addNullprocessorsToProcessEvents(){
    	testPublisher = new TestPublisher();
        orchestrator =  (EventOrchestrator)createOrchestrator();
        orchestrator.setup(testPublisher);
        
        Event se1 = new ShippingEvent("ShippingEvent1",200.0);
        Event se2 = new ShippingEvent("ShippingEvent2",300.0);
        orchestrator.receive(se1);
        orchestrator.receive(se2);
        
        orchestrator.register(null);
        
        orchestrator.processAllEvents();
       
    }
    
    @After
    public void cleanUp(){
    	 testPublisher = null;
    	 orchestrator = null;
    }

   
    
    private Event prepareEventOneTriggerAllProcessors(Event parentEvent1){
	    
	    Event re1 = new RiskEvent(riskEventId(parentEvent1.getId()),parentEvent1.getId(),50.0);
	    Event me1 = new MarginEvent(marginEventId(parentEvent1.getId()),parentEvent1.getId(),10.0);
	    Event se1 = new ShippingEvent(shipEventId(parentEvent1.getId()),parentEvent1.getId(),200.0);
	    CompositeEvent ce = new CompositeEvent("eventID1", parentEvent1);
	    ce.addChild(re1);
	    ce.addChild(me1);
	    ce.addChild(se1);
	    
	    return ce;
    }
    
    private Event prepareEventTwoTriggerAllProcessors(Event parentEvent2) {
		// TODO Auto-generated method stub
    	Event re1 = new RiskEvent(riskEventId(parentEvent2.getId()),parentEvent2.getId(),70.0);
 	    Event me1 = new MarginEvent(marginEventId(parentEvent2.getId()),parentEvent2.getId(),15.0);
 	    Event se1 = new ShippingEvent(shipEventId(parentEvent2.getId()),parentEvent2.getId(),300.0);
 	    CompositeEvent ce = new CompositeEvent("eventID2", parentEvent2);
 	    ce.addChild(re1);
 	    ce.addChild(me1);
 	    ce.addChild(se1);
 	    
 	    return ce;
	}
    
    private Event prepareEventOneTriggerTwoProcessors(Event parentEvent) {
		// TODO Auto-generated method stub
    	Event re1 = new RiskEvent(riskEventId(parentEvent.getId()),parentEvent.getId(),70.0);
 	    Event re2 = new RiskEvent(riskEventId(parentEvent.getId()) + "2",parentEvent.getId(),80.0);
 	    CompositeEvent ce = new CompositeEvent("eventID3", parentEvent);
 	    ce.addChild(re1);
 	    ce.addChild(re2);
 	    
 	    return ce;
	}
    
    private Orchestrator setupOrchestrator() {
        Orchestrator orchestrator = createOrchestrator();
        orchestrator.register(new TradeProcessor(orchestrator));
        orchestrator.register(new RiskProcessor(orchestrator));
        orchestrator.register(new MarginProcessor(orchestrator));
        orchestrator.register(new ShippingProcessor(orchestrator));
        return orchestrator;
    }
    


    private void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    private Orchestrator createOrchestrator() {
        //TODO solve the tes
        
        Orchestrator orchestrator = new EventOrchestrator();
        return orchestrator;
    }
}
