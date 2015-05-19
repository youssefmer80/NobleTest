package com.thisisnoble.javatest.processors;

import com.thisisnoble.javatest.Event;
import com.thisisnoble.javatest.Orchestrator;
import com.thisisnoble.javatest.events.TradeEvent;


public class TradeProcessor extends AbstractProcessor {

    public TradeProcessor(Orchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean interestedIn(Event event) {
        return event instanceof TradeEvent;
    }

    @Override
    protected Event processInternal(Event event) {
        String parId = event.getId();
        if (event instanceof TradeEvent)
            return new TradeEvent( parId, calculateTradeRisk(event));
        throw new IllegalArgumentException("unknown event for trade " + event);
    }
    
    private double calculateTradeRisk(Event te) {
        return ((TradeEvent) te).getNotional() * 0.05;
    }

}
