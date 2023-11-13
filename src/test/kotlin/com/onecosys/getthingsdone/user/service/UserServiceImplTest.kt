package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
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
    private val user = User(email = "newemail@example.com")

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
    fun `when change user email gets triggered then expect invalid email response`() {
        val invalidEmail = "invalidemail.com"
        val request = UserInfoUpdateRequest(
            email = invalidEmail, firstName = "Ahmad",
            lastName = "Hasan",
            username = "ahmad_hasan"
        )


        val exception = assertThrows<BadRequestException> { objectUnderTest.changeEmail(request, principal) }
        assertEquals("Email does not contain @ symbol", exception.message)
        verify { mockRepository.save(user) wasNot called }
    }
}