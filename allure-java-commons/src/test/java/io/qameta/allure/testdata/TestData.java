package io.qameta.allure.testdata;

import io.qameta.allure.model3.Label;
import io.qameta.allure.model3.Link;
import io.qameta.allure.model3.StepResult;
import io.qameta.allure.model3.TestResult;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

/**
 * @author charlie (Dmitry Baev).
 */
public final class TestData {

    private TestData() {
        throw new IllegalStateException("Do not instance");
    }

    public static String randomString() {
        return randomAlphabetic(10);
    }

    public static Label randomLabel() {
        return new Label()
                .setName(randomString())
                .setValue(randomString());
    }

    public static TestResult randomTestResult() {
        return new TestResult()
                .setUuid(UUID.randomUUID().toString())
                .setName(randomString());
    }

    public static StepResult randomStepResult() {
        return new StepResult()
                .setName(randomString());
    }

    public static Link randomLink() {
        return new Link()
                .setName(randomString())
                .setType(randomString())
                .setUrl(randomString());
    }
}
