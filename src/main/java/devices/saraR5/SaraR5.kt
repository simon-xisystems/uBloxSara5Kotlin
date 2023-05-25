package devices.saraR5

import devices.Device
import devices.serial.AtSession
import devices.serial.DataListener
import devices.serial.SerialConnection
import devices.serial.SerialPort
import java.lang.Thread.sleep

class SaraR5(private val serialPort: SerialPort):Device {
    private var atSession: AtSession? = null
    private var serialConnection: SerialConnection? = null
    private var dataListener: DataListener? = null
    val model: String
        get() =   atSession!!.atCommand("AT+CGMM")

    val serialNumber:String
        get() = atSession!!.atCommand("AT+CGSN")

    fun setDataListener(dataListener: DataListener){
        this.dataListener = dataListener

    }
    override fun initialize() {
        try{
            serialConnection = serialPort.openConnection(115200)

        }catch (e: Exception){
            println("Exception Initialising Serial Connection ${e}")
        }



        try {

            atSession = AtSession(serialConnection)
            atSession!!.eatNoise()

        }catch (e: Exception){
            println("Exception Starting AT Session ${e}")
        }

    }

//    fun getSerialNumber():String{
//        return atSession!!.atCommand("AT+CGSN")
//    }

    fun initialiseGPSNMEAStream(){
        try {
            atSession!!.atCommand("AT+UGPRF=2")     // send and receive GNSS data through the multiplexer
            sleep(15)
            atSession!!.atCommand("AT+USIO=2")      // Configure the dataflow on the SARA-R5 serial interface to the right variant
            sleep(15)
            atSession!!.atCommand("AT+UGIND=1")     //Activate the unsolicited aiding result
            sleep(15)
            atSession!!.atCommand("AT+UGRMC=1")
            sleep(15)
            atSession!!.atCommand("AT+UGGLL=1")
            sleep(15)
            atSession!!.atCommand("AT+UGGSV=1")
            sleep(15)
            atSession!!.atCommand("AT+UGGGA=1")     //Activate storing of last value of NMEA
            sleep(15)
            atSession!!.atCommand("AT+UGPS=1,1,67") //Start the GNSS with GPS+SBAS+GLONASS systems and local aiding.
        }catch (e: Exception){
            println("failed to set nmea string, might already be done ${e}")
        }

        }

    override fun isHealthy(): Boolean {
        TODO("Not yet implemented")
    }

    override fun close() {
        try {
            serialConnection!!.close()
        }catch (e: Exception){
            println("Failed to Close Serial Connection ${e}")
        }
    }
}