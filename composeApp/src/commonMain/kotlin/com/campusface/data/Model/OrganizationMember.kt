package com.campusface.data.Model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class OrganizationMember @OptIn(ExperimentalTime::class) constructor(
    val id: String = "",
    val organizationId: String = "",
    val userId: String = "",
    val role: Role = Role.MEMBER,
    val status: MemberStatus = MemberStatus.ACTIVE,
    val faceImageId: String? = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

enum class Role {
    MEMBER,
    VALIDATOR,
    ADMIN
}

enum class MemberStatus {
    PENDING,
    ACTIVE,
    INACTIVE,
}