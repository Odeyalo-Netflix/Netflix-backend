package com.odeyalo.analog.auth.service.events.register;

import com.odeyalo.analog.auth.service.events.Event;
import com.odeyalo.analog.auth.service.events.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserRegistrationConfirmedEventHandler implements EventHandler {
    public static final String USER_REGISTRATION_CONFIRMED_EVENT_VALUE = "USER_REGISTRATION_CONFIRMED_EVENT";
    protected final Logger logger = LoggerFactory.getLogger(AbstractUserRegistrationConfirmedEventHandler.class);

    @Override
    public abstract void handleEvent(Event event);

    @Override
    public String getEventType() {
        return USER_REGISTRATION_CONFIRMED_EVENT_VALUE;
    }

    @Override
    public boolean checkIncomingEvent(Event event) {
        if (!(event.getEventType().equals(USER_REGISTRATION_CONFIRMED_EVENT_VALUE))) {
            this.logger.error("Wrong event was received. Expected event: {}, event type that was received: {}", USER_REGISTRATION_CONFIRMED_EVENT_VALUE, event.getEventType());
            return false;
        }
        if (!(event instanceof UserRegistrationConfirmedEvent)) {
            this.logger.error("Wrong event class was received. Excepted class: {}, received class: {}", UserRegistrationConfirmedEvent.class.getName(), event.getClass().getName());
            return false;
        }
        return true;
    }
}
