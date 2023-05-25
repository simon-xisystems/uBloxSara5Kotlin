package devices.saraR5

import NMEA.GPSFix
import NMEA.NMEA
import NMEA.NMEA.GGA
import NMEA.NMEA.GLL
import NMEA.NMEA.RMC
import devices.serial.DataListener
import devices.serial.SerialConnection
import devices.serial.SerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.DataInputStream


class GpsNMEARxer(val port: String) {

    private var streamGps = false

    var locationFix: GPSFix? = null



    fun openSerialPort() {
        try{
//            serialPort = com.fazecast.jSerialComm.SerialPort.getCommPort(this.port)
//            serialPort?.openPort()
        }catch (e: Exception){
            println("Failed to open NMEA Serial Port ${e}")
        }
    }

    fun stopNmeaStream() {
        streamGps = false
    }


    fun startNmeaStream() {
        streamGps = true
        println("Starting Stream Connection")
        val serialPort =  com.fazecast.jSerialComm.SerialPort.getCommPort(this.port)
        serialPort.openPort()
        println("Serial Port Open")
        val reader = serialPort?.inputStream?.bufferedReader()
        val nmea = NMEA()

        CoroutineScope(Dispatchers.Default).launch {
            while (streamGps) {
                if (serialPort.bytesAvailable()> 0) {
                    val rxData = reader?.readLine()
                    nmea.parse(rxData)
                    when {
                        rxData!!.contains("\$GNGLL")-> {
                            try {
                                val gll = GLL(rxData)
                                val position = GPSFix(
                                    Latitude = gll.latitude!!,
                                    Longitude = gll.longitude!!,
                                    speed = 0.00,
                                    time = gll.time
                                )
                               // println("update Fix from gll")
                                locationFix = position

                            } catch (e: Exception) {
                                println("failed to parse GLL ${e}")
                            }
                        }

                        rxData.contains("\$GPGGA") -> {
                            try {
                                val gga = GGA(rxData)
                                //println("${gga.altitude}, ${gga.latitude}, ${gga.longitude}, ${gga.satelliteCount}")
                            } catch (e: Exception) {
                                println("failed to parse gga ${e}")
                            }

                        }

                        rxData.contains("\$GNRMC")-> {
                            try {
                                val rmc = RMC(rxData)
                                //println("RMC: long, ${rmc.longitude},lat,  ${rmc.latitude}, time, ${rmc.time},date,  ${rmc.date},speed, ${rmc.speed}")
                                val position = GPSFix(
                                    Latitude = rmc.latitude!!,
                                    Longitude = rmc.longitude!!,
                                    speed = rmc.speed!!,
                                    time = rmc.time
                                )
                                //intln("update Fix from rmc")
                                locationFix = position
                            } catch (e: Exception) {
                                println("failed to parse rmc ${e}")
                            }
                        }

                        else -> {
                            //println(rxData.substringAfter("$").substringBefore(","))
                        }
                    }
                }
            }
        }

    }
}