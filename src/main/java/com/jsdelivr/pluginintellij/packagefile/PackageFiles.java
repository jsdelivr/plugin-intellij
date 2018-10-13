package com.jsdelivr.pluginintellij.packagefile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiFilesError;
import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiPackageFiles;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

class PackageFiles {
	static ApiPackageFiles pkgFiles;
	static ApiFilesError error;

	private static final String jsDelivrPackageFilesEndpoint = "https://data.jsdelivr.com/v1/package/npm/";
	private static final String userAgentHeader = "jsDelivr intelliJ plugin/" + PackageFiles.class.getPackage().getImplementationVersion() + " (https://github.com/jsdelivr/plugin-intellij)";

	static ApiPackageFiles getFiles(String packageName, String packageVersion) {
		String response;

		try {
			HttpClient client = HttpClients.custom().setUserAgent(userAgentHeader).build();
			HttpGet httpGet = new HttpGet();
			httpGet.setURI(new URI(jsDelivrPackageFilesEndpoint + packageName + "@" + packageVersion + "/flat"));
			HttpResponse resp = client.execute(httpGet);

			response = EntityUtils.toString(resp.getEntity());

			if (resp.getStatusLine().getStatusCode() == 200) {
				return new ObjectMapper().readValue(response, ApiPackageFiles.class);
			} else if (resp.getStatusLine().getStatusCode() == 403) {
				ApiFilesError err = new ApiFilesError();
				err.status = 0;
				err.message = "Package size exceeded the limit of 50 MB";
				error = err;
			} else if (resp.getStatusLine().getStatusCode() == 404) {
				error = new ObjectMapper().readValue(response, ApiFilesError.class);
			} else {
				ApiFilesError err = new ApiFilesError();
				err.status = 0;
				err.message = "Unknown API error occurred";
				error = err;
			}
		} catch (Exception exception) {
			pkgFiles = null;
			error = null;
		}

		return null;
	}
}
