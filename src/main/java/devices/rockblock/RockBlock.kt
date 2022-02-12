package devices.rockblock

import devices.Device
import devices.rockblock.AtSession
import devices.serial.SerialConnection
import devices.serial.DataListener
import kotlin.Throws
import java.lang.Exception
import devices.rockblock.message.ShortBurstDataInitiateSession
import devices.rockblock.message.ShortBurstDataStatusExtended
import kotlin.jvm.JvmOverloads
import devices.rockblock.message.Reception
import devices.serial.SerialPort
import java.io.IOException
import java.lang.InterruptedException

class RockBlock(private val serialPort: SerialPort) : Device {
    private var atSession: AtSession? = null
    private var serialConnection: SerialConnection? = null
    private var dataListener: DataListener? = null
    fun setDataListener(dataListener: DataListener?) {
        this.dataListener = dataListener
    }

    @Throws(IOException::class)
    override fun initialize() {
        try {
            serialConnection = serialPort.openConnection(19200)
        } catch (e: Exception) {
            throw IOException(e)
        }
        atSession = AtSession(serialConnection)
        atSession!!.eatNoise()
        try {
            atSession!!.atCommand("ATE0")
        } catch (e: IOException) {
            atSession!!.atCommand("ATE0")
        }
        //        atSession.atCommand("AT+SBDMTA0");   // disable unsolicited SBDRING alerts to the serial
        atSession!!.atCommand("AT&D0") // ignore DTR
        atSession!!.atCommand("AT&K0") // disable RTS/CTS
    }

    @Throws(Exception::class)
    override fun isHealthy(): Boolean {
        return true
    }

    @get:Throws(IOException::class)
    val manufacturer: String
        get() = atSession!!.atCommand("AT+CGMI")

    @get:Throws(IOException::class)
    val model: String
        get() = atSession!!.atCommand("AT+CGMM")

    @get:Throws(IOException::class)
    val revision: String
        get() = atSession!!.atCommand("AT+CGMR")

    @Throws(IOException::class)
    fun sendTestMessage(text: String) {
        atSession!!.atCommand("AT+SBDWT=$text")
    }

    @Throws(IOException::class)
    fun sendBinaryMessage(data: ByteArray) {
        atSession!!.atCommandWriteBinary("AT+SBDWB=" + data.size, data)
    }

    fun getSignalLevel():String{
        val level =  atSession!!.atCommand("AT+CSQ")
       return level.substringAfter("+CSQ:")
    }

    @Throws(IOException::class)
    fun sendAndReceive() {
        waitForReception()
        var shortBurstDataInitiateSession: ShortBurstDataInitiateSession? = null
        do {
            waitForReception()
            shortBurstDataInitiateSession = initiateSession()
            println(shortBurstDataInitiateSession)
            if (shortBurstDataInitiateSession.terminatedStatus == 1) {
                val data = readBinaryMessage()
                if (data.size > 0 && dataListener != null) {
                    dataListener!!.receive(data, data.size)
                }
            }
        } while (shortBurstDataInitiateSession!!.gatewayQueuedCount > 0)
    }

    @Throws(IOException::class)
    fun readBinaryMessage(): ByteArray {
        val data = atSession!!.atCommandReadBinary("AT+SBDRB")
        clearReceivingBuffer()
        return data
    }

    @get:Throws(IOException::class)
    val status: ShortBurstDataStatusExtended
        get() = ShortBurstDataStatusExtended(splitResponseValues(atSession!!.atCommand("AT+SBDSX")))

    @JvmOverloads
    @Throws(IOException::class)
    fun waitForReception(timeout: Long = (1000 * 60 * 3).toLong(), minimumReception: Int = 2): Boolean {
        val testUntil = System.currentTimeMillis() + timeout
        do {
            if (hasReception(minimumReception)) {
                return true
            }
            sleep(2000)
        } while (System.currentTimeMillis() < testUntil)
        return false
    }

    @Throws(IOException::class)
    fun initiateSession(): ShortBurstDataInitiateSession {
        val shortBurstDataInitiateSession = ShortBurstDataInitiateSession(
            splitResponseValues(atSession!!.atCommand("AT+SBDIX"))
        )
        clearSendingBuffer()
        return shortBurstDataInitiateSession
    }

    @Throws(IOException::class)
    fun clearSendingBuffer() {
        atSession!!.atCommand("AT+SBDD0")
    }

    @Throws(IOException::class)
    fun clearReceivingBuffer() {
        atSession!!.atCommand("AT+SBDD1")
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun hasReception(minimumReception: Int = 2): Boolean {
        val reception = Reception(splitResponseValues(atSession!!.atCommand("AT+CSQ")))
        println(reception)
        return reception.reception >= minimumReception
    }

    private fun splitResponseValues(response: String): Array<String> {
        val line = response.substring(response.indexOf(":") + 1)
        val values = line.split(",").toTypedArray()
        for (x in values.indices) {
            values[x] = values[x].trim { it <= ' ' }
        }
        return values
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (i: InterruptedException) {
        }
    }

    @Throws(IOException::class)
    override fun close() {
        serialConnection!!.close()
    }
}