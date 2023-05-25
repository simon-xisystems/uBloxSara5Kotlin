package NMEA

data class GPSFix(
    val Latitude: Double,
    val Longitude: Double,
    val speed: Double,
    val time: String
)
