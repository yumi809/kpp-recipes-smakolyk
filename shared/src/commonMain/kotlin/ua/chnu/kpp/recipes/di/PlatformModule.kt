package ua.chnu.kpp.recipes.di

import org.koin.core.module.Module

/** Platform-specific bindings (KSafe needs an Android Context, a directory on desktop...). */
expect val platformModule: Module
