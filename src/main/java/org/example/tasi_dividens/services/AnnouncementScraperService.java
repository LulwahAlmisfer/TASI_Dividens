package org.example.tasi_dividens.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementScraperService {

    private static final String BASE_URL = "https://www.saudiexchange.sa/";

    private static final String COOKIE = "BIGipServerSaudiExchange.sa.app~SaudiExchange.sa_pool=2617184684.20480.0000; JSESSIONID=!T/KWLqnyb+hRhzFLU5wxdB6jNS4wI1h6gL8dEca/RT5yojdafmafYaQyUZAa5Ous1cgp/eW2YtTV7HJ07C0o5691M7pPiPu/4ZtO; TS01fdeb15=0102d17fad9163be63a2f5162048018b68c9a75ede5804b4c703f2b20cab9ea732424d6b8cd7c2e79388a95876d0580d82119caadb8595344feca10e4c0ed1c58870438a01328bbd1e5204f2d3c5280dcb23042628b14649af5a29ab934467b72ea9f0653d; com.ibm.wps.state.preprocessors.locale.LanguageCookie=ar";

    public static Map<String, Object> scrapeAnnouncement(String relativeUrl) throws IOException {
        String fullUrl = BASE_URL + relativeUrl;

        Document doc = Jsoup.connect(fullUrl)
                .userAgent("Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ar,en;q=0.9")
                .header("Referer", "https://www.saudiexchange.sa/")
                .header("Cookie", COOKIE)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get();

        Map<String, Object> result = new HashMap<>();
        result.put("url", fullUrl);

        Element table = doc.selectFirst("table");
        if (table == null) {
            result.put("announcement", "Table not found");
            return result;
        }

        // mapping for name i will only show data in arabic
        Map<String, String> arabicToEnglish = new HashMap<>();
        arabicToEnglish.put("مقدمة", "introduction");
        arabicToEnglish.put("مدينة و مكان انعقاد الجمعية العامة", "meetingLocation");
        arabicToEnglish.put("رابط مقر الاجتماع", "meetingLink");
        arabicToEnglish.put("تاريخ انعقاد الجمعية العامة", "meetingDate");
        arabicToEnglish.put("وقت انعقاد الجمعية العامة", "meetingTime");
        arabicToEnglish.put("كيفية انعقاد الجمعية العامة", "meetingMethod");
        arabicToEnglish.put("حق الحضور، وأحقية التسجيل، ونهاية التصويت", "attendanceAndVoting");
        arabicToEnglish.put("النصاب اللازم لانعقاد الجمعية", "requiredQuorum");
        arabicToEnglish.put("جدول أعمال الجمعية", "agenda");
        arabicToEnglish.put("نموذج التوكيل", "proxyForm");
        arabicToEnglish.put("حق المساهم في مناقشة الموضوعات المدرجة على جدول أعمال الجمعية، وتوجيه الأسئلة، وكيفية ممارسة حق التصويت", "shareholderRights");
        arabicToEnglish.put("تفاصيل خاصية التصويت الإلكتروني على بنود الجمعية", "electronicVotingDetails");
        arabicToEnglish.put("طريقة التواصل في حال وجود أي استفسارات", "contactInfo");
        arabicToEnglish.put("الملفات الملحقة", "attachedFiles");

        Map<String, Object> announcementData = new HashMap<>();
        Elements rows = table.select("tbody tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() == 2) {
                String arabicKey = cols.get(0).text().trim();
                String englishKey = arabicToEnglish.get(arabicKey);
                if (englishKey == null) continue;

                if (englishKey.equals("attachedFiles")) {

                    Elements links = cols.get(1).select("a");
                    List<String> files = new ArrayList<>();
                    for (Element link : links) {
                        String href = link.attr("href");
                        files.add(BASE_URL + href);
                    }
                    announcementData.put(englishKey, files);
                } else {
                    String value = cols.get(1).text().trim();
                    announcementData.put(englishKey, value);
                }
            }
        }

        result.put("announcement", announcementData);
        return result;
    }



}
