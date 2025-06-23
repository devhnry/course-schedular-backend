package com.henry.universitycourseschedular.enums;

public enum InviteStatus {
    PENDING,    // invited & not yet used & not expired
    ACCEPTED,   // user exists & accountVerified=true
    EXPIRED     // invite expired or marked used without signup
}
