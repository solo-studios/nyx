/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxLicenseInfoExtension.kt is part of nyx
 * Last modified on 25-12-2024 07:53 p.m.
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
 * NYX IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.solostudios.nyx.project

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property

public class NyxLicenseInfoExtension(override val project: Project) : InternalNyxExtension {
    /**
     * The name for the selected license. Recommended to be the SPDX
     * identifier.
     */
    public val name: Property<String> = property()

    /**
     * The url for the selected license.
     */
    public val url: Property<String> = property()

    /**
     * Selects the MIT license.
     */
    public fun useMIT() {
        name.set("MIT")
        url.set("https://mit-license.org/")
    }

    /**
     * Selects the Apache 1.1 license.
     */
    public fun useApachev11() {
        name.set("Apache-1.1")
        url.set("https://www.apache.org/licenses/LICENSE-1.1")
    }

    /**
     * Selects the Apache 2.0 license.
     */
    public fun useApachev2() {
        name.set("Apache-2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0")
    }

    /**
     * Selects the ISC license.
     */
    public fun useISC() {
        name.set("ISC")
        url.set("https://www.isc.org/licenses/")
    }

    /**
     * Selects the Zero-Clause BSD license.
     */
    public fun use0BSD() {
        name.set("0BSD")
    }

    /**
     * Selects the 1-Clause BSD license.
     */
    public fun useBSD1Clause() {
        name.set("BSD-1-Clause")
    }

    /**
     * Selects the 2-Clause BSD license.
     */
    public fun useBSD2Clause() {
        name.set("BSD-2-Clause")
    }

    /**
     * Selects the 3-Clause BSD license.
     */
    public fun useBSD3Clause() {
        name.set("BSD-3-Clause")
    }

    /**
     * Selects the GNU General Public License version 2.
     */
    public fun useGPLv2() {
        name.set("GPLv2")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the GNU General Public License version 3.
     */
    public fun useGPLv3() {
        name.set("GPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the GNU Library General Public License version 2.
     */
    public fun useLGPLv2() {
        name.set("LGPLv2.0")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the GNU Lesser General Public License version 2.1.
     */
    public fun useLGPLv21() {
        name.set("LGPLv2.1")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the GNU Lesser General Public License version 3.
     */
    public fun useLGPLv3() {
        name.set("LGPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the GNU Affero General Public License version 3.
     */
    public fun useAGPLv3() {
        name.set("AGPLv3")
        url.set("https://www.gnu.org/licenses/")
    }

    /**
     * Selects the Mozilla Public License version 1.0.
     */
    public fun useMPLv1() {
        name.set("MPL-1.0")
        url.set("https://mozilla.org/MPL/1.0/")
    }

    /**
     * Selects the Mozilla Public License version 1.1.
     */
    public fun useMPLv11() {
        name.set("MPL-1.1")
        url.set("https://mozilla.org/MPL/1.1/")
    }

    /**
     * Selects the Mozilla Public License version 2.0.
     */
    public fun useMPLv2() {
        name.set("MPL-2.0")
        url.set("https://mozilla.org/MPL/2.0/")
    }

    /**
     * Selects the Eclipse Public License version 1.0.
     */
    public fun useEPLv1() {
        name.set("EPL-1.0")
        url.set("https://www.eclipse.org/legal/epl-v10.html")
    }

    /**
     * Selects the Eclipse Public License version 2.0.
     */
    public fun useEPLv2() {
        name.set("EPL-2.0")
        url.set("https://www.eclipse.org/legal/epl-2.0/")
    }

    /**
     * Selects the Unlicense.
     */
    public fun useUnlicense() {
        name.set("Unlicense")
        url.set("https://unlicense.org/")
    }

    /**
     * Selects the Creative Commons Zero v1.0 Universal license.
     */
    public fun useCC0() {
        name.set("CC0-1.0")
        url.set("https://creativecommons.org/public-domain/cc0/")
    }

    /**
     * Selects the Do What The F*ck You Want To Public License.
     */
    public fun useWTFPL() {
        name.set("WTFPL")
        @Suppress("HttpUrlsUsage")
        url.set("http://www.wtfpl.net/")
    }

    /**
     * Selects the Creative Commons Attribution Non Commercial Share Alike 4.0
     * International
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution 3.0 Unported license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Share Alike 4.0 International
     * license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Share Alike 3.0 Unported
     * license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial Share Alike 4.0
     * International license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial Share Alike 3.0
     * Unported license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial Share Alike 4.0
     * International license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial No Derivatives
     * 3.0 Unported license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial 4.0
     * International license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution Non Commercial 3.0 Unported
     * license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution No Derivatives 4.0
     * International license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    /**
     * Selects the Creative Commons Attribution No Derivatives 3.0 Unported
     * license
     *
     * **The use of this license is deprecated**
     *
     * It is not recommended to use Creative Commons licenses for software.
     *
     * From the official Creative Commons website:
     * > We recommend against using Creative Commons licenses for software.
     * > Instead, we strongly encourage you to use one of the very good software
     * > licenses which are already available. We recommend considering licenses
     * > listed as free by the Free Software Foundation and listed as “open
     * > source” by the Open Source Initiative.
     *
     * https://creativecommons.org/faq/#can-i-apply-a-creative-commons-license-to-software
     */
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

    override fun configureProject() {}
}
