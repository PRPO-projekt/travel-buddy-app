package si.travelbuddy.travelbuddy.ui.user

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import si.travelbuddy.travelbuddy.model.User

enum class LoginState {
    NotLoggedIn, Failed, Success
}

data class UserUiState(
    val user: User? = null,
    val loginState: LoginState = LoginState.NotLoggedIn
)

class UserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun updateUser(user: User?) {
        _uiState.update { currentState ->
            currentState.copy(
                user = user,
                loginState = LoginState.Success
            )
        }
    }

    fun updateLoginState(state: LoginState) {
        _uiState.update { currentState ->
            currentState.copy(
                loginState = state
            )
        }
    }
}