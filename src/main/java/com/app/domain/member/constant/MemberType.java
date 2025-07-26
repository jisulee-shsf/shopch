package com.app.domain.member.constant;

public enum MemberType {

    KAKAO,
    GOOGLE;

    public static MemberType from(String memberType) {
        return MemberType.valueOf(memberType.toUpperCase());
    }
}
