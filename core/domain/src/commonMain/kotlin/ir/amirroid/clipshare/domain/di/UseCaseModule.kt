package ir.amirroid.clipshare.domain.di

import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardItemUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.GetClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetClipboardContentUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetOneFileClipboardUseCase
import ir.amirroid.clipshare.domain.usecase.device.AcceptPendingConnectionUseCase
import ir.amirroid.clipshare.domain.usecase.device.AddDeviceToConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetIsStartedBroadcastingUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetNearByDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.GetPendingConnectionsUseCase
import ir.amirroid.clipshare.domain.usecase.device.RejectPendingConnectionUseCase
import ir.amirroid.clipshare.domain.usecase.device.RemoveDeviceFromConnectedDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StartDiscoveringDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopBroadcastingDevicesUseCase
import ir.amirroid.clipshare.domain.usecase.device.StopDiscoveringDevicesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetClipboardHistoryUseCase)
    factoryOf(::DeleteClipboardHistoryUseCase)
    factoryOf(::DeleteClipboardItemUseCase)
    factoryOf(::SetClipboardContentUseCase)
    factoryOf(::SetOneFileClipboardUseCase)
    factoryOf(::GetNearByDevicesUseCase)
    factoryOf(::StopBroadcastingDevicesUseCase)
    factoryOf(::StartBroadcastingDevicesUseCase)
    factoryOf(::StartDiscoveringDevicesUseCase)
    factoryOf(::StopDiscoveringDevicesUseCase)
    factoryOf(::GetIsStartedBroadcastingUseCase)
    factoryOf(::GetConnectedDevicesUseCase)
    factoryOf(::AddDeviceToConnectedDevicesUseCase)
    factoryOf(::RemoveDeviceFromConnectedDevicesUseCase)
    factoryOf(::RejectPendingConnectionUseCase)
    factoryOf(::GetPendingConnectionsUseCase)
    factoryOf(::AcceptPendingConnectionUseCase)
}