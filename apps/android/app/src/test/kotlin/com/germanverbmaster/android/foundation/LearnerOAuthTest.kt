package com.germanverbmaster.android.foundation

import android.net.Uri
import com.germanverbmaster.android.learner.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LearnerOAuthTest {
    private val app="com.example.learner"
    @Test fun acceptsOnlyCodeForThisApplication() {
        assertEquals("abc",learnerOAuthCode(Uri.parse("${learnerOAuthRedirect(app)}?code=abc"),app))
        listOf("other.auth://callback?code=abc", "${learnerOAuthRedirect(app)}?code=a&code=b",
            "${learnerOAuthRedirect(app)}?error=access_denied", "${learnerOAuthRedirect(app)}#access_token=abc",
            "${learnerOAuthRedirect(app)}/wrong?code=abc").forEach {
            assertTrue(runCatching {learnerOAuthCode(Uri.parse(it),app)}.isFailure)
        }
    }
}
