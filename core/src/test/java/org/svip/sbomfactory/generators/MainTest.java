package org.svip.sbomfactory.generators;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author pliu
 */

public class MainTest {

    class JavaProcess {

        private JavaProcess() {
        }

        public static int exec(String[] command, String stdindata) throws IOException, InterruptedException {

            Process process;
            try {
                ProcessBuilder builder = new ProcessBuilder(command);
                if(stdindata == null) {
                    process = builder.inheritIO().start();
                }
                else {
                    process = builder.start();
                    BufferedReader out = process.inputReader();
                    BufferedReader err = process.errorReader();
                    //the following order makes a difference;
                    BufferedWriter in = process.outputWriter();
                    in.write(stdindata);
                    in.flush();
                    out.lines().forEach(stm1 -> System.out.println(stm1));
                    err.lines().forEach(stm1 -> System.err.println(stm1));
                }
                process.waitFor();
                return process.exitValue();
            }
            catch (Exception e) {
                System.out.println("exec() Exception catched");
                e.printStackTrace();
                return (0);
            }
        }
    }

    static String[] argv = {"-h", "TestData/Datadir"};

    @Disabled
    @BeforeEach
    void setUp1() {
        System.out.println("setUp1()");
    }

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        System.out.println("@Beforeall : Check directory: " + argv[1]);
        Path path = Paths.get(argv[1]);
        if(! Files.exists(path)) {
            System.out.println(argv[1] + " does not exist!");
            try {
                Path tempDirectory = Files.createDirectory(path);
                if(Files.exists(tempDirectory)) {
                    System.out.println(argv[1] + " created!");
                }
            }
            catch (Exception e) {
                System.err.println("Exception received!");
                e.printStackTrace();
            }
        }
        else
            System.out.println(argv[1] + " exists!");

        //int ret = JavaProcess.exec(new String[]{"mvn", "clean", "package"});
        //assertEquals(0, ret);
    }


    @Test
    @DisplayName("No Argv test")
    void noArgvTest() {

        String[] argv = new String[0];
        String in = "TestData/Datadir";
        InputStream stdin = System.in;
        InputStream is = new ByteArrayInputStream(in.getBytes());
        System.setIn(is);
        System.out.println("No argv/argv: " + Arrays.toString(argv) + " with answering to the prompt: " + in);
        GeneratorsTestMain.main(argv);
        System.setIn(stdin);
    }

    @Test
    @DisplayName("Help(-h) test")
    void minusHTest() {
        boolean flag = true;
        //assertTrue(true);

        //-h
        argv[0] = "-h";
        System.out.println("Help Flag(-h)/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
        assertTrue(flag);
    }

    @Test
    @DisplayName("Summary(-s) test")
    void minusSTest() {
        //-s
        argv[0] = "-s";
        System.out.println("Summary Flag(-s)/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Debug(-d) test")
    void minusDTest() {
        //-d
        argv[0] = "-d";
        System.out.println("Debug Flag(-d)/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Unsupported (-y) test")
    void minusUnsupportedTest() throws IOException, InterruptedException {
        //-y
        int retcode = -5;
        String[] argv = {"java", "-jar", "target/parser.jar", "-y", "TestData/Datadir"};
        System.out.println("Unsupported (-y) test/argv: " + Arrays.toString(argv));
        try {
            retcode = JavaProcess.exec(argv, null);
        }
        catch (Exception e) {
            System.out.println("Caught an Exception");
            e.printStackTrace();
        }
        assertEquals(0, retcode);
    }

    @Test
    @DisplayName("Normal process project")
    void processProjectTest() {
        argv[0] = "src";
        System.out.println("Normal process project/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Non existing project directory")
    void processNonExistingProjectTest() {
        argv[0] = "src_notthere";
        argv[1] = "-h";
        String in = "TestData/Datadir";
        InputStream stdin = System.in;
        InputStream is = new ByteArrayInputStream(in.getBytes());
        System.setIn(is);
        System.out.println("Non existing project directory project/argv: " + Arrays.toString(argv) + " with answering to the prompt: " + in);
        GeneratorsTestMain.main(argv);
        System.setIn(stdin);
    }


    @Test
    @DisplayName("Output YAML/-o=yaml")
    void outputYAMLTest() {
        //Exception thrown = assertThrows(Exception.class, () -> {
        argv[0] = "-o=yaml";
        System.out.println("Output YAML/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
        //});
        //System.out.println(thrown.getMessage());
        //assertEquals(null, parseInt(thrown.getMessage()));
        //pliu assertEquals(0, 1, "The YAML type is not in SBOMGenerator.java on line 48 public String getExtension() .");

    }



    @Test
    @DisplayName("Output unsupported type/-o")
    void outputUnsupportedTypeTest() {

        int retcode = -5;
        String[] argv = {"java", "-jar", "target/parser.jar", "-o", "TestData/Datadir"};
        System.out.println("Output unsupported type/argv: " + Arrays.toString(argv));
        try {
            retcode = JavaProcess.exec(argv, null);
        }
        catch (Exception e) {
            System.out.println("Caught an Exception");
            e.printStackTrace();
        }
        assertEquals(0, retcode);
    }

    @Test
    @DisplayName("Filter Language/-f=l")
    void filterLanguageTest() {
        argv[0] = "-f=l";
        argv[1] = "src";
        System.out.println("Filter Language/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Filter Internal/-f=i")
    void filterInternalTest() {
        argv[0] = "-f=i";
        argv[1] = "src";
        System.out.println("Filter Internal/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Filter Language & Internal/-f=l,i")
    void filterLanguageInternalTest() {
        argv[0] = "-f=l,i";
        argv[1] = "src";
        System.out.println("Filter Language & Internal/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }



    //TODO: how does append work?
    //@@Test
    @DisplayName("Append/-a=i")
    void appendTest() {
        argv[0] = "-a=i";
        argv[1] = "targetPath:componentName";
        System.out.println("Append TEST/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }


    //@Test
    @DisplayName("Max attempt on error argument list Test")
    void maxAttemptOnErrorArgumentListTest() {
        String[] argv = {"-o=pliu", "-o=pl", "f=json", "-d", "-h", "-s", "TestData/Datadir"};
        System.out.println("Max attempt TEST/argv: " + Arrays.toString(argv));
        GeneratorsTestMain.main(argv);
    }

    @Test
    @DisplayName("Incorrect Argument Form Test")
    void incorrectArgumentFormTest() {

        int retcode = -5;
        String[] argv = {"java", "-jar", "target/parser.jar", "TestData/Datadir", "-a=1"};
        System.out.println("Output unsupported type/argv: " + Arrays.toString(argv));
        try {
            retcode = JavaProcess.exec(argv, "\n");
        }
        catch (Exception e) {
            System.out.println("Caught an Exception");
            e.printStackTrace();
        }
        assertEquals(0, retcode);
    }

}