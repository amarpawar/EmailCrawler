package com.amar.crawler.service.impl;

import com.amar.crawler.constant.CommonConstants;
import com.amar.crawler.service.CrawlerService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class CrawlerServiceImpl implements CrawlerService
{
    @Override
    public void crawlEmails(int year, String location) throws IOException
    {
        CloseableHttpClient httpClient = null;

        try
        {
            httpClient = HttpClients.createDefault();
            Path downloadPath = Paths.get(location).toAbsolutePath().normalize();

            downloadEmails(httpClient, year, downloadPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error while downloading emails: " + e.getMessage());
        }
        finally
        {
            if (httpClient != null)
            {
                httpClient.close();
            }
        }
    }

    private void downloadEmails(CloseableHttpClient httpClient, int year, Path downloadPath) throws Exception
    {
        Document document = Jsoup.parse(getResponse(httpClient, CommonConstants.URL));

        for (Element element : document.select(CommonConstants.TABLE))
        {
            if (element.getElementsByTag(CommonConstants.TH).first().text().equalsIgnoreCase(CommonConstants.YEAR + year))
            {
                Element tBody = element.getElementsByTag(CommonConstants.TBODY).first();

                for (Element trElement : tBody.getElementsByTag(CommonConstants.TR))
                {
                    String date = trElement.select(CommonConstants.TD_DATE).first().text();
                    String link = trElement.select(CommonConstants.TD_LINKS).first().getElementsByTag(CommonConstants.ANCHOR).first().attr(CommonConstants.HREF);
                    Path monthPath = downloadPath.resolve(date);

                    System.out.print("Downloading emails for " + date + "... ");
                    downloadMonthEmails(httpClient, link, monthPath);
                    System.out.println("done.");
                }

                System.out.println("\nAll emails downloaded for year " + year);
            }
        }
    }

    private void downloadMonthEmails(CloseableHttpClient httpClient, String uri, Path monthPath) throws Exception
    {
        String monthURL = CommonConstants.URL + uri.replace(CommonConstants.THREAD, CommonConstants.AUTHOR);
        Document monthDocument = Jsoup.parse(getResponse(httpClient, monthURL));

        if (Objects.nonNull(monthDocument))
        {
            Files.createDirectories(monthPath);
            Element msgTable = monthDocument.getElementById(CommonConstants.MSG_LIST);

            if (Objects.nonNull(msgTable))
            {
                Element msgTBody = msgTable.getElementsByTag(CommonConstants.TBODY).first();

                if (Objects.nonNull(msgTBody))
                {
                    for (Element trElement : msgTBody.getElementsByTag(CommonConstants.TR))
                    {
                        Element subjectTD = trElement.select(CommonConstants.TD_SUBJECT).first();
                        Element subjectAnchor = subjectTD.getElementsByTag(CommonConstants.ANCHOR).first();

                        if (Objects.nonNull(subjectAnchor))
                        {
                            String fileName = getValidFileName(trElement.select(CommonConstants.TD_AUTHOR).first().text() +
                                    CommonConstants.FILE_SEPARATOR + subjectTD.text() +
                                    trElement.select(CommonConstants.TD_DATE).first().text()) +
                                    CommonConstants.XML_EXT;
                            String email = getResponse(httpClient, monthURL.replace(CommonConstants.AUTHOR, CommonConstants.AJAX) + subjectAnchor.attr(CommonConstants.HREF));
                            Files.write(monthPath.resolve(fileName.replace(CommonConstants.DIR_SEPARATOR, CommonConstants.FILE_SEPARATOR)), email.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
            }
        }
    }

    private String getResponse(CloseableHttpClient httpClient, String url) throws Exception
    {
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet))
        {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String getValidFileName(String fileName)
    {
        return fileName.replaceAll("[/<>:\"\\\\|?*]", "_");
    }
}
