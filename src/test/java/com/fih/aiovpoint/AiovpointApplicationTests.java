package com.fih.aiovpoint;

import com.fih.aiovpoint.constant.ApiKeyConstant;
import com.fih.aiovpoint.pojo.AccessToken;
import com.fih.aiovpoint.service.AccountService;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
//@Transactional
public class AiovpointApplicationTests {
    @Autowired
    AccountService accountService;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mvc;

    private String Authorization;
    private final String Key= ApiKeyConstant.ServiceKey.Account.getValue();

    @Before
    public void setup() throws Exception{
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/v1/point/_generateSignature";
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",Key);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).headers(headers).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        System.out.println(result.getResponse().getContentAsString() );
        JSONArray r = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("result");
        AccessToken accessToken = accountService.getTokenForTest(r.getString(6), r.getString(3));
        Authorization = accessToken.getAccessToken();
        System.out.println("Authorization:"+Authorization );
        Assert.assertEquals("Http Response Error not ok",200,status);
    }

    @Test
    public void getPoint() throws Exception{
        String uri = "/api/v1/point/query";
        String[] generateSignature = accountService.getSignature();
        HttpHeaders headers = accountService.generateHeaders(generateSignature[6],generateSignature[3]);
        //headers.add("key",Key);
        headers.add("Authorization", Authorization);
        System.out.println(headers.toString());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).headers(headers).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        System.out.println(result.getResponse().getContentAsString());
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("$['errorCode']");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("$['message']");
        Integer totalPoint = JsonPath.parse(result.getResponse().getContentAsString()).read("$['result']['totalPoint']");

        Assert.assertTrue(totalPoint > 0);
        Assert.assertEquals(message, 0, errorCode);
    }

    @Test
    public void addPoint() throws Exception{
        String uri = "/api/v1/point/addRecord";
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",Key);
        JSONObject json = new JSONObject();
        json.put("userId", "F3C36C264C04498098FDBFFEA5C598E0");
        json.put("resourceType","A-01");
        json.put("resourceId", UUID.randomUUID().toString());
        json.put("description","test");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).headers(headers).contentType(MediaType.APPLICATION_JSON).content(json.toString()).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("errorCode");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("message");
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertEquals(message, 0, errorCode);
    }

    @Test
    public void exchangePoint() throws Exception{
        String uri = "/api/v1/point/exchangeGoods";
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",Key);
        JSONObject json = new JSONObject();
        json.put("userId", "F3C36C264C04498098FDBFFEA5C598E0");
        json.put("resourceType","D-02");
        json.put("resourceId", UUID.randomUUID().toString());
        json.put("description","exchange test");
        json.put("amount","-5");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).headers(headers).contentType(MediaType.APPLICATION_JSON).content(json.toString()).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("errorCode");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("message");
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertEquals(message, 0, errorCode);
    }

    @Test
    public void showList() throws Exception{
        String uri = "/api/v1/point/list?page=1&size=3";
        String[] generateSignature = accountService.getSignature();
        HttpHeaders headers = accountService.generateHeaders(generateSignature[6],generateSignature[3]);
        headers.add("Authorization", Authorization);
        System.out.println(headers.toString());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).headers(headers).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("errorCode");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("message");
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertEquals(message, 0, errorCode);
    }

    @Test
    public void showListByAccount() throws Exception{
        String uri = "/api/v1/point/listByAccount?account=F3C36C264C04498098FDBFFEA5C598E0&page=1&size=10";
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",Key);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).headers(headers).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("errorCode");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("message");
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertEquals(message, 0, errorCode);
    }

    @Test
    public void showPointByAccount() throws Exception{
        String uri = "/api/v1/point/queryByAccount";
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",Key);
        JSONObject json = new JSONObject();
        List<String > accountList = new ArrayList<>();
        accountList.add("8a4bf300aa484ad2b297cc4e2d5e58f9");
        accountList.add("F3C36C264C04498098FDBFFEA5C598E0");
        json.put("account", accountList);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri).headers(headers).contentType(MediaType.APPLICATION_JSON).content(json.toString()).accept(MediaType.APPLICATION_JSON)).andReturn();
        int status = result.getResponse().getStatus();
        Assert.assertEquals("Http Response Error", 200, status);
        int errorCode = JsonPath.parse(result.getResponse().getContentAsString()).read("errorCode");
        String message = JsonPath.parse(result.getResponse().getContentAsString()).read("message");
        System.out.println(result.getResponse().getContentAsString());
        Assert.assertEquals(message, 0, errorCode);
    }
}
