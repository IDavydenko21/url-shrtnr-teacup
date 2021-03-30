package edu.kpi.testcourse.urlservice;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.quicktheories.QuickTheory.qt;

import static org.quicktheories.generators.SourceDSL.integers;

@MicronautTest
public class ShortUrlTokenPBTest {

  @Inject
  UrlService urlService;


  //Should generate valid url token
  @Test
  void generateValidUrlTokenPB() {
    qt()
        .forAll(
            integers().allPositive()
        )

        // GIVEN
        .check(urlId -> {

          // WHEN
          String token = urlService.shortenUrlToken(urlId);

          // THEN
          return urlService.isUserAliasValid(token);
        });
  }

}
