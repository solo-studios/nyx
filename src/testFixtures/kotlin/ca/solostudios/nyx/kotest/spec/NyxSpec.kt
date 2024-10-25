/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxSpec.kt is part of nyx
 * Last modified on 25-10-2024 11:40 a.m.
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

@file:Suppress("unused")

package ca.solostudios.nyx.kotest.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.factory.FactoryConstrainedAfterContainerListener
import io.kotest.core.factory.FactoryConstrainedAfterTestListener
import io.kotest.core.factory.FactoryConstrainedBeforeContainerListener
import io.kotest.core.factory.FactoryConstrainedBeforeTestListener
import io.kotest.core.factory.FactoryConstrainedTestListener
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.TestName
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.TestWithConfigBuilder
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.test.TestScope


fun nyxSpec(block: NyxSpecTestFactoryConfiguration.() -> Unit): TestFactory = NyxSpecTestFactoryConfiguration().apply(block).build()

interface NyxSpecRootScope : RootScope {
    fun feature(name: String, test: suspend NyxSpecFeatureContainerScope.() -> Unit) = addFeature(name, false, test)
    fun xfeature(name: String, test: suspend NyxSpecFeatureContainerScope.() -> Unit) = addFeature(name, true, test)

    fun given(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, false, test)
    fun xgiven(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, true, test)

    private fun addFeature(name: String, xdisabled: Boolean, test: suspend NyxSpecFeatureContainerScope.() -> Unit) {
        addContainer(TestName("Feature: ", name, true), disabled = xdisabled, null) {
            NyxSpecFeatureContainerScope(this).test()
        }
    }

    private fun addGiven(name: String, xdisabled: Boolean, test: suspend NyxSpecGivenContainerScope.() -> Unit) {
        addContainer(TestName("given ", name, true), disabled = xdisabled, null) {
            NyxSpecGivenContainerScope(this).test()
        }
    }
}

class NyxSpecTestFactoryConfiguration : TestFactoryConfiguration(), NyxSpecRootScope {
    internal var tests = emptyList<RootTest>()

    override fun add(test: RootTest) {
        tests = tests + test
    }

    internal fun build(): TestFactory {
        return TestFactory(
            factoryId = factoryId,
            tags = appliedTags(),
            extensions = registeredExtensions().map {
                when (it) {
                    is TestListener -> FactoryConstrainedTestListener(factoryId, it)
                    is BeforeContainerListener -> FactoryConstrainedBeforeContainerListener(factoryId, it)
                    is AfterContainerListener -> FactoryConstrainedAfterContainerListener(factoryId, it)
                    is BeforeTestListener -> FactoryConstrainedBeforeTestListener(factoryId, it)
                    is AfterTestListener -> FactoryConstrainedAfterTestListener(factoryId, it)
                    else -> it
                }
            },
            assertionMode = assertions,
            tests = tests,
            configuration = this
        )
    }
}

abstract class NyxSpec(body: NyxSpec.() -> Unit = {}) : DslDrivenSpec(), NyxSpecRootScope {
    init {
        body()
    }

    @ExperimentalKotest
    suspend fun ContainerScope.feature(name: String, test: suspend NyxSpecFeatureContainerScope.() -> Unit) {
        registerContainer(TestName("Feature: ", name, true), disabled = false, null) {
            NyxSpecFeatureContainerScope(this).test()
        }
    }

    @ExperimentalKotest
    suspend fun ContainerScope.given(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) {
        addGiven(name, test, xdisabled = false)
    }

    suspend fun ContainerScope.upon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) {
        addUpon(name, test, xdisabled = false)
    }
}

@KotestTestScope
class NyxSpecFeatureContainerScope(testScope: TestScope) : AbstractContainerScope(testScope) {
    suspend fun scenario(name: String, test: suspend NyxSpecScenarioContainerScope.() -> Unit) = addScenario(name, test, xdisabled = false)
    suspend fun xscenario(name: String, test: suspend NyxSpecScenarioContainerScope.() -> Unit) = addScenario(name, test, xdisabled = true)

