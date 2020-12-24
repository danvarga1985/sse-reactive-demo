package com.danvarga.SSE_MVC_Demo;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@RestController
public class TemperatureController {

    /*
     A. SseEmitter's sole purpose is to send SSE events.
     B. CopyOnWriteArraySet can be modified and iterated through the same time. Costly implementation, but ideal for
     storing Subscriptions, that rarely change.
    */
    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @RequestMapping(value = "/temperature-stream", method = RequestMethod.GET)
    public SseEmitter events(HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);

        /*
         When a request-handling method returns an SseEmitter instance, the actual request processing continues until
         (a) SseEmitter.complete(), (b) an error or a (c) timeout occurs.
        */
        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onCompletion(() -> clients.remove(emitter));

        return emitter;
    }

    /*
     A. Handles the reception of events about temperature changes. @EventListener makes sure that only Temperature
     events will invoke the method.
     B. @Async makes it sure, that the method is invoked in the manually configured thread-pool.
    */
    @Async
    @EventListener
    public void handleMessage(Temperature temperature) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        clients.forEach(sseEmitter -> {
            try {
                // Message sent asynchronously in JSON format in parallel for each event.
                sseEmitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception ignore) {
                /*
                 SseEmitter does not provide any callback for error handling -> errors are handled when thrown by
                 the send() method.
                */
                deadEmitters.add(sseEmitter);
            }
        });

        clients.removeAll(deadEmitters);
    }

}
