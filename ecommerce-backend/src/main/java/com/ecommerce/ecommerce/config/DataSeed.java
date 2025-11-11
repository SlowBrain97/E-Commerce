package com.ecommerce.ecommerce.config;

import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeed implements CommandLineRunner {
  private final UserRepository userRepository;
  @Override
  public void run(String... args){
      if (userRepository.existsByUsername("admin")) return;
      userRepository.save(
            User.builder().id(1L).email("tranthang160897@gmail.com").username("admin")
                    .isActive(true).isVerified(true).firstName("Tran")
                    .lastName("Thang")
                    .role(User.Role.ADMIN)
                    .password(System.getenv("ADMIN_PASSWORD"))
                    .phoneNumber("07013765084")
                    .build());

  }
}
