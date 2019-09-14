# DynoCom - Compile classes on the fly

<img src="https://github.com/elvinmahmudov/DynoCom/blob/master/logo.png" width="200"> 


<br/>

[![Build Status](https://travis-ci.org/elvinmahmudov/DynoCom.svg?branch=master)](https://travis-ci.org/elvinmahmudov/DynoCom)


### Getting Started
The aim of this project is to help developers compile source Java classes in memory on the fly.

### Installing
Step 1: Compile and add resulted jar file to your external libraries.

Step 2: compile string and load class:

```
public class Main {

    private static String MODEL_NAME = "com.elvinmahmudov.dynocom.MathTeacher";

    public static void main(String[] args) {
        var compiler = new JavaStringCompiler();
        var results = compiler.compile("Customer.java", SINGLE_CLASS);
        var clazz = compiler.loadClass(MODEL_NAME, results);
        
        var customer = (Teacher) clazz.newInstance();
    }

    static final String SINGLE_CLASS = "                                      "
            + "package com.elvinmahmudov.dynocom.model;                       "
            + "                                                               "
            + "import com.elvinmahmudov.dynocom.model.*;                      "
            + "import lombok.Data;                                            "
            + "                                                               "
            + "@Data                                                          "
            + "public class MathTeacher extends Teacher {                     "
            + "    String mainSubject = \"Math\";                             "
            + "}                                                              ";
}
```