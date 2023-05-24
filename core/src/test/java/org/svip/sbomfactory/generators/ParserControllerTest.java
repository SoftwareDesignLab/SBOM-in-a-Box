package org.svip.sbomfactory.generators;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;


import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author pliu
 */
public class ParserControllerTest {

    static String[] argv = {"src/test/java/org/svip/sbomfactory/generators/TestData/Conan", "-d"};

    //assertTrue(flag);

    final ParserController controller = new ParserController(argv[0]);

    @Test
    @DisplayName("Conan Parser Tests")
    void conanParserTest() {
        controller.setPWD(argv[0]);
        GeneratorsTestMain.main(argv);
    }
//
//
//    @Test
//    @DisplayName("Source Code Comment Test")
//    void srcCommentTest() {
//        argv[0] = "src/test/java/org/svip/sbomfactory/generators/TestData/Java";
//        controller.setPWD(Paths.get(argv[0]));
//        GeneratorsTestMain.main(argv);
//    }
//
//
//    @Test
//    @DisplayName("Sub Process Test")
//    void subProcessTest() throws XMLStreamException, FileNotFoundException {
//        argv[0] = "src/test/java/org/svip/sbomfactory/generators/TestData/subprocess.py";
//        argv[1] = "-d";
//        try {
//            GeneratorsTestMain.main(argv);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Disabled
//    @Test
//    @DisplayName("XML Comment Test")
//    void xmlCommentTest() throws XMLStreamException, FileNotFoundException {
//        //argv[0] = "src/test/java/org/svip/sbomfactory/generators/TestData/Java";
//        //controller.setPWD(Paths.get(argv[0]));
//        try {
//            //Main.xmlComment();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    @Test
//    @DisplayName("Parser Controller Java Test")
//    void javaTest() {
//        argv[0] = "src/test/java/org/svip/sbomfactory/generators/TestData/Java";
//        controller.setPWD(Paths.get(argv[0]));
//
//        System.out.println("controller.getProjectName() : " + controller.getProjectName());
//        System.out.println("controller.getPWD() : " + controller.getPWD());
//        System.out.println("controller.getSRC() : " + controller.getSRC());
//        //pliu System.out.println("controller.getOutputFileType() : " + controller.getOutputFileType());
//        System.out.println("controller.getSBOM() : " + controller.getSBOM());
//        System.out.println("Parser Controller Java test/argv : " + Arrays.toString(argv));
//        GeneratorsTestMain.main(argv);
//
//
//        String type = "json";
//        System.out.println("controller.setOutputFormat(" + type + ");" );
//        //pliu controller.setOutputFormat(type);
//
//        type = "yaml";
//        System.out.println("controller.setOutputFormat(" + type + ");" );
//        // pliu controller.setOutputFormat(type);
//
//    }
//
//    @Test
//    @DisplayName("parse() Test")
//    void parseTest() throws IOException {
//        //create a temp file with an unknown file extension
//        String fn = "src/test/java/org/svip/sbomfactory/generators/TestData/Java/afile.ext";
//        Path path = Paths.get(fn); //creates Path instance
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fn));
//        writer.close();
//        System.out.println("parseTest(null, null)" );
//        controller.parse(null, Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Java"));
//        Files.delete(path);
//
//    }
//
//    @Test
//    @DisplayName("getParent() Test")
//    void getParentTest() {
//        argv[0] = "/home/pliu/githubissues/170/Issue170Example:Issue170Example";
//        argv[1] = "-a=/home/pliu/NetBeansProjects/SBOM_WorkingGroup/BenchmarkParser/TestData/Java";
//        System.out.println("argv:" + Arrays.toString(argv) );
//        GeneratorsTestMain.main(argv);
//    }
//
//    @Test
//    @DisplayName("getExcepParent() Test")
//    void getParentExcepTest() {
//        String fn = "src/test/java/org/svip/sbomfactory/generators/TestData/Java";
//        System.out.println("getParent(" + fn + ")" );
//        //pliu controller.getParent(fn);
//    }
//
//
//
//    @Test
//    @DisplayName("OutputFormatIllegalArgumentException Test")
//    void OutputFormatIllegalArgumentExceptionTest() {
//        Calendar now = Calendar.getInstance();
//        System.out.println("Current time : " + now.get(Calendar.HOUR_OF_DAY)
//                + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
//
//        //IllegalArgumentException
//        String type = String.valueOf(now.get(Calendar.SECOND));
//        System.out.println("controller.setOutputFormat(" + type + ");" );
//        // pliu controller.setOutputFormat(type);
//
//    }
//
//    @Test
//    @DisplayName("toFile() Test")
//    void toFileTest() throws IOException {
//
//        String sepchar = "/";
//        String[] os = System.getProperty("os.name").split(" ");
//        if(os[0].equals("Windows"))  sepchar = "\\";    //System.out.println("Windows found");
//
//        String adir = "src/test/java/org/svip/sbomfactory/generators/TestData/Java";
//        String outdir = "src/test/java/org/svip/sbomfactory/generators/SBOMOut";
//
//        List<String> expectedFileNames = new ArrayList<>();
//
//        for(GeneratorSchema schema : GeneratorSchema.values()) {
//            for(GeneratorSchema.GeneratorFormat format : GeneratorSchema.GeneratorFormat.values()) {
//                if(!schema.supportsFormat(format)) continue;
//                System.out.printf("controller.toFile(%s, %s, %s)%n", outdir, schema, format);
//                controller.toFile(outdir, schema, format);
//                expectedFileNames.add("Java_" + schema + "." + format.getExtension());
//            }
//        }
//
//        List<File> files = Arrays.stream(new File(outdir).listFiles()).toList();
//        List<String> fileNames = files.stream().map(File::getName).toList();
//
//        for(String fileName : expectedFileNames) {
//            assertTrue(fileNames.contains(fileName));
//            System.out.println(fileName + " correctly generated.");
//            // TODO test file contents?
//        }
//
//        //remove all files in the variable outdir inclusively
//        for (File file : files) {
//            System.out.println("Files.delete(Paths.get(" + outdir + sepchar + file.getName() + ");");
//            Files.delete(Paths.get(outdir + sepchar + file.getName()));
//        }
//        System.out.println("Files.delete(Paths.get(" + outdir + ");");
//        Files.delete(Paths.get(outdir));
//    }
}