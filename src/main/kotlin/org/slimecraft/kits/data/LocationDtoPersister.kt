package org.slimecraft.kits.data

import com.j256.ormlite.field.FieldType
import com.j256.ormlite.field.SqlType
import com.j256.ormlite.field.types.BaseDataType
import com.j256.ormlite.support.DatabaseResults
import org.slimecraft.bedrock.util.location.LocationDto
import java.util.UUID

class LocationDtoPersister private constructor() : BaseDataType(SqlType.STRING, arrayOf<Class<*>>(LocationDto::class.java)) {
    companion object {
        private val SINGLETON = LocationDtoPersister()

        @JvmStatic
        fun getSingleton(): LocationDtoPersister {
            return SINGLETON
        }
    }

    override fun parseDefaultString(fieldType: FieldType?, defaultStr: String?): Any? {
        return stringToLocation(defaultStr)
    }

    override fun javaToSqlArg(fieldType: FieldType?, obj: Any?): Any? {
        return locationToString(obj as LocationDto?)
    }

    override fun sqlArgToJava(fieldType: FieldType?, sqlArg: Any?, columnPos: Int): Any? {
        return stringToLocation(sqlArg as String?)
    }

    override fun resultToSqlArg(fieldType: FieldType?, results: DatabaseResults?, columnPos: Int): Any? {
        return results?.getString(columnPos)
    }

    private fun locationToString(loc: LocationDto?): String? {
        if (loc == null) return null
        return "${loc.world},${loc.x},${loc.y},${loc.z},${loc.pitch},${loc.yaw}"
    }

    private fun stringToLocation(str: String?): LocationDto? {
        if (str.isNullOrEmpty()) return null

        val parts = str.split(",")
        val uuid = UUID.fromString(parts[0])
        val x = parts[1].toDouble()
        val y = parts[2].toDouble()
        val z = parts[3].toDouble()
        val pitch = parts[4].toFloat()
        val yaw = parts[5].toFloat()

        return LocationDto(
            uuid,
            x.toInt(),
            y.toInt(),
            z.toInt(),
            x,
            y,
            z,
            pitch,
            yaw
        )
    }
}