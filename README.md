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

We also have a helper function to wait for signal and then to send data, used like this;

```
 rockBlock.sendTestMessage("HEllO WORLD, DOES This Send?")
 rockBlock.waitForReception(1000, 2) //wait for signal
 rockBlock.sendAndReceive() //tell rockblock to complete a tx/rx cycle

```
