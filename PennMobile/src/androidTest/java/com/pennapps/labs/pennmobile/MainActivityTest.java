package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DirectoryPerson;

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

    public void testLabsCoursesAPI() {
        Labs mLabs = activity.getLabsInstance();
        List<Course> courses = mLabs.courses("CIS 110");
        assertEquals(110, courses.get(0).course_number);
    }

    public void testLabsCoursesInstructors() {
        Labs mLabs = activity.getLabsInstance();
        List<Course> courses = mLabs.courses("CIS 110");
        assertEquals("Eric Eaton", courses.get(0).instructors.get(0).name);
    }

    public void testCourseMeetings() {
        Labs mLabs = activity.getLabsInstance();
        List<Course> courses = mLabs.courses("CIS 110");
        assertEquals("Towne Building", courses.get(0).meetings.get(0).building_name);
    }

    public void testDirectorySearch() {
        Labs mLabs = activity.getLabsInstance();
        List<DirectoryPerson> people = mLabs.people("adel");
        assertEquals("ADELEKE, VICTOR O", people.get(0).name);
    }
}
