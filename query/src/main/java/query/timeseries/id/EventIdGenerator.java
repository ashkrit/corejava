package query.timeseries.id;

public interface EventIdGenerator {
    String next();

    String next(long ms);
}
