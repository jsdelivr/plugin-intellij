package com.jsdelivr.pluginintellij.packagename;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchConfig;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.Query;
import com.algolia.search.models.indexing.SearchResult;
import com.jsdelivr.pluginintellij.packagename.remotetypes.AlgoliaPackage;

import java.util.Collections;
import java.util.List;

public class AlgoliaSearch {
	private static final String appId = "OFCNCOG2CU";
	private static final String apiKey = "f54e21fa3a2a0160595bb058179bfb1e";
	private static final String indexName = "npm-search";

	private SearchIndex<AlgoliaPackage> index;

	public AlgoliaSearch() {
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
		SearchConfig config = new SearchConfig.Builder(appId, apiKey).build();
		SearchClient client = DefaultSearchClient.create(config);
		index = client.initIndex(indexName, AlgoliaPackage.class);
	}

	List<AlgoliaPackage> search(String packageName) {
		try {
			SearchResult<AlgoliaPackage> result = index.search(new Query(packageName));
			return result.getHits();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}
}
