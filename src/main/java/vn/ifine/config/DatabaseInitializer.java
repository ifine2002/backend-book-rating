package vn.ifine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.ifine.model.User;
import vn.ifine.repository.UserRepository;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Service
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    System.out.println(">>> START INIT DATABASE");
    long countUsers = this.userRepository.count();

    if (countUsers == 0) {
      User adminUser = new User();
      adminUser.setEmail("admin@gmail.com");
      adminUser.setAddress("hn");
      adminUser.setGender(GenderEnum.MALE);
      adminUser.setFullName("I'm super admin");
      adminUser.setStatus(UserStatus.ACTIVE);
      adminUser.setPassword(this.passwordEncoder.encode("123456"));

      this.userRepository.save(adminUser);
    }
    if (countUsers > 0) {
      System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
    }
    System.out.println(">>> END INIT DATABASE");
  }
}
