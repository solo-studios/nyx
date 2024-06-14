/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file Jenkinsfile is part of nyx
 * Last modified on 14-06-2024 03:18 p.m.
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

        stage('Deploy Release to Solo Studios repository') {
            steps {
                withCredentials([
                        string(credentialsId: 'maven-signing-key', variable: 'ORG_GRADLE_PROJECT_signingKey'),
                        // string(credentialsId: 'maven-signing-key-id', variable: 'ORG_GRADLE_PROJECT_signingKeyId'),
                        string(credentialsId: 'maven-signing-key-password', variable: 'ORG_GRADLE_PROJECT_signingPassword'),
                        usernamePassword(
                                credentialsId: 'solo-studios-maven',
                                passwordVariable: 'ORG_GRADLE_PROJECT_SoloStudiosPassword',
                                usernameVariable: 'ORG_GRADLE_PROJECT_SoloStudiosUsername'
                        )
                ]) {
                    withGradle {
                        sh './gradlew publishAllPublicationsToSoloStudiosRepository'
                    }
                }
            }
        }

        stage('Deploy Release to Sonatype Repository') {
            when {
                expression { env.TAG_NAME != null && env.TAG_NAME.matches('v\\d+\\.\\d+\\.\\d+') }
            }

            steps {
                withCredentials([
                        string(credentialsId: 'maven-signing-key', variable: 'ORG_GRADLE_PROJECT_signingKey'),
                        // string(credentialsId: 'maven-signing-key-id', variable: 'ORG_GRADLE_PROJECT_signingKeyId'),
                        string(credentialsId: 'maven-signing-key-password', variable: 'ORG_GRADLE_PROJECT_signingPassword'),
                        usernamePassword(
                                credentialsId: 'sonatype-maven-credentials',
                                passwordVariable: 'ORG_GRADLE_PROJECT_SonatypePassword',
                                usernameVariable: 'ORG_GRADLE_PROJECT_SonatypeUsername'
                        )
                ]) {
                    withGradle {
                        sh './gradlew publishAllPublicationsToSonatypeRepository'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
