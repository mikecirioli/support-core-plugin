package com.cloudbees.jenkins.support.impl;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.cloudbees.jenkins.support.SupportTestUtils;
import hudson.slaves.DumbSlave;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Tests for the {@link NodeRemoteDirectoryComponent}
 */
@WithJenkins
class NodeRemoteDirectoryComponentTest {

    private DumbSlave createAgent(JenkinsRule j) throws Exception {
        var agent = j.createOnlineSlave();
        await().until(agent.getRootPath()::isDirectory);
        return agent;
    }

    /*
     * Test adding agent remote directory content with the defaults.
     */
    @Test
    void addContents(JenkinsRule j) throws Exception {
        var agent = createAgent(j);

        Map<String, String> output =
                SupportTestUtils.invokeComponentToMap(new NodeRemoteDirectoryComponent(), agent.toComputer());

        String prefix = "nodes/slave/" + agent.getNodeName() + "/remote";
        assertThat(output.keySet(), hasItem(matchesPattern(prefix + "/support/.*.log")));
    }

    /*
     * Test adding agent remote directory content with excludes pattern(s).
     */
    @Test
    void addContentsWithExcludes(JenkinsRule j) throws Exception {
        var agent = createAgent(j);

        Map<String, String> output = SupportTestUtils.invokeComponentToMap(
                new NodeRemoteDirectoryComponent("", "**/*.log", true, 10), agent.toComputer());

        String prefix = "nodes/slave/" + agent.getNodeName() + "/remote";
        assertThat(output.keySet(), not(hasItem(matchesPattern(prefix + ".*/.*.log"))));
    }

    /*
     * Test adding agent remote directory content with includes pattern(s).
     */
    @Test
    void addContentsWithIncludes(JenkinsRule j) throws Exception {
        var agent = createAgent(j);

        Map<String, String> output = SupportTestUtils.invokeComponentToMap(
                new NodeRemoteDirectoryComponent("support/*.log", "", true, 10), agent.toComputer());

        String prefix = "nodes/slave/" + agent.getNodeName() + "/remote";
        assertThat(output.keySet(), hasItem(matchesPattern(prefix + "/support/.*.log")));
    }

    /*
     * Test adding agent remote directory content with includes pattern(s).
     */
    @Test
    void addContentsWithMaxDepth(JenkinsRule j) throws Exception {
        var agent = createAgent(j);

        Map<String, String> output = SupportTestUtils.invokeComponentToMap(
                new NodeRemoteDirectoryComponent("", "", true, 1), agent.toComputer());

        String prefix = "nodes/slave/" + agent.getNodeName() + "/remote";
        assertThat(output.keySet(), not(hasItem(matchesPattern(prefix + "/support/.*.log"))));
    }
}
