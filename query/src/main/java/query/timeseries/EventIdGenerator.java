package query.timeseries;

public interface EventIdGenerator {
    String next();

    String next(long ms);
}
