package ir.amirroid.clipshare.domain.models.device

import ir.amirroid.clipshare.domain.models.utils.ConnectionStatus

data class ConnectedDevice(
    val connectionStatus: ConnectionStatus,
    val device: Device
)