package ua.chnu.kpp.recipes.ui.onboarding

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import ua.chnu.kpp.recipes.resources.Res
import ua.chnu.kpp.recipes.resources.onboarding_1_text
import ua.chnu.kpp.recipes.resources.onboarding_1_title
import ua.chnu.kpp.recipes.resources.onboarding_2_text
import ua.chnu.kpp.recipes.resources.onboarding_2_title
import ua.chnu.kpp.recipes.resources.onboarding_3_text
import ua.chnu.kpp.recipes.resources.onboarding_3_title
import ua.chnu.kpp.recipes.resources.onboarding_4_text
import ua.chnu.kpp.recipes.resources.onboarding_4_title
import ua.chnu.kpp.recipes.resources.onboarding_chef
import ua.chnu.kpp.recipes.resources.onboarding_favourites
import ua.chnu.kpp.recipes.resources.onboarding_recipes
import ua.chnu.kpp.recipes.resources.onboarding_steps

internal data class OnboardingPage(
    val illustration: DrawableResource,
    val title: StringResource,
    val text: StringResource,
)

internal val onboardingPages = listOf(
    OnboardingPage(Res.drawable.onboarding_recipes, Res.string.onboarding_1_title, Res.string.onboarding_1_text),
    OnboardingPage(Res.drawable.onboarding_steps, Res.string.onboarding_2_title, Res.string.onboarding_2_text),
    OnboardingPage(Res.drawable.onboarding_favourites, Res.string.onboarding_3_title, Res.string.onboarding_3_text),
    OnboardingPage(Res.drawable.onboarding_chef, Res.string.onboarding_4_title, Res.string.onboarding_4_text),
)
