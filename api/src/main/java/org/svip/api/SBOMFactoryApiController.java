package org.svip.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.svip.sbomfactory.generators.ParserController;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/svip")
public class SBOMFactoryApiController {
    // TODO: Docstring
    @PostMapping("generateSBOM")
    public ResponseEntity<String> generate(@RequestParam("contents") String contentsArray, @RequestParam("fileNames") String fileArray, @RequestParam("schemaName") String schemaName, @RequestParam("formatName") String formatName) throws IOException {

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<String> fileContents = objectMapper.readValue(contentsArray, new TypeReference<>(){});
        final List<String> filePaths = objectMapper.readValue(fileArray, new TypeReference<>(){});

        final ParserController controller = new ParserController(null); // TODO: Get root directory and use it here

        for (int i = 0; i < filePaths.size(); i++) {
            final String path = filePaths.get(i);
            final String contents = fileContents.get(i);
//            controller.setPWD(path);
//            controller.parse(path, contents); // TODO: Fix
        }

//        // Parse the root directory with the controller
//        try (final Stream<String> stream = filePaths.stream()) {
//            stream.forEach(filepath -> {
//                try {
//                    // Set pwd to formatted filepath if it is actually a directory
//                    if (Files.isDirectory(filepath)) {
//                        controller.setPWD(filepath);
//                        controller.incrementDirCounter(); // TODO: Remove
//                    } else { // Otherwise, it is a file, try to parse
//                        controller.setPWD(filepath);
//                        controller.parse(filepath, fileContents); // TODO: Fix
//                    }
//                } catch (Exception e) {
////                    log(Debug.LOG_TYPE.EXCEPTION, e);
//                }
//            });
//        } catch (Exception e) {
////            log(Debug.LOG_TYPE.EXCEPTION, e);
//        }

        // Get schema from parameters, if not valid, default to CycloneDX
        GeneratorSchema schema = GeneratorSchema.CycloneDX;
        try { schema = GeneratorSchema.valueOfArgument(schemaName.toUpperCase()); }
        catch (IllegalArgumentException ignored) { }

        // Get format from parameters, if not valid, default to JSON
        GeneratorSchema.GeneratorFormat format = schema.getDefaultFormat();
        try { format = GeneratorSchema.GeneratorFormat.valueOf(formatName.toUpperCase()); }
        catch (IllegalArgumentException ignored) {
//            log(Debug.LOG_TYPE.WARN, String.format(
//                    "Invalid format type provided: '%s', defaulting to '%s'",
//                    optArgs.get("-f").toUpperCase(),
//                    format
//            ));
        }

        //encode and send report
        try {
            return new ResponseEntity<>(controller.toFile(null, schema, format), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
