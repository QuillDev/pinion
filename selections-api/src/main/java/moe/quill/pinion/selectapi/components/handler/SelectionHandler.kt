package moe.quill.pinion.selectapi.components.handler

import moe.quill.pinion.selectapi.components.LocationGroup
import moe.quill.pinion.selectapi.components.NamedLocation
import moe.quill.pinion.selectapi.components.Schematic
import moe.quill.pinion.selectapi.components.Zone
import org.bukkit.Location
import java.util.*

interface SelectionHandler {
    //Selections
    fun getSelection(uuid: UUID): Pair<Location, Location>?
    fun getLeftSelection(uuid: UUID): Location?
    fun getRightSelection(uuid: UUID): Location?
    fun setSelection(uuid: UUID, pair: Pair<Location, Location>)
    fun setLeftSelection(uuid: UUID, location: Location)
    fun setRightSelection(uuid: UUID, location: Location)

    //Adding
    fun addSchematic(schematic: Schematic)
    fun addZone(zone: Zone)
    fun addLocation(name: String, location: Location)
    fun addLocationGroup(name: String, location: MutableList<Location>)

    fun removeSchematic(schematic: Schematic)
    fun removeZone(zone: Zone)
    fun removeLocation(location: NamedLocation)
    fun removeSchematic(name: String)
    fun removeZone(name: String)
    fun removeLocation(name: String)
    fun removeLocationGroup(name: String)

    //Getting
    fun getSchematic(name: String): Schematic
    fun getPossibleSchematic(name: String): Schematic?
    fun getZone(name: String): Zone
    fun getPossibleZone(name: String): Zone?
    fun getLocation(name: String): Location
    fun getPossibleLocation(name: String): Location?
    fun getLocationGroup(name: String): LocationGroup
    fun getPossibleLocationGroup(name: String): LocationGroup?

    fun getSchematics(): List<Schematic>
    fun getZones(): List<Zone>
    fun getLocations(): List<NamedLocation>
}