package com.whyaji.warehousestockapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextInput(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordTf: Boolean = false,
    isEmailTf: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    visibility: MutableState<Boolean> = rememberSaveable { mutableStateOf(true) },
    nextFocusRequester: FocusRequester? = null,
    isLast: Boolean = false,
    buttonEnabled: Boolean = true,
    errorMessage: String = "",
    performAction: () -> Unit = {}
) {
    val imeAction = if (isLast) ImeAction.Done else ImeAction.Next
    val keyboardActions = KeyboardActions(
        onNext = {
            nextFocusRequester?.requestFocus()
        },
        onDone = { if (buttonEnabled) performAction.invoke() }
    )
    val keyboardOptions = KeyboardOptions(
        keyboardType = if (isPasswordTf) KeyboardType.Password else if (isEmailTf) KeyboardType.Email else KeyboardType.Text,
        imeAction = imeAction
    )

    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            singleLine = true,
            visualTransformation = if (isPasswordTf && !visibility.value) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(8.dp),
            leadingIcon = leadingIcon,
            isError = errorMessage.isNotEmpty(),
            trailingIcon = if (!isPasswordTf) null else {
                @Composable {
                    val image = if (visibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Localized description for accessibility services
                    val description = if (visibility.value) "Hide password" else "Show password"

                    // Toggle button to hide or display password
                    IconButton(onClick = {visibility.value = !visibility.value}){
                        Icon(imageVector  = image, description)
                    }
                }
            },
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}