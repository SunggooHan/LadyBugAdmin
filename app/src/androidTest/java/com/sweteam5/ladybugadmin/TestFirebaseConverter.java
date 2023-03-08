package com.sweteam5.ladybugadmin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestFirebaseConverter {

    @Test
    public void testBool() {
        String testBool = "[null, false, false, true]"; // Expected result of
        boolean[] expectedResult = {false, false, true};
        boolean[] result = FirebaseConverter.convertString2BoolList(testBool);
        assertThat(result, is(expectedResult));
    }

    @Test
    public void testCode() {
        String testDict = "{11111=, 12345=, 22222=}";
        String[] expectedCodeList = {"11111", "12345", "22222"};
        String[] resultCodeList = FirebaseConverter.convertDict2CodeList(testDict);
        assertThat(resultCodeList, is(expectedCodeList));
    }

    @Test
    public void testBusLoc() {
        String testDict = "{LocationIndex_3=-1, LocationIndex_2=-1, LocationIndex_1=1}";
        int[] expectedLocationList = {1, -1, -1};
        int[] resultLocationList = FirebaseConverter.convertMsg2BusLocation(testDict);
        assertThat(resultLocationList, is(expectedLocationList));
    }
}
