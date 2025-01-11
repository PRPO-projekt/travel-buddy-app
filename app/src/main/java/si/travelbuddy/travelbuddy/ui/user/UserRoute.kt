package si.travelbuddy.travelbuddy.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.User
import si.travelbuddy.travelbuddy.ui.theme.TravelBuddyTheme

@Composable
fun LoginForm(
    onLogin: (String, String) -> Unit
) {
    var userFieldValue by remember { mutableStateOf("") }
    var passwordFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = userFieldValue,
            onValueChange = { userFieldValue = it },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = passwordFieldValue,
            onValueChange = { passwordFieldValue = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(
            onClick = { onLogin(userFieldValue, passwordFieldValue) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Login",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    TravelBuddyTheme {
        LoginForm { s1, s2 -> }
    }
}

@Composable
fun UserView(
    user: User
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.height(128.dp).fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Person,
                "User icon",
                modifier = Modifier.size(128.dp)
            )
            Text(
                "${user.name} ${user.surname}",
                modifier = Modifier.fillMaxWidth(),
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserViewPreview() {
    TravelBuddyTheme {
        UserView(
            User(
                id = 0,
                name = "Walter",
                surname = "White",
                username = "walter_white",
                passwordHash = "",
                passwordSalt = "",
                created = ""
            )
        )
    }
}

@Composable
fun UserRoute(
    onLogin: suspend (String, String) -> User?,
    viewModel: UserViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val user = uiState.user
    if (user == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            LoginForm { username, password ->
                coroutineScope.launch {
                    val user = onLogin(username, password)

                    viewModel.updateUser(user)

                    val state = if (user == null) {
                        LoginState.Failed
                    }
                    else {
                        LoginState.Success
                    }

                    viewModel.updateLoginState(state)
                }
            }
        }
    }
    else {
        UserView(user)
    }
}