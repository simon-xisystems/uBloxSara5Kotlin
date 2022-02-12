package devices.rockblock.webservice;

public class MOMessagePrinter implements MOMessageHandler {
    @Override
    public void handle(MOMessage moMessage) {
        System.out.println(moMessage);
    }
}
