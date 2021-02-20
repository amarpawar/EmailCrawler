# EmailCrawler

This simple crawler allowes you to crawl and download all the mails for specific year from https://mail-archives.apache.org/mod_mbox/maven-users/ 

Use following command to build the jar file.
> ./gradlew bootjar

This will generate the **crawler.jar** file at the location *crawler/build/libs*

You can pass two command line arguments while running the script - **Year** and **Location** to store the downloaded emails.
> java -jar crawler.jar 2020 /home/joe/Documents/emails

This will download all the emails for year 2020 and store them at the location /home/joe/Documents/emails. All emails will be stored in their respective month folders.
