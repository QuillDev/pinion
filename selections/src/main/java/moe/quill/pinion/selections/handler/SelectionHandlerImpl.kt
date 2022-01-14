package moe.quill.pinion.selections.handler

import moe.quill.pinion.commands.CommandProcessor
import moe.quill.pinion.selectapi.components.NamedLocation
import moe.quill.pinion.selectapi.components.Schematic
import moe.quill.pinion.selectapi.components.Zone
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import moe.quill.pinion.selections.managers.locations.LocationCommands
import moe.quill.pinion.selections.managers.locations.LocationManager
import moe.quill.pinion.selections.managers.schematic.SchematicCommands
import moe.quill.pinion.selections.managers.schematic.SchematicManager
import moe.quill.pinion.selections.managers.zones.ZoneCommands
import moe.quill.pinion.selections.managers.zones.ZoneManager
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.*

//TODO: Convert manager classes to interfaces with implementations as well?, makes sense
class SelectionHandlerImpl(plugin: Plugin, commandProcessor: CommandProcessor) : SelectionHandler {
    val selections = mutableMapOf<UUID, Pair<Location, Location>>()
    private val leftSelections = mutableMapOf<UUID, Location>()
    private val rightSelection = mutableMapOf<UUID, Location>()

    private val locationManager = LocationManager(plugin)
    private val zoneManager = ZoneManager(plugin)
    private val schematicManager = SchematicManager(plugin)

    init {
        commandProcessor.registerCommand(LocationCommands(this))
        commandProcessor.registerCommand(ZoneCommands(this))
        commandProcessor.registerCommand(SchematicCommands(this))

        commandProcessor.registerTranslator(NamedLocation::class, locationManager)
        commandProcessor.registerTranslator(Zone::class, zoneManager)
        commandProcessor.registerTranslator(Schematic::class, schematicManager)
    }

    //Selection Stuff
    override fun getSelection(uuid: UUID): Pair<Location, Location>? {
        return selections[uuid]
    }

    override fun getLeftSelection(uuid: UUID): Location? {
        return leftSelections[uuid]
    }

    override fun getRightSelection(uuid: UUID): Location? {
        return rightSelection[uuid]
    }

    override fun setSelection(uuid: UUID, pair: Pair<Location, Location>) {
        selections[uuid] = pair
    }

    override fun setLeftSelection(uuid: UUID, location: Location) {
        leftSelections[uuid] = location
    }

    override fun setRightSelection(uuid: UUID, location: Location) {
        rightSelection[uuid] = location
    }

    //Adding stuff
    override fun addSchematic(schematic: Schematic) {
        schematicManager.save(schematic)
    }

    override fun addZone(zone: Zone) {
        zoneManager.save(zone)
    }

    override fun addLocation(name: String, location: Location) {
        locationManager.save(name, location)
    }


    override fun removeSchematic(schematic: Schematic) {
        removeSchematic(schematic.name)
    }

    override fun removeSchematic(name: String) {
        schematicManager.remove(name)
    }

    override fun removeZone(zone: Zone) {
        removeZone(zone.name)
    }

    override fun removeZone(name: String) {
        zoneManager.remove(name)
    }

    override fun removeLocation(location: NamedLocation) {
        removeLocation(location.name)
    }

    override fun removeLocation(name: String) {
        locationManager.remove(name)
    }


    override fun getSchematic(name: String): Schematic {
        return getPossibleSchematic(name)!!
    }

    override fun getPossibleSchematic(name: String): Schematic? {
        return schematicManager.get(name)
    }

    override fun getZone(name: String): Zone {
        return getPossibleZone(name)!!
    }

    override fun getPossibleZone(name: String): Zone? {
        return zoneManager.get(name)
    }

    override fun getPossibleLocation(name: String): Location? {
        return locationManager.get(name)

    }

    override fun getLocation(name: String): Location {
        return locationManager.get(name)!!
    }

    override fun getSchematics(): List<Schematic> {
        return schematicManager.data
    }

    override fun getZones(): List<Zone> {
        return zoneManager.data
    }

    override fun getLocations(): List<NamedLocation> {
        return locationManager.data
    }
}