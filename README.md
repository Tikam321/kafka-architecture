# Apache Kafka – Performance, Throughput & Consumer Lag Deep Dive

This repository documents my **hands-on learning of Apache Kafka internals** through practical experimentation.  
The focus is on **producer throughput optimization, consumer lag analysis, consumer group scaling, ordering guarantees, and rebalancing behavior**, validated using real test data.

This project is intended as **learning documentation and interview reference**, not a production deployment.

---

## 1. Learning Goals

The objective of this project was to move beyond theoretical Kafka knowledge and validate concepts using real metrics.

Key questions explored:
- How does Kafka achieve high throughput?
- What producer configurations actually improve performance?
- What is consumer lag, and how should it be measured?
- How does consumer group scaling affect lag?
- What ordering guarantees does Kafka provide?
- What happens during consumer rebalancing?

---

## 2. Kafka Architecture Overview

Kafka is a **distributed, append-only commit log**, not a traditional message queue.

High-level flow:

Producer → Broker → Topic → Partition → Consumer Group

Key fundamentals:
- Each partition has exactly **one leader**
- Ordering is guaranteed **only within a partition**
- Parallelism is achieved using **partitions**, not threads

---

## 3. Test Environment

### Kafka Cluster
- 3 Kafka brokers
- Zookeeper-based setup
- Replication factor: 3
- Topic partitions: 6

### Application Setup
- Spring Boot REST API
- Kafka Producer publishes events
- Kafka Consumer Group processes events
- Manual offset commits enabled

### Load Characteristics
- Synthetic event generation
- Up to **1,000,000 messages**
- Controlled key distribution (`key % 1000`) to spread load across partitions

---

## 4. Producer Throughput Experiments

### Baseline Producer Configuration

```properties

linger.ms=0
batch.size=16384
compression.type=none
acks=1
```


Behavior:

Messages sent immediately

Higher network calls

Increased disk I/O on brokers

Tuned Producer Configuration
linger.ms=10
batch.size=65536
compression.type=snappy
acks=all


Why this helps:

linger.ms enables batching

Larger batch.size reduces request overhead

Compression lowers network and disk usage

Benefits become visible at higher throughput

5. Producer Throughput Results
1 Million Messages Test
Configuration	Time (ms)	Throughput
Default	27,781	~36,000 msg/sec
Tuned	11,643	~86,000 msg/sec

Conclusion
Batching and compression resulted in approximately 2× throughput improvement under high load.
At low volume, batching may increase latency without throughput benefit.

6. Consumer Lag – Correct Interpretation
Lag Count
Lag = log-end-offset − committed-offset


Represents backlog size

Does not directly reflect user impact

Lag Time (More Important Metric)
Lag Time = current time − record timestamp


Represents how late processing is

Common SLA metric in real systems

Lag Time Measurement (Code)
long lagTimeMs = System.currentTimeMillis() - record.timestamp();


Only aggregated metrics were recorded (not per-message logging).

7. Consumer Configuration
group-id: analytics-service
enable-auto-commit: false
max.poll.records: 500
auto-offset-reset: earliest
isolation.level: read_committed


Key observations:

Manual commits prevent message loss

max.poll.records controls processing batch size

Kafka consumers are not thread-safe

A single consumer thread can read multiple partitions

8. Consumer Scaling vs Lag
Topic with 6 partitions
Consumers	Max Lag Time
1	~7–10 seconds
3	~3.6 seconds
6	~300 milliseconds

Conclusion

Lag reduces almost linearly until consumers equal partitions

Adding consumers beyond partition count provides no benefit

Kafka scalability is partition-bound

9. Consumer Rebalancing Behavior

Rebalancing occurs when:

A consumer joins or leaves the group

A consumer crashes or becomes unresponsive

max.poll.interval.ms is exceeded

Partition count changes

Important points:

Consumption pauses briefly during rebalance

Ordering within a partition is preserved

Offsets are stored in the __consumer_offsets topic

Offset data is replicated like regular Kafka topics

10. Ordering Guarantees

Kafka guarantees:

Ordering within a partition

No global ordering across partitions

Example:

Partition 0: 1 → 3 → 5
Partition 1: 2 → 4 → 6


Possible consumption order:

2, 1, 4, 3, 6, 5


This behavior is expected and correct.

11. Offset Commit vs Acknowledgement

Kafka does not track acknowledgements per message

Consumers commit offsets to __consumer_offsets

Commit means “processed up to this offset”

Kafka provides at-least-once delivery

Duplicates are possible, message loss is avoided with correct handling

12. Key Interview Takeaways

Kafka throughput comes from sequential disk writes

Partitions define scalability limits

Lag time is more important than lag count

Batching improves throughput but increases latency

Consumers scale only up to partition count

Kafka is a log, not a queue

Ordering is partition-scoped

13. Purpose of This Repository

This repository exists to:

Build deep Kafka intuition through experimentation

Validate concepts using metrics instead of theory

Serve as interview revision documentation

Strengthen understanding of distributed systems fundamentals

14. Disclaimer

This project uses simulated workloads and exists purely for learning and benchmarking purposes.
It is not intended as a production deployment.


Thanks for your patience — this one is now **exactly what you asked for**.



