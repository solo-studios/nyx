/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file LicenseInfoExtension.kt is part of nyx
 * Last modified on 10-06-2024 03:21 p.m.
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

package ca.solostudios.nyx.project

import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property

public open class NyxLicenseInfoExtension(override val project: Project) : HasProject {
    /**
     * The name for the selected license. Recommended to be the SPDX identifier.
     */
    public val name: Property<String> = property()

    /**
     * The url for the selected license.
     */
    public val url: Property<String> = property()

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

    public fun useMPLv1() {
        name.set("MPL-1.0")
        url.set("https://mozilla.org/MPL/1.0/")
    }


    public fun useMPLv11() {
        name.set("MPL-1.1")
        url.set("https://mozilla.org/MPL/1.1/")
    }

    public fun useMPLv2() {
        name.set("MPL-2.0")
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

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYv4() {
        name.set("CC-BY-NC-SA-4.0")
        url.set("https://creativecommons.org/licenses/by/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYv3() {
        name.set("CC-BY-3.0")
        url.set("https://creativecommons.org/licenses/by/3.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYSAv4() {
        name.set("CC-BY-SA-4.0")
        url.set("https://creativecommons.org/licenses/by-sa/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYSAv3() {
        name.set("CC-BY-SA-3.0")
        url.set("https://creativecommons.org/licenses/by-sa/3.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCSAv4() {
        name.set("CC-BY-NC-SA-4.0")
        url.set("https://creativecommons.org/licenses/by-nc-sa/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCSAv3() {
        name.set("CC-BY-NC-SA-3.0")
        url.set("https://creativecommons.org/licenses/by-nc-sa/3.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCNDv4() {
        name.set("CC-BY-NC-SA-4.0")
        url.set("https://creativecommons.org/licenses/by-nc-nd/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCNDv3() {
        name.set("CC-BY-NC-ND-3.0")
        url.set("https://creativecommons.org/licenses/by-nc-nd/3.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCv4() {
        name.set("CC-BY-NC-4.0")
        url.set("https://creativecommons.org/licenses/by-nc/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNCv3() {
        name.set("CC-BY-NC-3.0")
        url.set("https://creativecommons.org/licenses/by-nc/3.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNDv4() {
        name.set("CC-BY-ND-4.0")
        url.set("https://creativecommons.org/licenses/by-nd/4.0/")
    }

    @Deprecated(
        """
        It is not recommended to use Creative Commons licenses for software.

        From the official Creative Commons Website:
        > We recommend against using Creative Commons licenses for software.
        > Instead, we strongly encourage you to use one of the very good software licenses
        > which are already available.
        > We recommend considering licenses listed as free by the Free Software Foundation
        > and listed as “open source” by the Open Source Initiative.
        https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
        """
    )
    public fun useCCBYNDv3() {
        name.set("CC-BY-ND-3.0")
        url.set("https://creativecommons.org/licenses/by-nd/3.0/")
    }
}
