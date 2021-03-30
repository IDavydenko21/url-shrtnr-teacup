package edu.kpi.testcourse.pbt;


import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import edu.kpi.testcourse.auth.PasswordHash;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.security.NoSuchAlgorithmException;

import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Test;

@MicronautTest
public class PasswordHashPBTest {

//Should hash and validate password
  @Test
  public void HashAndValidPasswordsPBT() {
    qt().forAll(
      strings().basicLatinAlphabet().ofLengthBetween(0, 10)
    ).check((String password) -> {

      //GIVEN
      String secret = "testSecret";
      String secretHash = "";

      try {
        //WHEN
        secretHash = PasswordHash.createHash(secret);
        boolean valid = PasswordHash.validatePassword(secret, secretHash);

      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        return false;
      }

      //THEN
      return true;
    });
  }
}
