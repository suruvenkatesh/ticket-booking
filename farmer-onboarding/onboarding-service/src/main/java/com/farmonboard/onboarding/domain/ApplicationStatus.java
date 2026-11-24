package com.farmonboard.onboarding.domain;

// The workflow statuses of a farmer application, in order.
// The numbers in the comments are the status codes the mobile app shows today.
public enum ApplicationStatus {
    DRAFT,              // 1: created, still being filled in
    SUBMITTED,          // 2: sent to the RM for approval
    RM_APPROVED,        // 3: approved by the RM
    MDM_APPROVED,       // 4: approved by the MDM approver
    AADHAAR_VERIFIED,   // 5: e-agreement Aadhaar verification done
    AGREEMENT_SIGNED,   // 6: farmer and company signed the e-agreement
    VENDOR_CREATED      // 7: vendor created, the workflow is finished
}
