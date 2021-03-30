package edu.kpi.testcourse.pbt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import edu.kpi.testcourse.dataservice.DataService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
public class UserDoesNotExist {

  @Inject
  DataService dataService;

  //Should return null when user doesn't exist
  @Test
  public void nullUserDoesNotExistsPB() {
    qt().
      forAll(
        strings().basicLatinAlphabet().ofLengthBetween(0,10)
      )

      // GIVEN
      .checkAssert((String user) -> {

        // WHEN
        assertThat(dataService.getUser(user)).isNull();
      });
  }

}
