/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file Jenkinsfile is part of nyx
 * Last modified on 17-09-2024 01:21 a.m.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * GRADLE-CONVENTIONS-PLUGIN IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

pipeline {
    agent any

    tools {
        jdk "Temurin Java 21"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Setup Gradle') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                withGradle {
                    sh './gradlew build'
                }
            }

            post {
                success {
                    archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true, onlyIfSuccessful: true

                    javadoc javadocDir: 'build/dokka/html/', keepAll: true
                }
            }
        }

        stage('Tests') {
            steps {
                withGradle {
                    sh './gradlew test'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                withGradle {
                    sh './gradlew functionalTest'
                }
            }
        }

        stage('Deploy to snapshots repositories') {
            when {
                not {
                    buildingTag()
                }
            }

            steps {
                withCredentials([
                        string(credentialsId: 'maven-signing-key', variable: 'ORG_GRADLE_PROJECT_signingKey'),
                        // string(credentialsId: 'maven-signing-key-id', variable: 'ORG_GRADLE_PROJECT_signingKeyId'),
                        string(credentialsId: 'maven-signing-key-password', variable: 'ORG_GRADLE_PROJECT_signingPassword'),
                        usernamePassword(
                                credentialsId: 'solo-studios-maven',
                                passwordVariable: 'ORG_GRADLE_PROJECT_SoloStudiosSnapshotsPassword',
                                usernameVariable: 'ORG_GRADLE_PROJECT_SoloStudiosSnapshotsUsername'
                        )
                ]) {
                    withGradle {
                        sh './gradlew publishAllPublicationsToSoloStudiosSnapshotsRepository'
                    }
                }
            }

        }

        stage('Deploy to releases repositories') {
            when {
                buildingTag()
            }

            steps {
                withCredentials([
                        string(credentialsId: 'maven-signing-key', variable: 'ORG_GRADLE_PROJECT_signingKey'),
                        // string(credentialsId: 'maven-signing-key-id', variable: 'ORG_GRADLE_PROJECT_signingKeyId'),
                        string(credentialsId: 'maven-signing-key-password', variable: 'ORG_GRADLE_PROJECT_signingPassword'),
                        usernamePassword(
                                credentialsId: 'solo-studios-maven',
                                passwordVariable: 'ORG_GRADLE_PROJECT_SoloStudiosReleasesPassword',
                                usernameVariable: 'ORG_GRADLE_PROJECT_SoloStudiosReleasesUsername'
                        ),
                        usernamePassword(
                                credentialsId: 'sonatype-maven-credentials',
                                passwordVariable: 'ORG_GRADLE_PROJECT_SonatypePassword',
                                usernameVariable: 'ORG_GRADLE_PROJECT_SonatypeUsername'
                        )
                ]) {
                    withGradle {
                        sh './gradlew publishAllPublicationsToSoloStudiosReleasesRepository'
                        sh './gradlew publishAllPublicationsToSonatypeRepository'
                    }
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/build/test-results/*/TEST-*.xml'
            recordIssues enabledForFailure: true, tools: [kotlin()]
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]

            cleanWs()
        }
    }
}
