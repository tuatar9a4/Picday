package com.devd.intro.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.noRippleClickable


@Preview
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    onMakeDiaryClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(48.dp),
                painter = rememberVectorPainter(Icons.Rounded.Build),
                contentDescription = "로고 자리"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.intro_title),
                style = OneDayTypography.titleMedium.copy(
                    color = TextDefaultColor
                )
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.intro_description),
                style = OneDayTypography.bodyLarge.copy(
                    color = TextDefaultColor
                )
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = OneDayOneShotTheme.color.tertiary,
                ),
                onClick = onMakeDiaryClick
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = stringResource(R.string.make_diary_text),
                    style = OneDayTypography.bodyLarge.copy(
                        color = WhiteColor
                    )
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.noRippleClickable(
                    onClick = onLoginClick
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_diary_text),
                    style = OneDayTypography.labelMedium.copy(
                        color = TextDefaultColor
                    )

                )
                Text(
                    text = stringResource(R.string.login_text),
                    style = OneDayTypography.labelLarge.copy(
                        color = AccentColor
                    )
                )

            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}