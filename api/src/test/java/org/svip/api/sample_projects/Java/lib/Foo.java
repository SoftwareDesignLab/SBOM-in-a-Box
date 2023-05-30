package org.svip.api.sample_projects.Java.lib;

/**
 * Long Comment
 */

// Live Import
import java.io.IOException;
import java.util.ArrayList;

// Dead Import


public class Foo {

    private ArrayList<String> test;
    public Foo() {
        this.test = new ArrayList<String>();
    }

    public void callProcess() throws IOException {
        //Process Call Comment
        Process test = new ProcessBuilder("C:\\PathToExe\\MyExe.exe","param1","param2").start();
    }

}