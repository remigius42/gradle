/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.integtests

import org.gradle.api.JavaVersion
import org.gradle.integtests.fixtures.CrossVersionIntegrationSpec

class TaskUpToDateCrossVersionIntegrationTest extends CrossVersionIntegrationSpec {
    public void "task is not up-to-date when Gradle version changes"() {
        given:
        buildFile << """
apply plugin: 'java'
        """

        and:
        file('src/main/java/org/gradle/Person.java') << """
package org.gradle;
class Person { }
"""

        expect:
        version previous withTasks 'compileJava' run() assertTaskNotSkipped(":compileJava")
        version previous withTasks 'compileJava' run() assertTaskSkipped(":compileJava")

        version current withTasks 'compileJava' run() assertTaskNotSkipped(":compileJava")
        version current withTasks 'compileJava' run() assertTaskSkipped(":compileJava")
    }

    public void "Compile Java task is not up-to-date when Java verison changes"() {
        given:
        buildFile << """
apply plugin: 'java'
        """

        and:
        file('src/main/java/org/gradle/Person.java') << """
package org.gradle;
class Person { }
"""

        expect:
        def oldVersion = System.properties['java.version']
        try {
            setJavaVersion("1.6")
            version current withTasks 'compileJava' run() assertTaskNotSkipped(":compileJava")
            version current withTasks 'compileJava' run() assertTaskSkipped(":compileJava")
            setJavaVersion("1.7")
            version current withTasks 'compileJava' run() assertTaskNotSkipped(":compileJava")
        } finally {
            setJavaVersion(oldVersion)
        }
    }

    private static void setJavaVersion(String version) {
        System.properties['java.version'] = version
        JavaVersion.resetCurrent()
    }
}