    suspend fun given(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, test, xdisabled = false)
    suspend fun xgiven(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, test, xdisabled = true)

    suspend fun upon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = false)
    suspend fun xupon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = true)
}

@KotestTestScope
class NyxSpecScenarioContainerScope(testScope: TestScope) : AbstractContainerScope(testScope) {
    suspend fun given(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, test, xdisabled = false)
    suspend fun xgiven(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addGiven(name, test, xdisabled = true)

    suspend fun upon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = false)
    suspend fun xupon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = true)
}

@KotestTestScope
class NyxSpecGivenContainerScope(testScope: TestScope) : AbstractContainerScope(testScope) {
    suspend fun and(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addAndGiven(name, test, xdisabled = false)
    suspend fun xand(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit) = addAndGiven(name, test, xdisabled = true)

    suspend fun upon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = false)
    suspend fun xupon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addUpon(name, test, xdisabled = true)

    suspend fun should(name: String, test: suspend TestScope.() -> Unit) = addShould(name, test, xdisabled = false)
    suspend fun xshould(name: String, test: suspend TestScope.() -> Unit) = addShould(name, test, xdisabled = true)

    fun should(name: String) = shouldWithConfig(name, xdisabled = false)
    fun xshould(name: String) = shouldWithConfig(name, xdisabled = true)
}

@KotestTestScope
class NyxSpecUponContainerScope(testScope: TestScope) : AbstractContainerScope(testScope) {
    suspend fun and(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addAndUpon(name, test, xdisabled = false)
    suspend fun xand(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit) = addAndUpon(name, test, xdisabled = true)

    suspend fun should(name: String, test: suspend TestScope.() -> Unit) = addShould(name, test, xdisabled = false)
    suspend fun xshould(name: String, test: suspend TestScope.() -> Unit) = addShould(name, test, xdisabled = true)

    fun should(name: String) = shouldWithConfig(name, xdisabled = false)
    fun xshould(name: String) = shouldWithConfig(name, xdisabled = true)
}

private suspend fun ContainerScope.addScenario(name: String, test: suspend NyxSpecScenarioContainerScope.() -> Unit, xdisabled: Boolean) {
    registerContainer(TestName("Scenario: ", name, true), xdisabled, null) {
        NyxSpecScenarioContainerScope(this).test()
    }
}

private suspend fun ContainerScope.addGiven(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit, xdisabled: Boolean) {
    registerContainer(TestName("Given: ", name, true), xdisabled, null) {
        NyxSpecGivenContainerScope(this).test()
    }
}

private suspend fun ContainerScope.addAndGiven(name: String, test: suspend NyxSpecGivenContainerScope.() -> Unit, xdisabled: Boolean) {
    registerContainer(TestName("And: ", name, true), xdisabled, null) {
        NyxSpecGivenContainerScope(this).test()
    }
}

private suspend fun ContainerScope.addUpon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit, xdisabled: Boolean) {
    registerContainer(TestName("Upon: ", name, true), xdisabled, null) {
        NyxSpecUponContainerScope(this).test()
    }
}

private suspend fun ContainerScope.addAndUpon(name: String, test: suspend NyxSpecUponContainerScope.() -> Unit, xdisabled: Boolean) {
    registerContainer(TestName("And: ", name, true), xdisabled, null) {
        NyxSpecUponContainerScope(this).test()
    }
}

private suspend fun ContainerScope.addShould(name: String, test: suspend TestScope.() -> Unit, xdisabled: Boolean) {
    registerTest(TestName("Should: ", name, true), disabled = xdisabled, null, test)
}

private fun ContainerScope.shouldWithConfig(name: String, xdisabled: Boolean): TestWithConfigBuilder {
    return TestWithConfigBuilder(TestName("Should: ", name, true), this, xdisabled = xdisabled)
}
