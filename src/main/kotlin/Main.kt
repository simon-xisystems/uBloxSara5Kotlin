import devices.rockblock.RockBlock
import devices.serial.DesktopSerialPort
import java.lang.Thread.sleep

fun main(){

   val serialPort = DesktopSerialPort("COM22")
   val rockBlock = RockBlock(serialPort)
   rockBlock.initialize()

   println("##### Rock Block Irridium Library ########")
   println("Device Manufacturer = ${rockBlock.manufacturer}")
   println("Device revision Number = ${rockBlock.revision}")
   rockBlock.clearReceivingBuffer()
   rockBlock.clearSendingBuffer()


   while(true){
      println("Signal Level = ${rockBlock.getSignalLevel()}")
      println("Status = ${rockBlock.status}")


      sleep(5000)



   }


}