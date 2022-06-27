package com.example.backend.kakaoSocialLogin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OAuthService{

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=53ab9d9beaf347ecc3d5779342ef3562"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:8080/oauth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
//            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

//            System.out.println("access_token : " + access_Token);
//            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public JsonNode getKakaoUserInfo(String autorize_code) {

        final String RequestUrl = "https://kapi.kakao.com/v2/user/me";
        //String CLIENT_ID = K_CLIENT_ID; // REST API KEY
        //String REDIRECT_URI = K_REDIRECT_URI; // 리다이렉트 URI
        //String code = autorize_code; // 로그인 과정중 얻은 토큰 값
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);
        String accessToken = getKakaoAccessToken(autorize_code);
        // add header
        post.addHeader("Authorization", "Bearer " + accessToken);

        JsonNode returnNode = null;

        try {

            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();
//            System.out.println("\nSending 'POST' request to URL : " + RequestUrl);
//            System.out.println("Response Code : " + responseCode);

            // JSON 형태 반환값 처리
            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            // clear resources
        }
//        System.out.println(returnNode);
        return returnNode;
    }
}
