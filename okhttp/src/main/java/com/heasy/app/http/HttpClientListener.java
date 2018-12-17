package com.heasy.app.http;

import okhttp3.Response;

public interface HttpClientListener {
	void onReponse(Response response);
}
