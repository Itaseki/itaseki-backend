package com.example.backend.video.service;

import com.nimbusds.oauth2.sdk.util.date.SimpleDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProgramCrawler {

    public static void main(String[] args){

        System.out.println("------------------KBS-----------------");
        kbs();
        System.out.println("------------------SBS-----------------");
        sbs();
        System.out.println("------------------MBC-----------------");
        mbc(2); //2: 현재방송 / 3: 종영

    }


    private static List<String> sbs(){
        String url1="https://apis.sbs.co.kr/main-api/section/tv?pgm_sct=ET&sort=new&offset=";
        String url2="&limit=30";
        int offset=0;
        Document document = null;
        List<String> sbsProgram=new ArrayList<>();
        while(true){
            String url=url1+offset+url2;
            try {
                String html= Jsoup.connect(url).
                        ignoreContentType(true)
                        .execute().body();
                if(!html.contains("title"))
                    break;
                JSONArray array = new JSONArray(html);

                for(int i=0;i<array.length();i++){
                    JSONObject jsonObject = array.getJSONObject(i);
                    sbsProgram.add(jsonObject.get("title").toString());
                }

                offset+=30;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return sbsProgram;
    }

    private static List<String> mbc(int state){
        String url1="https://control.imbc.com/TV/Program?callback=Program_2_1_1_3_2_0_0_0_";
        String url2="&subCategoryId=2&curPage=";
        String url3="&pageSize=100&order=3&broadState="+state+"&endYear=0&initial=&genre=0";
        int page=1;
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        String date= format.format(new Date());
        Document document = null;
        List<String> mbcProgram=new ArrayList<>();
        while(true){
            String url=url1+date+url2+page+url3;
            try {
                String html= Jsoup.connect(url).
                        ignoreContentType(true)
                        .execute().body();

                if(!html.contains("Title"))
                    break;

                List<Integer> head = findIndexes("(", html);
                List<Integer> tail = findIndexes(")", html);

                String parsed=html.substring(head.get(0)+1,tail.get(tail.size()-1));
                JSONObject jsnobject = new JSONObject(parsed);

                JSONArray jsonArray = jsnobject.getJSONArray("List");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title=jsonObject.getString("Title");
                    System.out.println("title = " + title);
                    mbcProgram.add(title);
                }
                page++;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return mbcProgram;
    }

    private static List<String> kbs(){
        String url1="https://pprogramapi.kbs.co.kr/api/v1/external/program?end_yn=n&section_code=04&page=";
        String url2="&page_size=12&rtype=jsonp&show_yn=Y&sort_option=rdatetime%20desc&dict=Y&callback=section3";
        int page=1;
        Document document;
        List<String> kbsProgram=new ArrayList<>();
        while(true){
            String url=url1+page+url2;
            try {
                document = Jsoup.connect(url).get();
                Element body = document.body();
                String html = body.html();
                //title + 3 + 13 / program_day_of_week - 3
                List<Integer> title = findIndexes("program_title", html);
                List<Integer> program_day_of_week = findIndexes("official_sns_instagram", html);
                if(title.isEmpty())
                    break;
                for(int i=0;i<title.size();i++){
                    String programTitle=html.substring(title.get(i)+16, program_day_of_week.get(i)-3);
                    System.out.println("programTitle = " + programTitle);
                    kbsProgram.add(programTitle);
                }
                page++;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return kbsProgram;
    }


    private static List<Integer> findIndexes(String word, String document) {
        List<Integer> indexList = new ArrayList<>();
        int index = document.indexOf(word);
        while(index != -1) {
            indexList.add(index);
            index = document.indexOf(word, index+word.length());
        }
        return indexList;
    }
}
