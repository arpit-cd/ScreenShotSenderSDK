package com.cd.uielementmanager.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier


/**
 * Provides a [UIElementViewModel] to its composable content and manages a potential training flow overlay.
 *
 * This composable sets the current screen name in the [UIElementViewModel], provides the view model
 * via [CompositionLocalProvider] to its children, and optionally displays a training flow overlay.
 *
 * @param screenName The name of the screen currently being displayed.  Used to set the screen context in the [UIElementViewModel].
 * @param uIElementViewModel The [UIElementViewModel] instance to provide to the composable content.
 * @param modifier Modifier to be applied to the outer [Box] containing the content and the training flow overlay.
 * @param enableTrainingFlow Whether to enable the training flow overlay. If true, [TrainingFlowOverlay] will be displayed on top of the content.
 * @param content The composable content to be rendered within the provided [UIElementViewModel] context.
 */
@Composable
fun UIElementProvider(
    screenName: String,
    uIElementViewModel: UIElementViewModel,
    modifier: Modifier = Modifier,
    enableTrainingFlow: Boolean = false,
    content: @Composable () -> Unit
) {
    uIElementViewModel.setCurrentScreen(screenName)
    CompositionLocalProvider(LocalUIElementViewModel provides uIElementViewModel) {
        Box(modifier = modifier) {
            content()
            if (enableTrainingFlow) {
                TrainingFlowOverlay(uIElementViewModel, modifier.fillMaxSize())
            }
        }
    }
}