package com.kuvyrkom.chartographer.usecase.application;

import com.kuvyrkom.chartographer.infrastructure.util.ChartaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

@SpringBootTest(classes = ChartographerApplication.class, args = {"target/sdfgsdfsd/"})
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class BaseTest {
    @BeforeEach
    void setAUp() {
        ChartaUtil.createTmpChartaDirectory(new String[]{"src/test/resources/chartaImage"});
    }

    @AfterEach
    void tearDown() {
        File directory = new File("src/test/resources/chartaImage");
        recursiveDelete(directory);
    }

    private static void recursiveDelete(File directory) {
        if (!directory.exists())
            return;
        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                recursiveDelete(f);
            }
        }
        directory.delete();
    }
}
