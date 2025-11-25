package com.ecommerce.ecommerce.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
  @Value("${cookie-domain}")
  private static String cookieDomain;


  public static ResponseCookie addRefreshTokenToCookie (String token){
    return ResponseCookie.from("refreshToken",token)
            .path("/auth/refresh")
            .maxAge(JwtUtil.refreshExpirationMs)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .domain(cookieDomain)
            .build();
  }
  public static ResponseCookie addAccessTokenToCookie (String token){
    return ResponseCookie.from("accessToken",token)
            .path("/")
            .maxAge(JwtUtil.jwtExpirationMs)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .domain(cookieDomain)
            .build();
  }

  public static ResponseCookie deleteRefreshToken(String token){
    return ResponseCookie.from("refreshToken",token)
            .path("/auth/refresh")
            .maxAge(0)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .domain(cookieDomain)
            .build();
  }
  public static ResponseCookie deleteAccessToken(String token){
    return ResponseCookie.from("accessToken",token)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .domain(cookieDomain)
            .build();
  }
}
