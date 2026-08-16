@file:Suppress("DEPRECATION")

package com.carenest.provider.auth.presentation.auth.login.google

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import android.util.Log

data class GoogleAccountPayload(
    val idToken: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
)

class GoogleSignInHelper(private val context: Context) {

    private val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GoogleAuthConstants.WEB_CLIENT_ID)
            .requestEmail()
            .build()

    val client: GoogleSignInClient
        get() = GoogleSignIn.getClient(context, googleSignInOptions)

    fun signOut(onComplete: () -> Unit = {}) {
        client.signOut().addOnCompleteListener {
            onComplete()
        }
    }

    fun parseGoogleAccount(data: Intent?): Result<GoogleAccountPayload> {
        println("GoogleAuthSDK: parseGoogleAccount called with intent=$data")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrBlank()) {
                val payload = GoogleAccountPayload(
                    idToken = idToken,
                    firstName = account.givenName,
                    lastName = account.familyName,
                    email = account.email,
                    profileImageUrl = account.photoUrl?.toString(),
                )
                println("GoogleAuthSDK: SUCCESS. Email=${payload.email}, firstName=${payload.firstName}, lastName=${payload.lastName}")
                Log.i(TAG, "Google Sign-In SDK SUCCESS: email=${payload.email}, firstName=${payload.firstName}, lastName=${payload.lastName}")
                Result.success(payload)
            } else {
                println("GoogleAuthSDK: ERROR. Null or empty ID token")
                Log.e(TAG, "Google Sign-In SDK returned null or empty ID token")
                Result.failure(IllegalStateException("Google Sign-In returned empty ID token"))
            }
        } catch (e: ApiException) {
            println("GoogleAuthSDK: FAILED with statusCode=${e.statusCode}: ${e.message}")
            Log.e(TAG, "Google Sign-In SDK FAILED with statusCode=${e.statusCode}: ${e.message}", e)
            val errorMessage = if (e.statusCode == 10) {
                val sha1 = "CD:83:2C:CE:3F:6E:50:77:B1:B8:BD:17:87:7A:18:D1:30:AF:4E:69"
                println("GoogleAuthSDK: StatusCode 10 = DEVELOPER_ERROR. SHA-1 [$sha1] must be added to Google Cloud Console for com.carenest.provider")
                Log.e(TAG, "StatusCode 10 = DEVELOPER_ERROR. SHA-1 [$sha1] must be added to Google Cloud Console for package com.carenest.provider")
                "Google Sign-In Developer Error (Code 10): SHA-1 fingerprint ($sha1) or package name (com.carenest.provider) not registered in Google Cloud Console."
            } else if (e.statusCode == 12501) {
                println("GoogleAuthSDK: StatusCode 12501 = SIGN_IN_CANCELLED by user.")
                "Google Sign-In cancelled"
            } else {
                e.message ?: "Google Sign-In failed (${e.statusCode})"
            }
            Result.failure(IllegalStateException(errorMessage, e))
        } catch (e: Throwable) {
            println("GoogleAuthSDK: Unexpected exception: ${e.message}")
            Log.e(TAG, "Unexpected exception during parseIdToken", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "GoogleAuthSDK"
    }
}
