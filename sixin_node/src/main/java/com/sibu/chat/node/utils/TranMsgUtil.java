package com.sibu.chat.node.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Code;

public class TranMsgUtil {

	/**
	 * 发送http请求,注意，notice不能以\0结尾
	 * @param address 节点ip:port
	 * @param userId 接收人id
	 * @param msg 要发送的消息
	 * @param needClose 是否需要关闭连接 true 需要  false 不需要
	 */
	public static void tran(String address, int userId, String notice, boolean needClose) {
		String url = "http://" + address + "/tran";
		HttpPost post = new HttpPost(url);

		HttpEntity entity = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", "" + userId));
		params.add(new BasicNameValuePair("notice", notice));
		params.add(new BasicNameValuePair("needClose", Boolean.toString(needClose)));
		try {
			entity = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		CloseableHttpResponse response = null;
		try {
			try {
				response = HttpClientUtil.CLIENT.execute(post);
				JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
				if (result.getIntValue("code") != Code._100.getCode()) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				throw new RuntimeException("消息转发失败");
			}
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
