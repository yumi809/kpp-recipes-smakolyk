package ua.chnu.kpp.recipes.ui.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import ua.chnu.kpp.recipes.ui.common.PaneLayout
import ua.chnu.kpp.recipes.ui.common.rememberPaneLayout
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.kpp.recipes.resources.Res
import ua.chnu.kpp.recipes.resources.onboarding_next
import ua.chnu.kpp.recipes.resources.onboarding_page_indicator
import ua.chnu.kpp.recipes.resources.onboarding_skip
import ua.chnu.kpp.recipes.resources.onboarding_start
import ua.chnu.kpp.recipes.ui.common.CollectEffects
import ua.chnu.kpp.recipes.ui.theme.SmakolykTheme

/** Stateful entry: wires the MVI view model to the stateless [OnboardingScreen]. */
@Composable
fun OnboardingRoute(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = state.currentPage, pageCount = { state.pageCount })

    // Pager -> intent: report the page the user settled on.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { viewModel.onIntent(OnboardingIntent.PageSettled(it)) }
    }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is OnboardingEffect.ScrollToPage -> pagerState.animateScrollToPage(effect.page)
            OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
        }
    }

    OnboardingScreen(
        state = state,
        pagerState = pagerState,
        layout = rememberPaneLayout(),
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun OnboardingScreen(
    state: OnboardingState,
    pagerState: PagerState,
    layout: PaneLayout,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (layout) {
        PaneLayout.SINGLE -> SinglePaneOnboarding(state, pagerState, onIntent, modifier)
        PaneLayout.TWO -> TwoPaneOnboarding(state, pagerState, onIntent, modifier)
    }
}

/** Wide window: the swipeable illustration on the left, text and controls on the right. */
@Composable
private fun TwoPaneOnboarding(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
        ) { index ->
            Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(onboardingPages[index].illustration),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxWidth = 360.dp, maxHeight = 360.dp).fillMaxWidth(0.8f),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 40.dp, vertical = 24.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(48.dp)) {
                if (!state.isLastPage) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = { onIntent(OnboardingIntent.SkipClicked) },
                    ) { Text(stringResource(Res.string.onboarding_skip)) }
                }
            }
            Crossfade(
                targetState = onboardingPages[state.currentPage],
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) { page ->
                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(page.title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(page.text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            PagerIndicator(currentPage = state.currentPage, pageCount = state.pageCount)
            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.widthIn(min = 220.dp).height(56.dp),
                onClick = { onIntent(OnboardingIntent.NextClicked) },
            ) {
                Text(
                    text = stringResource(if (state.isLastPage) Res.string.onboarding_start else Res.string.onboarding_next),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

/** Compact window (phone portrait): one column. */
@Composable
private fun SinglePaneOnboarding(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            if (!state.isLastPage) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onIntent(OnboardingIntent.SkipClicked) },
                ) {
                    Text(stringResource(Res.string.onboarding_skip))
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { index ->
            OnboardingPageContent(page = onboardingPages[index])
        }

        PagerIndicator(
            currentPage = state.currentPage,
            pageCount = state.pageCount,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { onIntent(OnboardingIntent.NextClicked) },
        ) {
            Text(
                text = stringResource(if (state.isLastPage) Res.string.onboarding_start else Res.string.onboarding_next),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
internal fun OnboardingPageContent(page: OnboardingPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(page.illustration),
            contentDescription = null,
            modifier = Modifier.sizeIn(maxWidth = 260.dp, maxHeight = 260.dp).fillMaxWidth(0.75f),
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(page.text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun PagerIndicator(currentPage: Int, pageCount: Int, modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.onboarding_page_indicator, currentPage + 1, pageCount)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = currentPage == index
            val width by animateDpAsState(if (selected) 28.dp else 10.dp)
            val color by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
            Box(
                modifier = Modifier
                    .size(height = 10.dp, width = width)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    SmakolykTheme {
        OnboardingScreen(
            state = OnboardingState(),
            pagerState = rememberPagerState(pageCount = { onboardingPages.size }),
            layout = PaneLayout.SINGLE,
            onIntent = {},
        )
    }
}
