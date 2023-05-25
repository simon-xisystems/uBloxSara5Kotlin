package devices.serial.binding;


import devices.serial.LineWriter;
import devices.serial.DataListener;


public class LineWriterDataListener implements DataListener {
    private StringBuffer dataBuffer = new StringBuffer();
    private LineWriter lineWriter;

    public LineWriterDataListener(LineWriter lineWriter) {
        this.lineWriter = lineWriter;
    }

    @Override
    public void receive(byte[] data, int len) {
        dataBuffer.append(new String(data, 0, len));
        int cr;
        while ((cr= dataBuffer.indexOf("\n")) != -1) {
            String line = dataBuffer.substring(0,cr);
            dataBuffer.delete(0,cr+1);
            lineWriter.writeLine(line);
        }
    }
}
