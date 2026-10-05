package com.germanverbmaster.android.learner

/** Loopback fixture transport is excluded from release sources. */
class LocalLearnerApi(port: Int = 5001, expectedSubject: String? = null) : LearnerApi by HttpLearnerApi(
    "http://127.0.0.1:$port", expectedSubject, { "foundation-local-demo" }, {}, {}
) { init { require(port in 1..65535) } }
