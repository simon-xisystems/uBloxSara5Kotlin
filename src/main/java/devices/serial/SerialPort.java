package devices.serial;

import java.io.IOException;

public interface SerialPort {
    SerialConnection openConnection(int baud) throws IOException;
}
