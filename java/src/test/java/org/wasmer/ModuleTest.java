package org.wasmer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.RuntimeException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class moduleTest {
    private byte[] getBytes(String filename) throws IOException,Exception {
        Path modulePath = Paths.get(getClass().getClassLoader().getResource(filename).getPath());
        System.out.println(modulePath);
        return Files.readAllBytes(modulePath);
    }

    @Test
    void validate() throws IOException,Exception {
        assertTrue(Module.validate(getBytes("tests.wasm")));
    }

    @Test
    void invalidate() throws IOException,Exception {
        assertFalse(Module.validate(getBytes("invalid.wasm")));
    }

    @Test
    void compile() throws IOException,Exception {
        assertTrue(new Module(getBytes("tests.wasm")) instanceof Module);
    }

    @Test
    void failedToCompile() throws IOException,Exception {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            Module module = new Module(getBytes("invalid.wasm"));
        });

        String expected = "Failed to compile the module: Validation error \"Invalid type\"";
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void instantiate() throws IOException,Exception {
        Module module = new Module(getBytes("tests.wasm"));

        Instance instance = module.instantiate();
        assertEquals(3, (Integer) instance.exports.get("sum").apply(1, 2)[0]);

        module.close();
    }
}