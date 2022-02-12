package devices.serial

import java.io.InputStream

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.io.IOException

class DesktopSerial(port: String, baud:Int): SerialConnection {
    val serialPort = SerialPort.getCommPort(port)
    init {
        serialPort.setComPortParameters(baud, 8, 1, 0)
        serialPort.openPort()

    }

    override fun write(data: ByteArray?) {
        data?.size?.toLong()?.let { serialPort.writeBytes(data, it) }
    }



    override fun close() {
        serialPort.closePort()
    }

    override fun getInputStream(): InputStream {
        return serialPort.inputStream
    }

    override fun setDataListener(dataListener: DataListener?) {
        val data = serialPort.addDataListener(object : SerialPortDataListener {
            override fun getListeningEvents(): Int {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE

            }

            override fun serialEvent(event: SerialPortEvent) {

                dataListener?.receive(ByteArray(serialPort.bytesAvailable()), serialPort.bytesAvailable())

            }
        })
    }
}