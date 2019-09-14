package com.elvinmahmudov.dynocom;

import com.elvinmahmudov.dynocom.model.Teacher;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaStringCompilerTest {

    static final String SINGLE_CLASS = "/* source file */   "
            + "package com.elvinmahmudov.dynocom.model;                       "
            + "                                                               "
            + "import com.elvinmahmudov.dynocom.model.*;                      "
            + "import lombok.Data;                                            "
            + "                                                               "
            + "@Data                                                          "
            + "public class MathTeacher extends Teacher {                     "
            + "    String mainSubject = \"Math\";                             "
            + "}                                                              ";

    static final String MULTIPLE_CLASSES = "/* a single class to many files */"
            + "package com.elvinmahmudov.dynocom.model;                       "
            + "import com.elvinmahmudov.dynocom.*;                            "
            + "import java.util.*;                                            "
            + "public class Multiple {                                        "
            + "    List<Bird> list = new ArrayList<Bird>();                   "
            + "    public void add(String name) {                             "
            + "        Bird bird = new Bird();                                "
            + "        bird.name = name;                                      "
            + "        this.list.add(bird);                                   "
            + "    }                                                          "
            + "    public Bird getFirstBird() {                               "
            + "        return this.list.get(0);                               "
            + "    }                                                          "
            + "    public static class StaticBird {                           "
            + "        public int weight = 100;                               "
            + "    }                                                          "
            + "    class NestedBird {                                         "
            + "        NestedBird() {                                         "
            + "            System.out.println(list.size() + \" birds...\");   "
            + "        }                                                      "
            + "    }                                                          "
            + "}                                                              "
            + "/* package level */                                            "
            + "class Bird {                                                   "
            + "    String name = null;                                        "
            + "}                                                              ";
    JavaStringCompiler compiler;

    @Before
    public void setUp() {
        compiler = new JavaStringCompiler();
    }

    @Test
    public void testCompileSingleClass() throws Exception {
        Map<String, byte[]> results = compiler.compile("MathTeacher.java", SINGLE_CLASS);
        assertEquals(1, results.size());
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.MathTeacher"));
        Class<?> clazz = compiler.loadClass("com.elvinmahmudov.dynocom.model.MathTeacher", results);
        // get method:
        Method setId = clazz.getMethod("setId", String.class);
        Method setDesignation = clazz.getMethod("setDesignation", String.class);
        Method setCollegeName = clazz.getMethod("setCollegeName", String.class);
        // try instance:
        Object obj = clazz.getDeclaredConstructor().newInstance();
        // set:
        setId.invoke(obj, "1");
        setDesignation.invoke(obj, "Teacher");
        setCollegeName.invoke(obj, "ADNSU");
        // get as customer:
        Teacher customer = (Teacher) obj;
        assertEquals("1", customer.getId());
        assertEquals("Teacher", customer.getDesignation());
        assertEquals("ADNSU", customer.getCollegeName());
    }

    @Test
    public void testCompileMultipleClasses() throws Exception {
        Map<String, byte[]> results = compiler.compile("Multiple.java", MULTIPLE_CLASSES);
        assertEquals(4, results.size());
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.Multiple"));
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.Multiple$StaticBird"));
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.Multiple$NestedBird"));
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.Bird"));
        Class<?> clzMul = compiler.loadClass("com.elvinmahmudov.dynocom.model.Multiple", results);
        // try instance:
        Object obj = clzMul.getDeclaredConstructor().newInstance();
        assertNotNull(obj);
    }
}
