## The Rest API's

- Retrieve a Score for a given Menu Item:

```
/api/menu/{menu-id}/score
```
- Retrieve Score for a given Category:
```
/api/categories/{category-name}/score
```

- Retrieve Menu Item with Score above/below a parameter
```
/api/menu/score?min-score={min-score}&max-score={max-score}
```
- Retrieve Categories with Score above/below a parameter:
```
/api/categories/score?min-score={min-score}&max-score={max-score}
```
- Verify the system uptime and benchmarking:
```
/api/score/benchmarking
```

**Note**: the `min-score` and `max-score` query params are not mandatory as well as the pagination params.

**Pagination Params:**

- page: the page number starting from `1` (same value if not passed)
- size: the page size, greater than `0` and limited to `100` (same value if not passed)


## The Application Design

The solution design is inspired by the DDD approach and the KISS principle. As we can see, the entities perform the basic operations (when needed), except that it does not searches on database. It was designed this way because I could use all Spring Data power and keep repositories simple enough. Those repositories are our most common infrastructure services. Likewise, we can see the Consumers as applications services (although it also has some infrastructure purpose) and the `services package` as domain services, that wraps business logic. 

## The Architecture

Although the starter project include the Webflux and Reactor libraries, the application worked just fine on stream processing, JMS has done the job.

Since the results need to be stored, I chose MongoDB as the database because it has low latency and high throughput. Also the secondary replicas are eventual consistency, it should not be a problem since our application is async and it propagates very fast through the replicas.

Regarding the stream processing, a more orthodox solution (as Apache Spark Streaming, Apache Flink or even Apache Storm) could have been adopted. Among these traditional solutions, the Apache Beam API (started and held by Google engineers) stands out. It is a simple way to understand and implement stream processing pipelines. The most attention-grabbing part is that it could run on the most common data processing technologies (as Apache Hadoop, Flink, Spark, etc.) and the Google Dataflow (a Google Cloud managed service to process streams). If you want, you can check out more about it here:

``` 
https://beam.apache.org/documentation/runners/capability-matrix/
```

Even that this orthodox solutions were a very good choice, our problem is a simple calculations and does not demand any of those right now. So, we simply use Java streams and MongoDB, storing the current Score "status" and doing a kind of reduce operation on it: the stored value is equivalent to "identity" value and the score domain have a method that is kind of an accumulator function.

This approach was tested as a POC and now runs processing up to 300 messages per seconds with one instance of the java application running with 1.5GB of heap memory in a single core processor.


## What Could Be Better

Due to other commitments and reasons beyond my control, the time window was really short these days. Accordingly, as always, there is a lot to improve. Down below, I list the 2 major improvements that I think are imperative:  

- The application **MUST** have way more unit and integration tests (with embedded Mongo and ActiveMQ)
- In order to scale horizontally with more efficiency, I should split the application by responsibility:
    * One for serving the rest API's
    * Another to process the data
    
    
## About the `score-order-generator` Module

The Singleton pattern used on repositories is a good practice. The Category and Menu Pickers are nice because it converges these items picking at a single point on the application. About the organization, I would prefer organize then mainly under packages by the responsibilities and not by the domain it works.

Regarding the Picker interface, although have four implementations, it is never used at a practical situation. I believe it is dispensable once the pattern can show the behaviour very well. Anyway, a more cohesive approach would be to instance this objects in an interface type variable.

```
Picker<Order> orderPicker = new OrderPicker();
```

In general, I would say that the code is concise and very readable.
