package com.henry.universitycourseschedular.models._dto;

import java.time.LocalDateTime;

public record InviteSuccessRequestDto(
        String email,
        String inviteToken,
        boolean inviteVerified,
        LocalDateTime inviteDate,
        LocalDateTime expirationDate
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String inviteToken;
        private boolean inviteVerified;
        private LocalDateTime inviteDate;
        private LocalDateTime expirationDate;

        public InviteSuccessRequestDto.Builder email(String email) {
            this.email = email;
            return this;
        }

        public InviteSuccessRequestDto.Builder inviteToken(String inviteToken) {
            this.inviteToken = inviteToken;
            return this;
        }

        public InviteSuccessRequestDto.Builder inviteVerified(Boolean inviteVerified) {
            this.inviteVerified = inviteVerified;
            return this;
        }

        public InviteSuccessRequestDto.Builder inviteDate(LocalDateTime inviteDate) {
            this.inviteDate = inviteDate;
            return this;
        }

        public InviteSuccessRequestDto.Builder expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public InviteSuccessRequestDto build() {
            return new InviteSuccessRequestDto(
                    email,
                    inviteToken,
                    inviteVerified,
                    inviteDate,
                    expirationDate
            );
        }
    }
}
