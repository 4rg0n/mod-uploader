package com.github.argon.moduploader.core.file;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Simple service for handling file operations.
 */
@Slf4j
@ApplicationScoped
public class FileService extends AbstractFileService {
    public final static Charset CHARSET = StandardCharsets.UTF_8;

    public List<String> readLines(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Reading lines from file {}", absolutePath);

        if (!Files.exists(absolutePath)) {
            // do not load what's not there
            log.info("{} is not a folder, does not exists or is not readable", absolutePath);
            return List.of();
        }

        try (InputStream inputStream = Files.newInputStream(absolutePath)) {
            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file {}", absolutePath, e);
            throw e;
        }
    }

    /**
     * @return content of the file as string or null if the file does not exist
     * @throws IOException if something goes wrong when reading the file
     */
    @Nullable
    public String read(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Reading from file {}", absolutePath);

        if (!Files.exists(absolutePath)) {
            // do not load what's not there
            log.info("{} is not a file, does not exists or is not readable", absolutePath);
            return null;
        }

        try (InputStream inputStream = Files.newInputStream(absolutePath)) {
            return readFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file {}", absolutePath, e);
            throw e;
        }
    }

    /**
     * Writes content into a file. Will create the file if it does not exist.
     *
     * @throws IOException if something goes wrong when writing the file
     */
    public void write(Path path, String content) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Writing into file {}", absolutePath);
        File parentDirectory = absolutePath.getParent().toFile();

        if (!parentDirectory.exists()) {
            try {
                Files.createDirectories(absolutePath.getParent());
            } catch (Exception e) {
                log.error("Could not create directories for {}", absolutePath);
                throw e;
            }
        }

        try {
            Files.writeString(absolutePath, content, CHARSET, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Could not write into {}", absolutePath);
            throw e;
        }
    }

    /**
     * @return whether the file is present anymore
     * @throws IOException if something goes wrong when deleting the file
     */
    public boolean delete(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Deleting file {}", absolutePath);
        try {
            Files.delete(absolutePath);
        } catch (NoSuchFileException e) {
            return true;
        } catch (Exception e) {
            log.error("Could not delete file {}", absolutePath, e);
            throw e;
        }

        return true;
    }
}
