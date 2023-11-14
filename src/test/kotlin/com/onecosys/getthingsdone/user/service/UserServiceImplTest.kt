package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.PasswordMismatchException
import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.model.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.model.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
internal class UserServiceImplTest {

    @RelaxedMockK
    private lateinit var mockPasswordEncoder: PasswordEncoder

    @RelaxedMockK
    private lateinit var mockRepository: UserRepository

    @RelaxedMockK
    private lateinit var mockMapper: UserInfoMapper

    private val userInfoUpdateRequest = UserInfoUpdateRequest(
        email = "newemail@example.com",
        firstName = "Ahmad",
        lastName = "Hasan",
        username = "ahmad_hasan"
    )
    private val mockUserInfoResponse: UserInfoResponse = mockk()
    private val user = User(email = "newemail@example.com", _password = "test")

    private lateinit var objectUnderTest: UserService
    private lateinit var principal: Authentication

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        principal = UsernamePasswordAuthenticationToken(user, null)
        objectUnderTest = UserServiceImpl(mockPasswordEncoder, mockRepository, mockMapper)
    }

    @Test
    fun `when change user email gets triggered then expect success response`() {
        every { mockRepository.findByEmail(userInfoUpdateRequest.email!!) } returns null
        every { mockRepository.save(any()) } returns user
        every { mockMapper.toDto(user) } returns mockUserInfoResponse

        val result = objectUnderTest.changeEmail(userInfoUpdateRequest, principal)

        assertNotNull(result)
        assertEquals(mockUserInfoResponse, result)
        verify(exactly = 1) { mockRepository.save(any()) }
        verify(exactly = 1) { mockMapper.toDto(user) }
    }

    @Test
    fun `when change user email gets triggered then expect invalid email exception`() {
        val request = UserInfoUpdateRequest(
            email = "invalidemail.com",
            firstName = "Ahmad",
            lastName = "Hasan",
            username = "ahmad_hasan"
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(request, principal) }

        assertEquals("Email does not contain @ symbol", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }

    @Test
    fun `when change user email gets triggered then expect email is taken by another user exception`() {
        val request = UserInfoUpdateRequest(
            email = "test@email.com",
            firstName = null,
            lastName = null,
            username = null
        )
        val exceptionMessage = "Email is already used by another user"
        every { mockRepository.findByEmail(request.email!!) } returns user

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(request, principal) }

        assertEquals(exceptionMessage, exception.message)
        verify(exactly = 1) { mockRepository.findByEmail(request.email!!) }
    }

    @Test
    fun `when change user email gets triggered then expect email can't be blank exception`() {
        val request = UserInfoUpdateRequest(
            email = null,
            firstName = "Ahmad",
            lastName = "Hasan",
            username = "ahmad_hasan"
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(request, principal) }

        assertEquals("Email can't be blank/null !", exception.message)
    }

    @Test
    fun `when change username gets triggered then expect invalid username exception`() {
        val request = UserInfoUpdateRequest(
            email = "valid@email.com",
            firstName = "Ahmad",
            lastName = "Hasan",
            username = "ahmad@hasan"
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeUsername(request, principal) }

        assertEquals("Username is not an email it can't contain @ symbol", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }

    @Test
    fun `when change username gets triggered then expect username already taken exception`() {
        val request = UserInfoUpdateRequest(
            email = "valid@email.com",
            firstName = "Ahmad",
            lastName = "Hasan",
            username = "ahmad-hasan"
        )
        every { mockRepository.findBy_username(request.username!!) } returns user

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeUsername(request, principal) }
        assertEquals("Username is already used by another user", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }

    @Test
    fun `when change username gets triggered then expect username changed success response`() {
        every { mockRepository.findBy_username(userInfoUpdateRequest.username!!) } returns null
        every { mockRepository.save(any()) } returns user
        every { mockMapper.toDto(user) } returns mockUserInfoResponse

        val response = objectUnderTest.changeUsername(userInfoUpdateRequest, principal)

        assertNotNull(response)
        assertEquals(mockUserInfoResponse, response)
        verify(exactly = 1) { mockRepository.save(any()) }
        verify(exactly = 1) { mockMapper.toDto(user) }
    }

    @Test
    fun `when change username gets triggered then expect username can't be blank exception`() {
        val request = UserInfoUpdateRequest(
            email = "ahmad.hasan@test.com",
            firstName = "Ahmad",
            lastName = "Hasan",
            username = null
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeUsername(request, principal) }

        assertEquals("Username can't be blank/null !", exception.message)
    }

    @Test
    fun `when change user password gets triggered then expect current password wrong exception`() {
        val request = UserPasswordUpdateRequest("hello", "hello", "hello")
        user._password = "test"

        val exception = assertThrows<PasswordMismatchException> { objectUnderTest.changePassword(request, principal) }

        assertEquals("The current password is wrong!", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }

    @Test
    fun `when change user password gets triggered then expect password confirmation does not match exception`() {
        val request = UserPasswordUpdateRequest("test", "hello", "hey")
        every { mockPasswordEncoder.matches(any(), any()) } returns true

        val exception = assertThrows<PasswordMismatchException> { objectUnderTest.changePassword(request, principal) }

        assertEquals("Your new password does not match with the password confirmation!", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }

    @Test
    fun `when change user password gets triggered then expect password change success response`() {
        val request = UserPasswordUpdateRequest("test", "hello", "hello")
        every { mockPasswordEncoder.matches(any(), any()) } returns true
        every { mockRepository.save(any()) } returns user

        objectUnderTest.changePassword(request, principal)

        assertEquals("hello", request.newPassword)
        verify(exactly = 1) { mockRepository.save(user) }
    }
}