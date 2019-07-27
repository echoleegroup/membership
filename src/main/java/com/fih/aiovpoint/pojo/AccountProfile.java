package com.fih.aiovpoint.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountProfile {

    private String id;

    private String account;

    @JsonProperty("display_name")
    private String displayName;

//    private String phone;
//
//    @JsonProperty("phone_verified")
//    private String phoneVerified;
//
//    private String gender;
//
//    private String birthday;
//
//    private String locale;
//
//    @JsonProperty("email_verified")
//    private String emailVerified;
//
//    @JsonProperty("agree_news")
//    private String agreeNews;
//
//    private String email;
//
//    @JsonProperty("photo_url")
//    private String photoUrl;
}
