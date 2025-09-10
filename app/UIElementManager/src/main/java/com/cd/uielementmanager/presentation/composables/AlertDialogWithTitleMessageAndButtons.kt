package com.cd.uielementmanager.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.cd.uielementmanager.R
import com.cd.uielementmanager.presentation.beans.ButtonHandlerBean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlertDialogWithTitleMessageAndButtons(
    descriptionText: String,
    titleText: String? = null,
    titleTextStyle: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    descriptionTextStyle: TextStyle = TextStyle(fontSize = 13.sp),
    positiveButton: ButtonHandlerBean? = null,
    negativeButton: ButtonHandlerBean? = null,
    dialogProperties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
    onDialogDismiss: (() -> Unit)? = null
) {
    BasicAlertDialog(
        onDismissRequest = { onDialogDismiss?.invoke() },
        properties = dialogProperties
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (titleText != null) {
                Text(text = titleText, style = titleTextStyle)
                SpacerHeight8()
            }
            Text(text = descriptionText, style = descriptionTextStyle)
            if (positiveButton != null || negativeButton != null) {
                SpacerHeight24()
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    if (negativeButton != null) {
                        TextButton(
                            onClick = { negativeButton.onButtonClicked() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                        ) {
                            Text(text = negativeButton.buttonText, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (positiveButton != null && negativeButton != null) {
                        SpacerWidth4()
                    }
                    if (positiveButton != null) {
                        TextButton(onClick = { positiveButton.onButtonClicked() }) {
                            Text(text = positiveButton.buttonText, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ErrorAlertDialog(
    errorMessage: String,
    errorTitle: String = stringResource(R.string.error),
    dialogProperties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
    positiveButton: ButtonHandlerBean? = null,
    negativeButton: ButtonHandlerBean? = null,
    onDismissRequest: (() -> Unit)? = null
) {
    AlertDialogWithTitleMessageAndButtons(
        descriptionText = errorMessage,
        titleText = errorTitle,
        titleTextStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error
        ),
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        dialogProperties = dialogProperties,
        onDialogDismiss = onDismissRequest
    )
}