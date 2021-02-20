package com.amar.crawler;

import com.amar.crawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner
{
	@Autowired
	private CrawlerService crawlerService;

	private static final String TEST_ENV_KEY = "testEnv";

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	@Override
	public void run(String... args)
	{
		if (StringUtils.hasLength(System.getProperty(TEST_ENV_KEY)) && System.getProperty(TEST_ENV_KEY).equalsIgnoreCase("y"))
		{
			return;
		}

		System.out.println("**************************************************");
		System.out.println("Starting the Email Crawler Application");
		System.out.println("**************************************************");
		System.out.println("\n");

		List<String> inputArgs = Arrays.asList(args);
		ApplicationHome home = new ApplicationHome(CrawlerApplication.class);

		int year = 2020;
		String location = home.getDir().getAbsolutePath();

		if (CollectionUtils.isEmpty(inputArgs))
		{
			System.out.println("No command line arguments found.\nInitializing default values...");
		}
		else
		{
			try
			{
				year = Integer.parseInt(inputArgs.get(0));
			}
			catch (Exception e)
			{
				System.out.println("Invalid value for year passed in the first argument.");
				System.exit(0);
			}

			if (inputArgs.size() > 1)
			{
				location = inputArgs.get(1);
			}
			else
			{
				System.out.println("Location value not passed in the second argument. Defaulting to current directory.");
			}
		}

		System.out.println("Configured values:\n");
		System.out.println("\tYear: " + year);
		System.out.println("\tLocation: " + location);
		System.out.println("\n");

		crawlerService.crawlEmails(year, location);
	}
}
