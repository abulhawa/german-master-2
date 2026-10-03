package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.AttemptBatch
import com.germanverbmaster.android.foundation.contract.AttemptBatchResponse
import com.germanverbmaster.android.foundation.contract.Session
import com.germanverbmaster.android.foundation.contract.ContractShape
import kotlinx.serialization.json.Json

object ContractReader {
    val json = Json { ignoreUnknownKeys = false; isLenient = false; coerceInputValues = false }
    fun attempts(source: String): AttemptBatch {
        val element=json.parseToJsonElement(source)
        ContractShape.checkAttemptBatch(element)
        return json.decodeFromJsonElement(AttemptBatch.serializer(), element)
    }
    fun acknowledgments(source: String): AttemptBatchResponse {
        val element=json.parseToJsonElement(source)
        ContractShape.checkAttemptBatchResponse(element)
        return json.decodeFromJsonElement(AttemptBatchResponse.serializer(), element)
    }
    fun session(source: String): Session {
        val element=json.parseToJsonElement(source)
        ContractShape.checkSession(element)
        return json.decodeFromJsonElement(Session.serializer(), element)
    }
}
