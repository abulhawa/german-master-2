package com.germanverbmaster.android.learner

import android.net.Uri

fun learnerOAuthRedirect(packageName: String) = "$packageName.auth://callback"
fun isLearnerOAuthCallback(uri: Uri, packageName: String): Boolean =
    uri.scheme=="$packageName.auth" && uri.host=="callback" && uri.port == -1 &&
        uri.userInfo==null && uri.path.orEmpty().isEmpty() && uri.fragment==null

/** Accept only a PKCE code; never import access tokens from an external intent. */
fun learnerOAuthCode(uri: Uri, packageName: String): String {
    require(isLearnerOAuthCallback(uri,packageName))
    require(uri.getQueryParameter("error")==null)
    require(uri.getQueryParameters("code").size==1)
    return requireNotNull(uri.getQueryParameter("code")).also {
        require(it.isNotBlank() && it.length<=4096)
    }
}
