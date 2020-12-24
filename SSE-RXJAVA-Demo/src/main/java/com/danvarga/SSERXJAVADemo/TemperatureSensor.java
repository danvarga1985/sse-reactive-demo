package com.danvarga.SSERXJAVADemo;

import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class TemperatureSensor {

    // Used for the Temperature value- and the delay value generation.
    private final Random rnd = new Random();

    private final Observable<Temperature> dataStream =
            Observable
                    // Generate an 'endless (Int.MAX)' flow of numbers.
                    .range(0, Integer.MAX_VALUE)
                    .concatMap(tick -> Observable
                            .just(tick)
                            .delay(rnd.nextInt(5000), TimeUnit.MILLISECONDS)
                            // Each value generated gets transformed into a random number.
                            .map(tickValue -> this.probe()))
                    /*
                     Without publishing, each subscriber would trigger a new subscription, and
                     a new sequence of sensor-readings.
                    */
                    .publish()
                    /*
                     'publish()' returns with a 'ConnectedObservable', that provides the '
                     refCount' operator, which creates a subscription to the incoming shared stream
                     only when there is at least one outgoing subscription.
                    */
                    .refCount();

    private Temperature probe() {
        return new Temperature(16 + rnd.nextGaussian() * 10);
    }

    public Observable<Temperature> temperatureStream() {
        return dataStream;
    }
}
