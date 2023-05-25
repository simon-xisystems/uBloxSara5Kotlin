import devices.rockblock.RockBlock
import devices.saraR5.GpsNMEARxer
import devices.saraR5.SaraR5
import devices.serial.DesktopSerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

fun main() {
    println("##### uBlox Sara R5 ########")
    val serialPort = DesktopSerialPort("/dev/ttyGSM2")
    val saraR5 = SaraR5(serialPort)
    saraR5.initialize()



    println("##### Device ${saraR5.serialNumber} attached #######")

    println("Configuring CMUX and module NMEA settings. ")

    try {
        saraR5.initialiseGPSNMEAStream()
    } catch (e: Exception) {
        println("failed to config NMEA Stream ${e}")
    }


    println("initialising GPS NMEA Stream.")

    val gps = GpsNMEARxer("/dev/ttyGSM3")


        gps.openSerialPort()
        gps.startNmeaStream()

    while(true){

        if(gps.locationFix != null){
            println("Do we have a fix? ${gps.locationFix}")
        }

        sleep(5000)


    }

//        gps.locationFix?.onEach {
//            println("new GPS Data - ${it}")
//        }








}

