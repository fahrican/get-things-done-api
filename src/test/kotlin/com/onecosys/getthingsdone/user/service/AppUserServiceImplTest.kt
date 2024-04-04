package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.authentication.service.ClientSessionService
import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.PasswordMismatchException
import com.onecosys.getthingsdone.user.entity.AppUser
import com.onecosys.getthingsdone.user.repository.AppUserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

internal class AppUserServiceImplTest {

    private val mockPasswordEncoder = mockk<PasswordEncoder>(relaxed = true)

    private val mockRepository = mockk<AppUserRepository>(relaxed = true)

    private val mockMapper = mockk<UserInfoMapper>(relaxed = true)

    private val mockAuthUserService = mockk<ClientSessionService>(relaxed = true)

    private val userInfoUpdateRequest = UserInfoUpdateRequest(firstName = "Ahmad", lastName = "Hasan")

    private val mockUserInfoResponse: UserInfoResponse = mockk()

    private val appUser =
        AppUser(email = "newemail@example.com", appPassword = "test", firstName = "Ali", lastName = "Muataz")

    private val request = HashMap<String, String>()

    private val objectUnderTest =
        AppUserServiceImpl(mockPasswordEncoder, mockRepository, mockMapper, mockAuthUserService)


    @Test
    fun `when change user email gets triggered then expect success response`() {
        val email = HashMap<String, String>()
        email["email"] = "info@test.com"
        every { mockAuthUserService.findCurrentSessionUser() } returns appUser
        every { mockRepository.findByEmail(email["email"]!!) } returns null
        every { mockRepository.save(any()) } returns appUser
        every { mockMapper.toDto(appUser) } returns mockUserInfoResponse

        val result = objectUnderTest.changeEmail(email)

        assertNotNull(result)
        assertEquals(mockUserInfoResponse, result)
        verify(exactly = 1) { mockRepository.save(any()) }
        verify(exactly = 1) { mockMapper.toDto(appUser) }
    }

    @Test
    fun `when change user email gets triggered then expect invalid email exception`() {
        val email = HashMap<String, String>()
        email["email"] = "invalidemail.com"

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(email) }

        assertEquals("Email must contain '@' symbol", exception.message)
        verify { mockRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when change user email gets triggered then expect email is taken by another user exception`() {
        val email = "test@email.com"
        request["email"] = email
        val exceptionMessage = "Email is already used by another user"
        every { mockAuthUserService.findCurrentSessionUser() } returns appUser
        every { mockRepository.findByEmail(email) } returns appUser

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(request) }

        assertEquals(exceptionMessage, exception.message)
        verify(exactly = 1) { mockRepository.findByEmail(email) }
    }

    @Test
    fun `when change username gets triggered then expect invalid username exception`() {
        request["username"] = "test@"

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeUsername(request) }

        assertEquals("Username cannot contain '@' symbol", exception.message)
        verify { mockRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when change username gets triggered then expect username already taken exception`() {
        request["username"] = "ahmad-hasan"

        every { mockRepository.findByAppUsername(request["username"]!!) } returns appUser

        val exception = assertThrows<BadRequestException> { objectUnderTest.changeUsername(request) }
        assertEquals("Username is already used by another user", exception.message)
        verify { mockRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when change username gets triggered then expect username changed success response`() {
        request["username"] = "ahmad-hasan"
        every { mockRepository.findByAppUsername(request["username"]!!) } returns null
        every { mockRepository.save(any()) } returns appUser
        every { mockMapper.toDto(appUser) } returns mockUserInfoResponse

        val response = objectUnderTest.changeUsername(request)

        assertNotNull(response)
        assertEquals(mockUserInfoResponse, response)
        verify(exactly = 1) { mockRepository.save(any()) }
        verify(exactly = 1) { mockMapper.toDto(appUser) }
    }

    @Test
    fun `when change user password gets triggered then expect current password wrong exception`() {
        val request = UserPasswordUpdateRequest("hello", "hello", "hello")
        appUser.appPassword = "test"

        val exception = assertThrows<PasswordMismatchException> { objectUnderTest.changePassword(request) }

        assertEquals("The current password is wrong!", exception.message)
        verify { mockRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when change user password gets triggered then expect password confirmation does not match exception`() {
        val request = UserPasswordUpdateRequest("test", "hello", "hey")
        every { mockPasswordEncoder.matches(any(), any()) } returns true

        val exception = assertThrows<PasswordMismatchException> { objectUnderTest.changePassword(request) }

        assertEquals("Your new password does not match with the password confirmation!", exception.message)
        verify { mockRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when change user password gets triggered then expect password change success response`() {
        val request = UserPasswordUpdateRequest("test", "hello", "hello")
        every { mockAuthUserService.findCurrentSessionUser() } returns appUser
        every { mockPasswordEncoder.matches(any(), any()) } returns true
        every { mockRepository.save(any()) } returns appUser

        objectUnderTest.changePassword(request)

        assertEquals("hello", request.newPassword)
        verify(exactly = 1) { mockRepository.save(appUser) }
    }

    @Test
    fun `when change user info gets triggered then expect success response`() {
        every { mockAuthUserService.findCurrentSessionUser() } returns appUser
        every { mockRepository.save(any()) } returns appUser
        every { mockMapper.toDto(appUser) } returns mockUserInfoResponse

        val response = objectUnderTest.changeInfo(userInfoUpdateRequest)

        assertEquals(mockUserInfoResponse, response)
        verify(exactly = 1) { mockRepository.save(appUser) }
    }

    @Test
    fun `when fetch user info gets triggered then expect success response`() {
        every { mockAuthUserService.findCurrentSessionUser() } returns appUser
        every { mockMapper.toDto(appUser) } returns mockUserInfoResponse

        val response = objectUnderTest.fetchInfo()

        assertEquals(mockUserInfoResponse, response)
    }
}