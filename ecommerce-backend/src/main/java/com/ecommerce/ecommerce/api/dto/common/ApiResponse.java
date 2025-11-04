package com.ecommerce.ecommerce.api.dto.common;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiResponse<T> {
  private int status;
  private String message;
  private T data;
  private boolean success;
  private String path;
  private Map<String, String> validationErrors;
  private LocalDateTime timestamp;
  public static<T> ApiResponse<T> success(int status, String message,T data){
    return new ApiResponse<>(status,message,data,true,null,null,LocalDateTime.now());
  }
  public static<T> ApiResponse<T> error(int status, String message,String path, Map<String,String> validationErrors){
    return new ApiResponse<>(status,message,null,false,path,validationErrors,LocalDateTime.now());
  }
}
