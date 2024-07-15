package com.ahmedzenhom.mytasks.data.model

import java.io.Serializable

data class AccountModel(
    val id: String? = null,
    val name: String? = null,
    val phone: String? = null,
) : Serializable
