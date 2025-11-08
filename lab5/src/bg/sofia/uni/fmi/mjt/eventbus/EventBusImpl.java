package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.comparators.EventsByTimeStampComparator;
import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventBusImpl implements EventBus {

    Map<Class<? extends Event<?>>, Set<Subscriber<?>>> eventsWithSubscribers = new HashMap<>();
    Map<Class<? extends Event<?>>, Set<Event<?>>> eventLog = new HashMap<>();

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("subscriber is null");
        }

        eventsWithSubscribers.putIfAbsent(eventType, new LinkedHashSet<>());
        eventsWithSubscribers.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("subscriber is null");
        }

        Set<Subscriber<?>> subscribers = eventsWithSubscribers.get(eventType);

        if (subscribers == null || !subscribers.contains(subscriber)) {
            throw new MissingSubscriptionException(String.format("%s does not exist in %s",
                    subscriber, eventType));
        }

        subscribers.remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event is null");
        }

        Set<?> subscribers = eventsWithSubscribers.get(event.getClass());

        //This cast is safe because it is the same event that I want to be a key. It is the one in passed as param
        @SuppressWarnings("unchecked")
        Class<T> eventType = (Class<T>) event.getClass();
        eventLog.putIfAbsent(eventType, new HashSet<>());
        eventLog.get(eventType).add(event);

        if (subscribers == null) {
            return;
        }

        for (var rawSubscriber : subscribers) {
            // Cast is safe because I am sure that
            // the subscriber is able to handle the given evet
            // as we are sure they are subscribed to it
            @SuppressWarnings("unchecked")
            Subscriber<T> subscriber = (Subscriber<T>) rawSubscriber;
            subscriber.onEvent(event);
        }
    }

    @Override
    public void clear() {
        eventLog.clear();
        eventsWithSubscribers.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }

        if (from == null) {
            throw new IllegalArgumentException("from is null");
        }

        if (to == null) {
            throw new IllegalArgumentException("to is null");
        }

        if (from.equals(to) || !eventLog.containsKey(eventType)) {
            return Collections.unmodifiableList(new ArrayList<>());
        }

        List<Event<?>> wantedEvents = new ArrayList<>();

        for (Event<?> event : eventLog.get(eventType)) {
            Instant timestamp = event.getTimestamp();

            if ((timestamp.equals(from) || timestamp.isAfter(from)) && timestamp.isBefore(to)) {
                wantedEvents.add(event);
            }
        }

        wantedEvents.sort(new EventsByTimeStampComparator());
        return List.copyOf(wantedEvents);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type is null");
        }

        Set<Subscriber<?>> subscribers = eventsWithSubscribers.get(eventType);

        if (subscribers == null) {
            return Set.of();
        }

        return Set.copyOf(subscribers);
    }
}
