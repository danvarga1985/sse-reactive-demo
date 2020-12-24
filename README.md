# server-sent-events-mvc-demo
Demo project for Server-Sent Events with Spring Boot


Problems with the @EventListener implementation:

-@EventListener was introduced for handling application life cycle events, and was not intended for high-load, high-performance scenarios.

-Using an internal framework mechanism to define and implement business logic makes the app vulnerable to changes in the framework.

-SseEmitter has a notion for errors and the ends of streams, but @EventListener doesn't. The definition of special objects or class hierarchies needed to signal the end of streams or errors.

-Allocation of the thread pool is needed to asynchronously broadcast temperature events.

-Events are generated after application start, regardless of requests.
