package ua.chnu.kpp.recipes

import ua.chnu.kpp.recipes.ui.login.LoginValidator
import ua.chnu.kpp.recipes.ui.login.PasswordError
import ua.chnu.kpp.recipes.ui.login.UsernameError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoginValidatorTest {

    @Test
    fun usernameRules() {
        assertEquals(UsernameError.EMPTY, LoginValidator.validateUsername("   "))
        assertEquals(UsernameError.TOO_SHORT, LoginValidator.validateUsername("em"))
        assertEquals(UsernameError.INVALID_CHARS, LoginValidator.validateUsername("емілі"))
        assertNull(LoginValidator.validateUsername("emilys"))
        assertNull(LoginValidator.validateUsername(" john.doe_1 "))
    }

    @Test
    fun passwordRules() {
        assertEquals(PasswordError.EMPTY, LoginValidator.validatePassword(""))
        assertEquals(PasswordError.TOO_SHORT, LoginValidator.validatePassword("12345"))
        assertNull(LoginValidator.validatePassword("emilyspass"))
    }
}
