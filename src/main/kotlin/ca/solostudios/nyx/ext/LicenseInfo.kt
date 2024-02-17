package ca.solostudios.nyx.ext

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

public open class LicenseInfo(project: Project) {
    /**
     * The name for the selected license. Recommended to be the SPDX identifier.
     */
    public val name: Property<String> = project.objects.property()

    /**
     * The url for the selected license.
     */
    public val url: Property<String> = project.objects.property()

    public fun useMIT() {
        name.set("MIT")
        url.set("https://mit-license.org/")
    }

    public fun useApachev11() {
        name.set("Apache-1.1")
        url.set("https://www.apache.org/licenses/LICENSE-1.1")
    }

    public fun useApachev2() {
        name.set("Apache-2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0")
    }

    public fun useISC() {
        name.set("ISC")
        url.set("https://www.isc.org/licenses/")
    }

    public fun use0BSD() {
        name.set("0BSD")
    }

    public fun useBSD1Clause() {
        name.set("BSD-1-Clause")
    }

    public fun useBSD2Clause() {
        name.set("BSD-2-Clause")
    }

    public fun useBSD3Clause() {
        name.set("BSD-3-Clause")
    }

    public fun useGPLv2() {
        name.set("GPLv2")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useGPLv3() {
        name.set("GPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useLGPLv2() {
        name.set("LGPLv2.0")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useLGPLv21() {
        name.set("LGPLv2.1")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useLGPLv3() {
        name.set("LGPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useAGPLv3() {
        name.set("AGPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    public fun useMPL() {
        name.set("MPL")
        url.set("https://mozilla.org/MPL/2.0/")
    }

    public fun useEPLv1() {
        name.set("EPL-1.0")
        url.set("https://www.eclipse.org/legal/epl-v10.html")
    }

    public fun useEPLv2() {
        name.set("EPL-2.0")
        url.set("https://www.eclipse.org/legal/epl-2.0/")
    }

    public fun useUnlicense() {
        name.set("Unlicense")
        url.set("https://unlicense.org/")
    }

    public fun useCC0() {
        name.set("CC0")
        url.set("https://creativecommons.org/public-domain/cc0/")
    }

    public fun useWTFPL() {
        name.set("WTFPL")
        @Suppress("HttpUrlsUsage")
        url.set("http://www.wtfpl.net/")
    }
}
