package com.danvarga.SSERXJAVADemo;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Subscriber;

import java.io.IOException;

public class RxSeeEmitter extends SseEmitter {

    static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
    private final Subscriber<Temperature> subscriber;

    // Encapsulate a subscriber for Temperature events.
    public RxSeeEmitter() {
        super(SSE_SESSION_TIMEOUT);

        this.subscriber = new Subscriber<Temperature>() {
            @Override
            public void onCompleted() {
                //TODO: missing impl
            }

            @Override
            public void onError(Throwable throwable) {
                //TODO: missing impl
            }

            @Override
            public void onNext(Temperature temperature) {
                try {
                    // Resend the signal s to an SSE client.
                    RxSeeEmitter.this.send(temperature);
                } catch (IOException e) {
                    unsubscribe();
                }
            }
        };

        // Cleanup for completion & timeout.
        onCompletion(subscriber::unsubscribe);
        onTimeout(subscriber::unsubscribe);
    }

    Subscriber<Temperature> getSubscriber() {
        return subscriber;
    }
}
