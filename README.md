# azeroth-event
Lightweight event-driven framework

[中文说明/Chinese Documentation](https://github.com/edagarli/azeroth-event/blob/master/README_CN.md)

>Better decoupling between components allows us to decouple our modules and domain boundaries in a domain-driven design (DDD) with a weak reference nature of events.
 
>Originally used as an internal plugin for business decoupling and event distribution. In the ongoing optimization, interested can pay attention to it.

## Structure diagram

![event](static/event.png)

### Quick start

```
// init eventbus plugin
EventBus plugin = new EventBus();
//Asynchronous globally enabled, if not enabled by default, synchronous blocking processing
plugin.async(1024, 8);
// Scan the class that contains the @Listener annotation method AND Implement the ApplicationEventListener interface
//Set the scan jar package to include the imported third-party jar package, which is not scanned by default.
plugin.scanJar();
// Set the default scan packet life, the default full scan
plugin.scanPackage("com.github.edagarli.eventbus.listener");
// plugin start
plugin.start();

// send the first message
plugin.publish("123", new EventSource("test"));
// send the second message
plugin.publish("123", new EventSource("test111111"));
// One-to-many, one event, triggering multiple event processing
// Coexisting multiple events with the same tag. Providing priority processing. The smaller the value of priority, the greater the priority.
plugin.publish("test", new EventSource("123123"));

Awaitility.await().atMost(2, TimeUnit.MINUTES).until(new Callable<Boolean>() {
@Override
   public Boolean call() throws Exception {
	  return eventBus.stop();
   }
});
```
maven jar~

```
<dependency>
  <groupId>com.github.edagarli</groupId>
  <artifactId>azeroth-event</artifactId>
  <version>1.0.0</version>
</dependency>
```
## Release Notes

>## DOING
>
>1. Code refactoring, internal responsibilities are more subdivided, and convenient for extended point expansion
>2. Improvement of execution strategy
>3. Support rule triggering, support time rule triggering
>4. Support el dynamic expressions
>5. Final consistency (persistent processing such as placement, increase retry mechanism)

>## 2019-01-12 v1.0.0
>1. Support concurrency, accelerate service processing efficiency, event publishing and asynchronous processing capability (disruptor)
>2. In-project service decoupling Observers and publishers do not interfere with each other
>3. Imitate the spring event-driven model. One-to-many release one event to trigger multiple event processing.
>4. Add multiple listener priority processing rights. In the case of asynchronous processing, the order of submission is guaranteed, and the order of execution is not guaranteed.
>5. Asynchronous queues support graceful downtime to ensure the reliability of memory channels
>6. Increase the threshold. In asynchronous mode, the ringbuffer exceeds the threshold. Automatically switch back to synchronous. Return to normal and switch to asynchronous mode again.

## Projects

[Disruptor](https://github.com/LMAX-Exchange/disruptor)

[spring event-driven model](https://docs.microsoft.com/bs-latn-ba/azure/architecture/guide/architecture-styles/event-driven)

