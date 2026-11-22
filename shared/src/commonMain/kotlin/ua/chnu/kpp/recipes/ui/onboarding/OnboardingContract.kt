package ua.chnu.kpp.recipes.ui.onboarding

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingState(
    val currentPage: Int = 0,
    val pageCount: Int = onboardingPages.size,
) {
    val isLastPage: Boolean get() = currentPage == pageCount - 1
}

sealed interface OnboardingIntent {
    /** The pager settled on [page] after a swipe or an animated scroll. */
    data class PageSettled(val page: Int) : OnboardingIntent
    data object NextClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
}

sealed interface OnboardingEffect {
    data class ScrollToPage(val page: Int) : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
}
