package devices.serial

class DesktopSerialPort(val serialPort: String): SerialPort {
    override fun openConnection(baud: Int): SerialConnection {

        return DesktopSerial(serialPort, baud)
    }
}