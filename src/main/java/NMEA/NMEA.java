package NMEA;

import java.util.HashMap;
import java.util.Map;
public class NMEA {
    // fucking java interfaces
    interface SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position);
    }

    // utils
    static float Latitude2Decimal(String lat, String NS) {
        int minutesPosition = lat.indexOf('.') - 2;
        float minutes = Float.parseFloat(lat.substring(minutesPosition));
        float decimalDegrees = Float.parseFloat(lat.substring(minutesPosition))/60.0f;

        float degree = Float.parseFloat(lat) - minutes;
        float wholeDegrees = (int)degree/100;

        float latitudeDegrees = wholeDegrees + decimalDegrees;
        if(NS.startsWith("S")) {
            latitudeDegrees = -latitudeDegrees;
        }
        return latitudeDegrees;
    }

    static float Longitude2Decimal(String lon, String WE) {
        int minutesPosition = lon.indexOf('.') - 2;
        float minutes = Float.parseFloat(lon.substring(minutesPosition));
        float decimalDegrees = Float.parseFloat(lon.substring(minutesPosition))/60.0f;

        float degree = Float.parseFloat(lon) - minutes;
        float wholeDegrees = (int)degree/100;

        float longitudeDegrees = wholeDegrees + decimalDegrees;
        if(WE.startsWith("W")) {
            longitudeDegrees = -longitudeDegrees;
        }
        return longitudeDegrees;
    }

    // parsers
    class GPGGA implements SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position) {
            position.time = Float.parseFloat(tokens[1]);
            position.lat = Latitude2Decimal(tokens[2], tokens[3]);
            position.lon = Longitude2Decimal(tokens[4], tokens[5]);
            position.quality = Integer.parseInt(tokens[6]);
            position.altitude = Float.parseFloat(tokens[9]);
            return true;
        }
    }

    class GPGGL implements SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position) {
            position.lat = Latitude2Decimal(tokens[1], tokens[2]);
            position.lon = Longitude2Decimal(tokens[3], tokens[4]);
            position.time = Float.parseFloat(tokens[5]);
            return true;
        }
    }

    class GPRMC implements SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position) {
            position.time = Float.parseFloat(tokens[1]);
            position.lat = Latitude2Decimal(tokens[3], tokens[4]);
            position.lon = Longitude2Decimal(tokens[5], tokens[6]);
            position.velocity = Float.parseFloat(tokens[7]);
            position.dir = Float.parseFloat(tokens[8]);
            return true;
        }
    }

    class GPVTG implements SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position) {
            position.dir = Float.parseFloat(tokens[3]);
            return true;
        }
    }

    class GPRMZ implements SentenceParser {
        public boolean parse(String [] tokens, GPSPosition position) {
            position.altitude = Float.parseFloat(tokens[1]);
            return true;
        }
    }

    public class GPSPosition {
        public float time = 0.0f;
        public float lat = 0.0f;
        public float lon = 0.0f;
        public boolean fixed = false;
        public int quality = 0;
        public float dir = 0.0f;
        public float altitude = 0.0f;
        public float velocity = 0.0f;

        public void updatefix() {
            fixed = quality > 0;
        }

        public String toString() {
            return String.format("POSITION: lat: %f, lon: %f, time: %f, Q: %d, dir: %f, alt: %f, vel: %f", lat, lon, time, quality, dir, altitude, velocity);
        }
    }

    GPSPosition position = new GPSPosition();

    private static final Map<String, SentenceParser> sentenceParsers = new HashMap<String, SentenceParser>();

    public NMEA() {
        sentenceParsers.put("GPGGA", new GPGGA());
        sentenceParsers.put("GPGGL", new GPGGL());
        sentenceParsers.put("GPRMC", new GPRMC());
        sentenceParsers.put("GPRMZ", new GPRMZ());
        //only really good GPS devices have this sentence but ...
        sentenceParsers.put("GPVTG", new GPVTG());
    }

    public GPSPosition parse(String line) {

        if(line.startsWith("$")) {
            String nmea = line.substring(1);
            String[] tokens = nmea.split(",");
            String type = tokens[0];
            //TODO check crc
            if(sentenceParsers.containsKey(type)) {
                System.out.println("recieved this type " + type );
                sentenceParsers.get(type).parse(tokens, position);
            }
            position.updatefix();
        }

        return position;
    }
}
