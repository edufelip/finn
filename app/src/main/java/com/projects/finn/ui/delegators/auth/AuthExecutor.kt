package com.projects.finn.ui.delegators.auth

import javax.inject.Inject

class AuthExecutor @Inject constructor(
    googleAuthExecutor: GoogleAuthExecutor,
    generalAuthExecutor: GeneralAuthExecutor
) {

    private val authProviders = listOf(googleAuthExecutor, generalAuthExecutor)

    suspend fun logout(clientsSet: HashSet<AuthTypes>? = null) {
        clientsSet?.let {
            executeFiltering(clientsSet) { client ->
                client.signOut()
            }
        } ?: kotlin.run {
            authProviders.forEach {
                it.signOut()
            }
        }
    }

    private inline fun executeFiltering(
        clientsSet: HashSet<AuthTypes>,
        function: (IAuthExecutor) -> Unit
    ) {
        authProviders.forEach {
            if (clientsSet.contains(it.type)) {
                function.invoke(it)
            }
        }
    }
}