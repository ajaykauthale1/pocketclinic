package com.pocketclinic.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.OnPrimary
import com.pocketclinic.app.ui.theme.OutlineVariant
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DOB_FMT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .testTag("profile_screen"),
    ) {
        Text(
            text  = "PATIENT PROFILE",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )

        Spacer(Modifier.height(4.dp))

        if (state.isSetup && state.savedAt != null) {
            Text(
                text  = "LAST SYNC  ${DOB_FMT.format(Date(state.savedAt!!))}",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                modifier = Modifier.testTag("last_sync_label"),
            )
        }

        Spacer(Modifier.height(24.dp))

        // Name field
        ProfileTextField(
            label    = "PATIENT NAME",
            value    = state.name,
            onChange = viewModel::onNameChanged,
            tag      = "field_name",
            keyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )

        Spacer(Modifier.height(12.dp))

        // Gender field
        ProfileTextField(
            label    = "GENDER",
            value    = state.gender,
            onChange = viewModel::onGenderChanged,
            tag      = "field_gender",
        )

        Spacer(Modifier.height(12.dp))

        // DOB — display only; a real app would use a DatePickerDialog
        Text(
            text  = "DATE OF BIRTH",
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text     = if (state.dateOfBirth > 0L) DOB_FMT.format(Date(state.dateOfBirth))
                       else "NOT SET",
            style    = MaterialTheme.typography.bodyMedium,
            color    = Color.White,
            modifier = Modifier.testTag("dob_display"),
        )

        Spacer(Modifier.height(32.dp))

        // Save button
        Button(
            onClick  = viewModel::save,
            enabled  = !state.isSaving && state.name.isNotBlank(),
            colors   = ButtonDefaults.buttonColors(
                containerColor = PrimaryAmber,
                contentColor   = OnPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_save"),
        ) {
            Text(
                text  = if (state.isSaving) "SAVING..." else "[ ■ ] SAVE PROFILE",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    tag: String,
    keyboard: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine    = true,
        keyboardOptions = keyboard,
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryAmber,
            unfocusedBorderColor = OutlineVariant,
            focusedLabelColor    = PrimaryAmber,
            unfocusedLabelColor  = SecondaryText,
            focusedTextColor     = Color.White,
            unfocusedTextColor   = Color.White,
            cursorColor          = PrimaryAmber,
        ),
        modifier      = Modifier
            .fillMaxWidth()
            .testTag(tag),
    )
}
