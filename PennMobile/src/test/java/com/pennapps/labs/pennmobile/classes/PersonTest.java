package com.pennapps.labs.pennmobile.classes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing for a Person
 * Created by Adel on 7/12/15.
 */
public class PersonTest {
    private Person person;

    @Before
    public void setUp() throws Exception {
        person = new Person("ADELMAN, STEPHEN R", "(215) 898-7297");
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Stephen R Adelman", person.getName());
    }
}