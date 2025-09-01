package com.shopch.external.oauth.provider.kakao.dto.response;

import com.shopch.external.oauth.dto.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse implements OAuthUserInfo {

    private Long id;

    @Override
    public String getOauthId() {
        return String.valueOf(id);
    }

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getName() {
        return kakaoAccount.getProfile().getNickname();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getImageUrl() {
        return kakaoAccount.getProfile().getThumbnailImageUrl();
    }

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
