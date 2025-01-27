package NMEA.NMEA;

import java.util.*

class GGA(message: String, throwIfContentInvalid: Boolean = true) :
    Nmea(message, throwIfContentInvalid) {
    val time: String by lazy { data[1] }
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[2],
            Cardinal.valueOf(data[3], Cardinal.N)!!
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[4],
            Cardinal.valueOf(data[5], Cardinal.E)!!
        )
    }
    val quality: Quality by lazy {
        try {
            return@lazy Quality.values().find { it.value == data[6].toInt() }
                ?: Quality.FIX_NOT_AVAILABLE
        } catch (_: NumberFormatException) {
        } catch (_: ArrayIndexOutOfBoundsException) {
        }
        Quality.FIX_NOT_AVAILABLE
    }
    val satelliteCount: Int by lazy { data[7].toIntOrNull() ?: 0 }
    val horizontalDilutionOfPrecision: Double? by lazy { data[8].toDoubleOrNull() }
    val altitude: Double? by lazy { data[9].toDoubleOrNull() }
    val ellipsoidalOffset: Double? by lazy { data[11].toDoubleOrNull() }
    val differentialGpsAge: Double? by lazy { data[13].toDoubleOrNull() }
    val differentialGpsStationId: String by lazy { data[14] }

    constructor(
        type: String,
        time: String = "",
        latitude: Double? = null,
        longitude: Double? = null,
        quality: Quality = Quality.FIX_NOT_AVAILABLE,
        satelliteCount: Int = 0,
        horizontalDilutionOfPrecision: Double? = null,
        altitude: Double? = null,
        ellipsoidalOffset: Double? = null,
        differentialGpsAge: Double? = null,
        differentialGpsStationId: String = ""
    ) : this(
        "$%sGGA,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s".format(
            type.uppercase(Locale.ROOT),
            time,
            latitude?.let { convertLocationNmea(it) } ?: "",
            latitude?.let { if (it < 0) Cardinal.S.name else Cardinal.N.name }
                ?: "",
            longitude?.let { convertLocationNmea(it) } ?: "",
            longitude?.let { if (it < 0) Cardinal.W.name else Cardinal.E.name }
                ?: "",
            quality.value,
            satelliteCount,
            horizontalDilutionOfPrecision ?: "",
            altitude ?: "",
            altitude?.let { 'M' } ?: "",
            ellipsoidalOffset ?: "",
            ellipsoidalOffset?.let { 'M' } ?: "",
            differentialGpsAge ?: "",
            differentialGpsStationId
        ).let { "%s*%s".format(it, computeChecksum(it)) }
    )

    override fun getTokenValidators(): Array<TokenValidator> {
        val optionalDoubleValidator = DoubleValidator(true)
        val meterValidator = EnumValidator(charArrayOf('M'), true)

        return arrayOf(
            // type $__GGA
            TypeValidator("GGA"),
            // UTC time hhmmss(.sss)
            TimeValidator(true),
            // latitude ddmm.ssss
            LatitudeValidator(true),
            // N or S
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.NORTH_SOUTH }
                    .map { it.name.single() }.toCharArray(),
                true
            ),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.WEST_EAST }
                    .map { it.name.single() }.toCharArray(),
                true
            ),
            // quality
            EnumValidator(
                Quality.values().map { it.value.toString().single() }.toCharArray(),
                true
            ),
            // satellite count
            IntValidator(true, 0, 99),
            // horizontal dilution of precision
            optionalDoubleValidator,
            // altitude geoid (mean sea level) in meter
            optionalDoubleValidator,
            // altitude unit M
            meterValidator,
            // WGS-84 earth ellipsoid offset
            optionalDoubleValidator,
            // ellipsoid offset unit M
            meterValidator,
            // age of differential GPS data (seconds)
            optionalDoubleValidator,
            // station
            StringValidator(true)
        )
    }

    enum class Quality(val value: Int) {
        FIX_NOT_AVAILABLE(0),
        GPS_FIX(1),
        DIFFERENTIAL_GPS_FIX(2),
        PPS_FIX(3),
        REAL_TIME_KINEMATIC_INTEGER(4),
        REAL_TIME_KINEMATIC_FLOAT(5),
        ESTIMATED(6),
        MANUAL_INPUT_MODE(7),
        SIMULATION_MODE(8)
    }
}
