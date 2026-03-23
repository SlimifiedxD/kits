package org.slimecraft.kits.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import org.slimecraft.bedrock.util.location.LocationDto

@DatabaseTable(tableName = "map-reset")
class MapReset() {
    @DatabaseField(generatedId = true)
    var id: Int = 0

    @DatabaseField(persisterClass = LocationDtoPersister::class)
    var one: LocationDto? = null

    @DatabaseField(persisterClass = LocationDtoPersister::class)
    var two: LocationDto? = null

    constructor(one: LocationDto, two: LocationDto) : this() {
        this.one = one
        this.two = two
    }
}
