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

   rockBlock.sendTestMessage("HEllO WORLD, DOES This Send?")
/*
*  rockBlock.waitForReception(1000, 2) //wait for signal
   rockBlock.sendAndReceive() //tell rockblock to complete a tx/rx cycle
*
* */


   while(true){
      println("Signal Level = ${rockBlock.getSignalLevel()}, has Reception? = ${rockBlock.hasReception(2)}")
      println("Status = ${rockBlock.status}")
      sleep(5000)



   }



}