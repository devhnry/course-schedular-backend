package com.henry.universitycourseschedular.models._dto;

import java.time.ZonedDateTime;

public record SuccessfulInviteDto(
        String email,
        String inviteToken,
        boolean inviteVerified,
        ZonedDateTime inviteDate,
        ZonedDateTime expirationDate
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String inviteToken;
        private boolean inviteVerified;
        private ZonedDateTime inviteDate;
        private ZonedDateTime expirationDate;

        public SuccessfulInviteDto.Builder email(String email) {
            this.email = email;
            return this;
        }

        public SuccessfulInviteDto.Builder inviteToken(String inviteToken) {
            this.inviteToken = inviteToken;
            return this;
        }

        public SuccessfulInviteDto.Builder inviteVerified(Boolean inviteVerified) {
            this.inviteVerified = inviteVerified;
            return this;
        }

        public SuccessfulInviteDto.Builder inviteDate(ZonedDateTime inviteDate) {
            this.email = email;
            return this;
        }

        public SuccessfulInviteDto.Builder expirationDate(ZonedDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public SuccessfulInviteDto build() {
            return new SuccessfulInviteDto(
                    email,
                    inviteToken,
                    inviteVerified,
                    inviteDate,
                    expirationDate
            );
        }
    }
}
