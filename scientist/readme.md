### Scientist

Scientist based on the ideas of the [Github Scientist](https://github.com/github/scientist/) implementation.


### Simple Example
```
  Experiment<Integer, Integer> experiment = new Experiment("Next Experiment");

        experiment
                .withControl("BitCount Using binary string", x ->
                        (int) Integer.toBinaryString(x)
                                .chars()
                                .filter(y -> y == '1')
                                .count()
                );

        experiment
                .withCandidate("BitCount using native", x -> Integer.bitCount(x));

        experiment
                .withParamGenerator(() -> 100)
                .compareResult("bit length", (control, candidate) -> control == candidate);

        experiment
                .times(100)
                .publish();
```

### Example with number of times and parallel execution

```
  Experiment<Integer, Integer> experiment = new Experiment("Next Experiment");

        experiment
                .withControl("BitCount Using binary string", x ->
                        (int) Integer.toBinaryString(x)
                                .chars()
                                .filter(y -> y == '1')
                                .count()
                );

        experiment
                .withCandidate("BitCount using native", x -> Integer.bitCount(x));

        experiment
                .withParamGenerator(() -> 100)
                .compareResult("bit length", (control, candidate) -> control == candidate);

        experiment
                .times(100)
                .parallel()
                .run()
                .publish();
```

