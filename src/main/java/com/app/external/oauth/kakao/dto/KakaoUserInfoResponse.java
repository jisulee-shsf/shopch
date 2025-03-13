package com.app.external.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Builder
    private KakaoUserInfoResponse(Long id, KakaoAccount kakaoAccount) {
        this.id = id;
        this.kakaoAccount = kakaoAccount;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        private String email;
        private Profile profile;

        @Builder
        private KakaoAccount(String email, Profile profile) {
            this.email = email;
            this.profile = profile;
        }

        @Getter
        @NoArgsConstructor
        public static class Profile {

            private String nickname;

            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;

            @Builder
            private Profile(String nickname, String thumbnailImageUrl) {
                this.nickname = nickname;
                this.thumbnailImageUrl = thumbnailImageUrl;
            }
        }
    }
}
