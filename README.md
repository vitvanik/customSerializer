# Java Test Exercise - Сustom byte array serializer

benchmark result:

```text
# Run complete. Total time: 00:03:25
Benchmark                      Mode  Cnt       Score      Error  Units
BenchmarkRunner.java          thrpt   20   21909,403 ±  165,353  ops/s
BenchmarkRunner.json          thrpt   20  232266,486 ± 2416,855  ops/s
BenchmarkRunner.kryo          thrpt   20  203200,530 ± 1998,266  ops/s
BenchmarkRunner.mySerializer  thrpt   20   21517,540 ±  199,320  ops/s
BenchmarkRunner.protobuf      thrpt   20  388131,864 ± 2974,000  ops/s
```