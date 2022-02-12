package devices;

public interface Device {
    void initialize() throws Exception;

    boolean isHealthy() throws Exception;

    void close() throws Exception;
}
