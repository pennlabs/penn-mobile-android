package com.pennapps.labs.pennmobile;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
    }

    public void testApplicationPackageName() {
        Application mApplication = getApplication();
        assertEquals("com.pennapps.labs.pennmobile", mApplication.getPackageName());
    }
}