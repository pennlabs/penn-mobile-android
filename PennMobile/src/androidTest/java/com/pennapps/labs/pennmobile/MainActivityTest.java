package com.pennapps.labs.pennmobile;

import android.content.Intent;
import com.google.android.material.navigation.NavigationView;
import android.view.ContextThemeWrapper;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Unit testing for MainActivity
 * Created by Adel on 12/9/14.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    private MainActivity activity;
    private Labs mLabs;

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
        mLabs = MainActivity.getLabsInstance();
    }

    public void testMainLayout() {
        assertNotNull(activity.findViewById(R.id.navigation));
    }

    public void testMainDrawerLayout() {
        assertTrue(activity.findViewById(R.id.navigation) instanceof NavigationView);
    }

    public void testLabsCoursesAPI() {
        List<Course> courses = mLabs.courses("CIS 110").toList().toBlocking().single().get(0);
        assertEquals(110, courses.get(0).course_number);
    }

    public void testLabsCoursesInstructors() {
        List<Course> courses = mLabs.courses("CIS 110").toList().toBlocking().single().get(0);
        assertEquals("Benedict Brown", courses.get(0).instructors.get(0).name);
    }

    public void testCourseMeetings() {
        List<Course> courses = mLabs.courses("CIS 110").toList().toBlocking().single().get(0);
        // Course locations not announced yet
        assertEquals("", courses.get(0).meetings.get(0).building_name);
    }

    public void testDirectorySearch() {
        List<Person> people = mLabs.people("adel").toList().toBlocking().single().get(0);
        assertEquals("ADELMAN, STEPHEN R", people.get(0).name);
    }

    public void testDirectorySearchGetName() {
        List<Person> people = mLabs.people("adel").toList().toBlocking().single().get(0);
        assertEquals("Stephen R Adelman", people.get(0).getName());
    }

    public void testCourseMeetingSection() {
        List<Course> courses = mLabs.courses("BIBB 109").toList().toBlocking().single().get(0);
        assertEquals("BIBB109401", courses.get(0).meetings.get(0).section_id);
    }

    public void testDiningVenues() {
        List<Venue> venues = mLabs.venues().toList().toBlocking().single().get(0);
        assertEquals("1920 Commons", venues.get(0).name);
    }

    public void testDiningMenuMeals() {
        List<Venue> venues = mLabs.venues().toList().toBlocking().single().get(0);
        DiningHall commons = mLabs.daily_menu(venues.get(0).id).toList().toBlocking().single().get(0);
        assertTrue(commons.menus.size() > 0);
    }

    public void testDiningMenu() {
        List<Venue> venues = mLabs.venues().toList().toBlocking().single().get(0);
        DiningHall commons = mLabs.daily_menu(venues.get(0).id).toList().toBlocking().single().get(0);
        DiningHall.Menu menu = commons.menus.get(0);
        assertTrue(menu.name.equals("Brunch") || menu.name.equals("Breakfast"));
    }

    public void testDiningMenuStation() {
        List<Venue> venues = mLabs.venues().toList().toBlocking().single().get(0);
        DiningHall commons = mLabs.daily_menu(venues.get(0).id).toList().toBlocking().single().get(0);
        DiningHall.Menu menu = commons.menus.get(0);
        DiningHall.DiningStation station = menu.stations.get(0);
        assertTrue(station.items.size() > 1);
    }
}
