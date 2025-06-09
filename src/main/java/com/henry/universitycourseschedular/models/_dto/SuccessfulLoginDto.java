package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.Role;

public record SuccessfulLoginDto(
        String userId,
        String fullName,
        Role role,
        String email,
        Boolean loginVerified,
        String accessToken,
        String refreshToken,
        String tokenExpirationDuration,
        OneTimePasswordDto oneTimePassword
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String fullName;
        private Role role;
        private String email;
        private Boolean loginVerified;
        private String accessToken;
        private String refreshToken;
        private String tokenExpirationDuration;
        private OneTimePasswordDto oneTimePassword;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder loginVerified(Boolean loginVerified) {
            this.loginVerified = loginVerified;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenExpirationDuration(String tokenExpirationDuration) {
            this.tokenExpirationDuration = tokenExpirationDuration;
            return this;
        }

        public Builder oneTimePassword(OneTimePasswordDto oneTimePassword) {
            this.oneTimePassword = oneTimePassword;
            return this;
        }

        public SuccessfulLoginDto build() {
            return new SuccessfulLoginDto(
                    userId,
                    fullName,
                    role,
                    email,
                    loginVerified,
                    accessToken,
                    refreshToken,
                    tokenExpirationDuration,
                    oneTimePassword
            );
        }
    }
}
