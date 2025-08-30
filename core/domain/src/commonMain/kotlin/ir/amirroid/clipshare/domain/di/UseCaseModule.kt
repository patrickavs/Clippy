package ir.amirroid.clipshare.domain.di

import ir.amirroid.clipshare.domain.usecase.clipboard.GetClipboardHistoryUseCase
import ir.amirroid.clipshare.domain.usecase.clipboard.SetClipboardContentUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetClipboardHistoryUseCase)
    factoryOf(::SetClipboardContentUseCase)
}