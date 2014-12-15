package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.List;

/**
 * Unit testing for MainActivity
 * Created by Adel on 12/9/14.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    private MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                MainActivity.class);
        startActivity(intent, null, null);
        activity = getActivity();
    }

    public void testMainLayout() {
        assertNotNull(activity.findViewById(R.id.left_drawer));
    }

    public void testMainDrawerLayout() {
        assertTrue(activity.findViewById(R.id.left_drawer) instanceof ListView);
    }

    public void testLabsAPI() {
        Labs mLabs = activity.getLabsInstance();
        List<Course> courses = mLabs.courses("CIS 110");
        assertEquals(110, courses.get(0).course_number);
    }
}
