package io.qameta.allure.jbehave;

import io.qameta.allure.Lifecycle;
import io.qameta.allure.aspect.AttachmentsAspects;
import io.qameta.allure.aspect.StepsAspects;
import io.qameta.allure.jbehave.steps.StackSteps;
import io.qameta.allure.model3.Status;
import io.qameta.allure.model3.TestResult;
import io.qameta.allure.test.InMemoryResultsWriter;
import org.assertj.core.groups.Tuple;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author charlie (Dmitry Baev).
 */
public class AllureJbehaveTest {

    private InMemoryResultsWriter results;
    private Embedder embedder = new Embedder();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        results = new InMemoryResultsWriter();
        final Lifecycle lifecycle = new Lifecycle(results);
        StepsAspects.setLifecycle(lifecycle);
        AttachmentsAspects.setLifecycle(lifecycle);
        embedder.useEmbedderControls(new EmbedderControls()
                .doGenerateViewAfterStories(false)
                .doVerboseFailures(true)
        );
        embedder.useConfiguration(new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                .useStoryReporterBuilder(new ReportlessStoryReporterBuilder(folder.newFolder())
                        .withReporters(new AllureJbehave(lifecycle))
                )
        );
        embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), new StackSteps())
                .createCandidateSteps()
        );
    }

    @Test
    public void shouldAddResults() throws Exception {
        embedder.runStoriesAsPaths(singletonList("stories/stack_story.story"));
        final List<TestResult> testResults = results.getAllTestResults();

        assertThat(testResults)
                .hasSize(2)
                .extracting(TestResult::getFullName, TestResult::getStatus)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("stack_story.story: Add elements to empty stack", Status.FAILED),
                        Tuple.tuple("stack_story.story: Clear stack", Status.PASSED)
                );
    }

    static class ReportlessStoryReporterBuilder extends StoryReporterBuilder {

        private final File outputDirectory;

        ReportlessStoryReporterBuilder(final File outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        @Override
        public File outputDirectory() {
            return outputDirectory;
        }
    }
}