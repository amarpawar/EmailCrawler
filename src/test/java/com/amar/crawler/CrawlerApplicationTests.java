package com.amar.crawler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amar.crawler.service.CrawlerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

@SpringBootTest
class CrawlerApplicationTests
{

	@Autowired
	private CrawlerService crawlerService;

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream standardOut = System.out;
	private static final String DIR = System.getProperty("user.dir") + "/emails";

	static
	{
		System.setProperty("testEnv", "y");
	}

	@BeforeEach
	public void setUp()
	{
		System.setOut(new PrintStream(outputStream));
	}

	@Test
	void checkEmailExists() throws Exception
	{
		int year = 2020;
		String month = "Dec 2020";
		String fileName = "Albert Pinto_How to run each TestNG suite in a separate process_Thu, 03 Dec, 08_29.xml";

		crawlerService.crawlEmails(year, DIR);

		Path basePath = Paths.get(DIR).toAbsolutePath().normalize();
		assertTrue(Files.exists(
				Paths.get(DIR).toAbsolutePath().normalize().resolve("Year " + year).resolve(month).resolve(fileName)
		));
		deletePath(basePath);
	}

	@Test
	void checkInvalidYear()
	{
		int year = 1997;
		String message = "No emails found for the year 1997";
		crawlerService.crawlEmails(year, DIR);
		Assertions.assertThat(outputStream.toString()).contains(message);
	}

	@AfterEach
	public void setDown()
	{
		System.setOut(standardOut);
	}

	private void deletePath(Path path) throws Exception
	{
		if (Files.exists(path))
		{
			try (Stream<Path> walk = Files.walk(path))
			{
				walk.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(
								file -> {
									file.delete();
								}
						);
			}
		}
	}
}
