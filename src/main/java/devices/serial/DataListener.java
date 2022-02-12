package devices.serial;

public interface DataListener {
    void receive(byte[] data, int len);
}
