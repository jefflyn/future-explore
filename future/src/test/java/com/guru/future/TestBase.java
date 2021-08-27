package com.guru.future;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(classes = FutureApplication.class)
@WebAppConfiguration
public class TestBase extends AbstractTestNGSpringContextTests {
    public TestBase() {
    }
}
