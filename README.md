# RockBlock-SBD
## Background

Libaray forked from (https://github.com/slipperyseal/B9) in order to extract classes used to communicate to a RockBLOCK Iridium Short-Burst Data (SBD) modem (https://www.groundcontrol.com/product/rockblock-9603-compact-plug-play-satellite-transmitter/). 

Code has been refactored to use JSerialComm library as oppsed to the Pi4J library, primarilly to make the code more portable. 

## Use

Quite simply, establish a Desktop Serial Port and pass that to a RockBlock instatiation.

```
   val serialPort = DesktopSerialPort("COM22")
   val rockBlock = RockBlock(serialPort)
   rockBlock.initialize()

```

We can now use the RockBLOCK to send and receive;

```
      println("Signal Level = ${rockBlock.getSignalLevel()}, has Reception? = ${rockBlock.hasReception(2)}")
      println("Status = ${rockBlock.status}")

```
