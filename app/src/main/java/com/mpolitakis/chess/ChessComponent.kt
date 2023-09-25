package com.mpolitakis.chess

import dagger.Component
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
interface ChessModule {
    // Module methods
}

@Component(modules = [ChessModule::class])
@ViewModelScoped
interface ChessComponent {
    // Component methods

    @Component.Factory
    interface Factory {
        fun create(): ChessComponent
    }
}