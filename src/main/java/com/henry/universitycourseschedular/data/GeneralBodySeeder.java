package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.models.core.GeneralBody;
import com.henry.universitycourseschedular.repositories.GeneralBodyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @Slf4j
@RequiredArgsConstructor
public class GeneralBodySeeder {
    private final GeneralBodyRepository generalBodyRepository;

    public void seed() {
        if (generalBodyRepository.count() == 0) {
            List<GeneralBody> bodies = List.of(
                    new GeneralBody(null, "CEDS", "Center for Entrepreneurial Development Studies"),
                    new GeneralBody(null, "ALDC", "African Leadership Development Centre")
            );
            generalBodyRepository.saveAll(bodies);
            log.info("✅ Seeded General Bodies");
        }
    }
}
