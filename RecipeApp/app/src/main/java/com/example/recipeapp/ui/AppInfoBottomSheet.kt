package com.example.recipeapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.recipeapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(
    onDismiss: () -> Unit,
    onShow: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { false }
    )

    BackHandler(enabled = true) {
        // Do nothing to prevent user from closing the screen with back button
    }

    LaunchedEffect(Unit) {
        onShow()
    }

    ModalBottomSheet(
        onDismissRequest = { /* Ignore clicks outside due to confirmValueChange */ },
        sheetState = sheetState,
        dragHandle = null,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_info_screen))
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.info_title),
                fontSize = dimensionResource(id = R.dimen.text_size_info_title).value.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_info_items)))
            
            Text(
                text = stringResource(id = R.string.info_description),
                fontSize = dimensionResource(id = R.dimen.text_size_info_body).value.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_info_button)))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.info_button_text))
            }
            
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_info_items)))
        }
    }
}
