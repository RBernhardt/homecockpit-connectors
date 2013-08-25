package de.newsarea.homecockpit.connector.facade.registration.util;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class ClassLoaderHelperTest {

    @Test
    public void shouldNotReturnConstructor_InvalidArgumentType() throws Exception {
        assertNull(ClassLoaderHelper.determineConstructorByArgumentTypes(TestClass1.class, new Class<?>[]{Integer.class}));
    }

    @Test
    public void shouldNotReturnConstructor_InvalidArgumentCount() throws Exception {
        assertNull(ClassLoaderHelper.determineConstructorByArgumentTypes(TestClass1.class, new Class<?>[]{String.class, String.class}));
    }

    @Test
    public void shouldReturnCorrectConstructor() throws Exception {
        Constructor constructor = ClassLoaderHelper.determineConstructorByArgumentTypes(TestClass1.class, new Class<?>[]{String.class});
        assertEquals("public de.newsarea.homecockpit.connector.facade.registration.util.ClassLoaderHelperTest$TestClass1(java.lang.String)", constructor.toString());
        constructor = ClassLoaderHelper.determineConstructorByArgumentTypes(TestClass1.class, new Class<?>[]{String.class, Map.class});
        assertEquals("public de.newsarea.homecockpit.connector.facade.registration.util.ClassLoaderHelperTest$TestClass1(java.lang.String,java.util.Map)", constructor.toString());
    }

    @Test
    public void shouldReturnCorrectConstructor_Inherit() throws Exception {
        Constructor constructor = ClassLoaderHelper.determineConstructorByArgumentTypes(TestClass1.class, new Class<?>[]{List.class});
        assertEquals("public de.newsarea.homecockpit.connector.facade.registration.util.ClassLoaderHelperTest$TestClass1(java.util.ArrayList)", constructor.toString());
    }

    private static class TestClass1 {

        public TestClass1(String arg1) { }

        public TestClass1(String arg1, Map<String, String> arg2) { }

        public TestClass1(ArrayList<String> arg2) { }

    }

}
