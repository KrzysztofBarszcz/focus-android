/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.whatsnew

import android.preference.PreferenceManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WhatsNewTest {
    @Before
    fun setUp() {
        // Reset all saved and cached values before running a test

        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .clear()
                .apply()

        WhatsNew.wasUpdatedRecently = null
    }

    /**
     * The first installation should not be tracked as an app update.
     */
    @Test
    fun testDefault() {
        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))
    }

    @Test
    fun testWithUpdatedAppVersionName() {
        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .putString(WhatsNew.PREFERENCE_KEY_APP_NAME, "2.0")
                .apply()

        // Reset cached value
        WhatsNew.wasUpdatedRecently = null

        // The app was updated recently
        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        // wasUpdatedRecently() will continue to return true as long as the process is alive
        for (i in 1..10) {
            assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))
        }
    }

    @Test
    fun testOverMultipleSessions() {
        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .putString(WhatsNew.PREFERENCE_KEY_APP_NAME, "2.0")
                .apply()

        // Reset cached value
        WhatsNew.wasUpdatedRecently = null

        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        // We simulate a new session by resetting the cached value
        WhatsNew.wasUpdatedRecently = null
        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        WhatsNew.wasUpdatedRecently = null
        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        // After three sessions the method will return false again
        WhatsNew.wasUpdatedRecently = null
        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))
    }

    @Test
    fun testResettingManually() {
        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .putString(WhatsNew.PREFERENCE_KEY_APP_NAME, "2.0")
                .apply()

        // Reset cached value
        WhatsNew.wasUpdatedRecently = null

        assertTrue(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        // Now we reset the state manually
        WhatsNew.reset(RuntimeEnvironment.application)

        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))

        // And also the next time we will return false
        WhatsNew.wasUpdatedRecently = null
        assertFalse(WhatsNew.wasUpdatedRecently(RuntimeEnvironment.application))
    }
}