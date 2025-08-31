package ir.amirroid.clipshare.domain.di

import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.DeleteClipboardItemUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.GetClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetClipboardContentUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetOneFileClipboardUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetClipboardHistoryUseCase)
    factoryOf(::DeleteClipboardHistoryUseCase)
    factoryOf(::DeleteClipboardItemUseCase)
    factoryOf(::SetClipboardContentUseCase)
    factoryOf(::SetOneFileClipboardUseCase)
}