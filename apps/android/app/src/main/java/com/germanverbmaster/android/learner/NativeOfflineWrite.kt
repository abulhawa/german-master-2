package com.germanverbmaster.android.learner

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.Serializable

/** Frozen ordered writes; receipt delivery never replaces provisional question feedback. */
@Serializable
data class NativeOfflineWrite(
    val attempt: Attempt? = null, val provisional: Evaluation? = null, val attemptReceipt: Acknowledgment? = null,
    val exposure: ExposureEvent? = null, val exposureReceipt: ExposureAcknowledgment? = null,
    val completion: SessionCompletionRequest? = null, val completionReceipt: SessionCompletionReceipt? = null
) {
    init {
        require(listOf(attempt, exposure, completion).count { it != null } == 1)
        require(attemptReceipt == null || attemptReceipt.attemptId == attempt?.attemptId)
        require(exposureReceipt == null || exposureReceipt.eventId == exposure?.eventId)
        require(completionReceipt == null || completionReceipt.requestId == completion?.requestId)
    }
    val delivered get() = attemptReceipt is AttemptAcknowledgment || attemptReceipt is AttemptDuplicate ||
        exposureReceipt is ExposureAccepted || exposureReceipt is ExposureDuplicate || completionReceipt != null
}
