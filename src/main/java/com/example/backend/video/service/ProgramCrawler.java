package com.example.backend.video.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ProgramCrawler {

    public static void main(String[] args) {
//        System.out.println("------------------KBS-----------------");
//        kbs().forEach(System.out::println);
//        System.out.println("------------------SBS-----------------");
//        sbs().forEach(System.out::println);
//        System.out.println("------------------MBC-----------------");
//        mbc(2).forEach(System.out::println); //2: 현재방송 / 3: 종영
//        mbc(3).forEach(System.out::println);
        System.out.println("------------------TVN-----------------");
        tvn().forEach(System.out::println);
    }

    @Scheduled
    private static void updatePrograms() {
        // mbc 종영 예능은 한 번만 실행, 비동기 처리로
    }

    private static void jtbc() { //selenium 사용
        String url = "https://tv.jtbc.co.kr/tv/program-list-more";
        while (true) {
            try {
                Document document = Jsoup.connect(url).ignoreContentType(true).post();
                System.out.println(document.body().toString());
            } catch (IOException exception) {
                break;
            }

        }
    }

    private static List<String> tvn() {
        String baseUrl = "http://tvn.tving.com/tvn/Program?page=%d&onair=A&code=%s&order=";
        String entertainmentCategory = "CAT002";
        String digitalCategory = "CAT011";
        int page = 1;
        String category = entertainmentCategory;

        List<String> tvnProgram = new ArrayList<>();
        while (true) {
            try {
                String url = String.format(baseUrl, page, category);
                Element body = Jsoup.connect(url).
                        ignoreContentType(true)
                        .get().body();

                Elements datas = body.getElementsByClass("title");
                if (datas.isEmpty()) {
                    if (category.equals(digitalCategory)) {
                        break;
                    }
                    category = digitalCategory;
                    page = 1;
                    continue;
                }

                datas.stream()
                        .map(Element::text)
                        .forEach(tvnProgram::add);
                page++;

            } catch (IOException e) {
                break;
            }
        }
        return tvnProgram;
    }


    private static List<String> sbs() {
        String url1 = "https://apis.sbs.co.kr/main-api/section/tv?pgm_sct=ET&sort=new&offset=";
        String url2 = "&limit=30";
        int offset = 0;

        List<String> sbsProgram = new ArrayList<>();
        while (true) {
            String url = url1 + offset + url2;
            try {
                String html = Jsoup.connect(url).
                        ignoreContentType(true)
                        .execute().body();

                if (!html.contains("title")) {
                    break;
                }

                JSONArray array = new JSONArray(html);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    sbsProgram.add(jsonObject.get("title").toString());
                }

                offset += 30;

            } catch (IOException e) {
                break;
            }
        }
        return sbsProgram;
    }

    private static List<String> mbc(int state) {
        String url1 = "https://control.imbc.com/TV/Program?callback=Program_2_1_1_3_2_0_0_0_";
        String url2 = "&subCategoryId=2&curPage=";
        String url3 = "&pageSize=100&order=3&broadState=" + state + "&endYear=0&initial=&genre=0";
        int page = 1;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date()); //오늘 날짜 기준으로 예능 조회

        List<String> mbcProgram = new ArrayList<>();
        while (true) {
            String url = url1 + date + url2 + page + url3;

            try {
                String html = Jsoup.connect(url).
                        ignoreContentType(true)
                        .execute().body();

                if (!html.contains("Title")) {
                    break;
                }

                List<Integer> head = findIndexes("(", html);
                List<Integer> tail = findIndexes(")", html); //string 형태의 데이터에서 () 내에 들어있는 데이터들만 찾아온다

                String parsed = html.substring(head.get(0) + 1, tail.get(tail.size() - 1));
                JSONObject jsonObject = new JSONObject(parsed);

                JSONArray jsonArray = jsonObject.getJSONArray("List");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("Title");
                    mbcProgram.add(title);
                }
                page++;

            } catch (IOException e) {
                break;
            }
        }
        return mbcProgram;
    }

    private static List<String> kbs() {
        String url1 = "https://pprogramapi.kbs.co.kr/api/v1/external/program?end_yn=n&section_code=04&page=";
        String url2 = "&page_size=12&rtype=jsonp&show_yn=Y&sort_option=rdatetime%20desc&dict=Y&callback=section3";
        int page = 1;
        Document document;
        List<String> kbsProgram = new ArrayList<>();
        while (true) {
            String url = url1 + page + url2;
            try {
                document = Jsoup.connect(url).get();
                Element body = document.body();
                String html = body.html();
                //title + 3 + 13 / programDayOfWeek - 3  -> 정확한 제목이 있는 위치
                List<Integer> title = findIndexes("program_title", html);
                List<Integer> programDayOfWeek = findIndexes("official_sns_instagram", html);
                if (title.isEmpty()) {
                    break;
                }
                for (int i = 0; i < title.size(); i++) {
                    String programTitle = html.substring(title.get(i) + 16, programDayOfWeek.get(i) - 3);
                    kbsProgram.add(programTitle);
                }
                page++;

            } catch (IOException e) {
                break;
            }
        }
        return kbsProgram;
    }


    private static List<Integer> findIndexes(String word, String document) {
        List<Integer> indexList = new ArrayList<>();
        int index = document.indexOf(word);
        while (index != -1) {
            indexList.add(index);
            index = document.indexOf(word, index + word.length());
        }
        return indexList;
    }
}
