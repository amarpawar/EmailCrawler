package com.amar.crawler.service;

import java.io.IOException;

public interface CrawlerService
{
    void crawlEmails(int year, String location) throws IOException;
}
