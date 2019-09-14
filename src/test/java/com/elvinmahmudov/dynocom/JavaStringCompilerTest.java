package com.elvinmahmudov.dynocom;

import com.elvinmahmudov.dynocom.model.BeanProxy;
import com.elvinmahmudov.dynocom.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaStringCompilerTest {

    static final String SINGLE_CLASS = "/* a single java class to one file */  "
            + "package com.elvinmahmudov.dynocom.model;                                            "
            + "import com.elvinmahmudov.dynocom.*;                            "
            + "public class UserProxy extends User implements BeanProxy {     "
            + "    boolean _dirty = false;                                    "
            + "    public void setId(String id) {                             "
            + "        super.setId(id);                                       "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setName(String name) {                         "
            + "        super.setName(name);                                   "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setCreated(long created) {                     "
            + "        super.setCreated(created);                             "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setDirty(boolean dirty) {                      "
            + "        this._dirty = dirty;                                   "
            + "    }                                                          "
            + "    public boolean isDirty() {                                 "
            + "        return this._dirty;                                    "
            + "    }                                                          "
            + "}                                                              ";
    static final String MULTIPLE_CLASSES = "/* a single class to many files */   "
            + "package com.elvinmahmudov.dynocom.model;                                            "
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
        Map<String, byte[]> results = compiler.compile("UserProxy.java", SINGLE_CLASS);
        assertEquals(1, results.size());
        assertTrue(results.containsKey("com.elvinmahmudov.dynocom.model.UserProxy"));
        Class<?> clazz = compiler.loadClass("com.elvinmahmudov.dynocom.model.UserProxy", results);
        // get method:
        Method setId = clazz.getMethod("setId", String.class);
        Method setName = clazz.getMethod("setName", String.class);
        Method setCreated = clazz.getMethod("setCreated", long.class);
        // try instance:
        Object obj = clazz.getDeclaredConstructor().newInstance();
        // get as proxy:
        BeanProxy proxy = (BeanProxy) obj;
        assertFalse(proxy.isDirty());
        // set:
        setId.invoke(obj, "A-123");
        setName.invoke(obj, "Fly");
        setCreated.invoke(obj, 123000999);
        // get as user:
        User user = (User) obj;
        assertEquals("A-123", user.getId());
        assertEquals("Fly", user.getName());
        assertEquals(123000999, user.getCreated());
        assertTrue(proxy.isDirty());
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
