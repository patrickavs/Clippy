package ir.amirroid.clipshare.process.tracker

class ClipboardSyncTracker {
    private val map: MutableMap<String, MutableSet<String>> = mutableMapOf()

    fun addItemForDevice(uuid: String, deviceId: String) {
        map.getOrPut(deviceId) { mutableSetOf() }.add(uuid)
    }

    fun addItemForDevices(uuid: String, deviceIds: List<String>) {
        deviceIds.forEach { id ->
            map.getOrPut(id) { mutableSetOf() }.add(uuid)
        }
    }

    fun isSynced(uuid: String, deviceId: String): Boolean {
        return map[deviceId]?.contains(uuid) == true
    }

    fun getUnsyncedItems(allUuids: List<String>, deviceId: String): List<String> {
        val synced = map[deviceId] ?: emptySet()
        return allUuids.filter { it !in synced }
    }

    fun markAsSynced(uuids: List<String>, deviceId: String) {
        map.getOrPut(deviceId) { mutableSetOf() }.addAll(uuids)
    }

    fun removeItemsForDevice(uuids: List<String>, deviceId: String) {
        map[deviceId]?.removeAll(uuids)
    }

    fun getDevicesForUuid(uuid: String): List<String> {
        return map.filter { (_, uuids) -> uuid in uuids }.keys.toList()
    }

    fun clear() {
        map.clear()
    }
}