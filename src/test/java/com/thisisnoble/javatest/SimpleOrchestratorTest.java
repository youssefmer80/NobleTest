package com.thisisnoble.javatest;

import com.thisisnoble.javatest.events.MarginEvent;
import com.thisisnoble.javatest.events.RiskEvent;
import com.thisisnoble.javatest.events.ShippingEvent;
import com.thisisnoble.javatest.events.TradeEvent;
import com.thisisnoble.javatest.impl.CompositeEvent;
import com.thisisnoble.javatest.processors.MarginProcessor;
import com.thisisnoble.javatest.processors.RiskProcessor;
import com.thisisnoble.javatest.processors.ShippingProcessor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleOrchestratorTest {

    @Test
    public void tradeEventShouldTriggerAllProcessors() {
        TestPublisher testPublisher = new TestPublisher();
        Orchestrator orchestrator = setupOrchestrator();
        orchestrator.setup(testPublisher);

        TradeEvent te = new TradeEvent("trade1", 1000.0);
        orchestrator.receive(te);
        safeSleep(100);
        CompositeEvent ce = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(te, ce.getParent());
        assertEquals(3, ce.size());
        for (Event evt : ce.getChildren()) {
            if (evt instanceof RiskEvent) {
                assertEquals(500.0, ((RiskEvent) evt).getRiskValue(), 0.01);
            } else if (evt instanceof ShippingEvent) {
                assertEquals(100.0, ((ShippingEvent) evt).getShippingCost(), 0.01);
            } else if (evt instanceof MarginEvent) {
                assertEquals(100.0, ((MarginEvent) evt).getMargin(), 0.01);
            }
        }
    }

    @Test
    public void shippingEventShouldTriggerOnly2Processors() {
        TestPublisher testPublisher = new TestPublisher();
        Orchestrator orchestrator = setupOrchestrator();
        orchestrator.setup(testPublisher);

        ShippingEvent se = new ShippingEvent("ship1", 200.0);
        orchestrator.receive(se);
        safeSleep(100);
        CompositeEvent ce = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(se, ce.getParent());
        assertEquals(2, ce.size());
        for (Event evt : ce.getChildren()) {
            if (evt instanceof ShippingEvent) {
                assertEquals(210.0, ((ShippingEvent) evt).getShippingCost(), 0.01);
            } else if (evt instanceof MarginEvent) {
                assertEquals(20.0, ((MarginEvent) evt).getMargin(), 0.01);
            }
        }
    }

    private Orchestrator setupOrchestrator() {
        Orchestrator orchestrator = createOrchestrator();
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
        //TODO solve the test
        throw new UnsupportedOperationException();
    }
}
