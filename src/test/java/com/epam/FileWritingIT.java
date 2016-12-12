package com.epam;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FileWritingIT {

	@Test
	public void shouldWriteFile() {

		File dataFile = new File("target/write-file.txt");
		FileWriter fileWriter = null;

		try {
			Files.deleteIfExists(dataFile.toPath());
			Files.createFile(dataFile.toPath());

			fileWriter = new FileWriter(dataFile);
			fileWriter.write("Hello this is an integration test.");
			fileWriter.flush();

			MatcherAssert.assertThat(dataFile.length(), Matchers.greaterThan(0l));

		} catch (IOException e) {
			System.out.println("Error when testing fileWriting functionality...");
			e.printStackTrace();

		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					System.out.println("Error closing fileWriter...");
					e.printStackTrace();
				}
			}
		}

	}

}
