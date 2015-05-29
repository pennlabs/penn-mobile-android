# QA

Documentation for the QA process to determine whether the app is working or
not, which is critical for now since Android testing is extremely lacking.
For each subheading, perform the tasks and determine whether the expected
behavior is being done.

## General

* **Confirm** that items on the homepage work and go to expected location
* **Confirm** that the drawer opens and closes using the button and using a gesture
* **Confirm** that items in the drawer go to the expected location

## Courses

* Confirm that searching for a course and viewing details works
  1. Search for `CIS`
  2. **Confirm** that the CIS courses from the currently offered semester are
     in the list of courses
  2. Tap the first item (usually 'CIS 110')
  3. **Confirm** that the course details page opens and is complete and accurate.

## Directory

* Confirm that searching for a Penn faculty works
  1. Search for `grant`
  2. **Confirm** that `Dr. Adam M Grant` is in the list

* Confirm that adding to contacts works
  1. Search for `grant`
  2. Confirm that clicking on the icon on the right offers to add Dr. Grant to
     your contacts

## Dining

* Confirm that venues and their times are correct
  1. Open dining page
  2. Cross-reference with the [dining website](http://cms.business-services.upenn.edu/dining/hours-locations-a-menus/dining-locations-a-menus.html)
     that all the open/close labels and times are correct

* Confirm that a dining hall menu is correct
  1. Open dining page
  2. Go to `1920 Commons` (must be open)
  3. Cross-reference with the [Commons menu](http://cms.business-services.upenn.edu/dining/hours-locations-a-menus/residential-dining/1920-commons.html)
     to confirm that the menu is correct

## Transit

* Confirm that navigating works
  1. Open transit
  2. Search for `parc`
  3. **Confirm** that it has selected the correct Parc restaurant
  4. Edit current location to `harnwell`
  5. Confirm that route is correct according to pennrides.com

* Confirm that transit layers work
  1. Open transit
  2. Click on layers
  3. Select `Campus Loop`
  4. Confirm that campus loop is complete and accurate

## News

* Confirm that opening the news works
  1. Open News
  2. **Confirm** that default page (currently the DP) loads and functions properly

* Confirm that news tabs work
  1. Open News
  2. Navigate to `34th Street` using tabs at top
  3. **Confirm** that 34th Street page loads and functions properly

## Map

* Confirm that search for campus buildings works
  1. Search for `harnwell`
  2. **Confirm** that Harnwell College House is in search results and has an appropriate image

## Campus Help

* Confirm that calling any number on the list works
