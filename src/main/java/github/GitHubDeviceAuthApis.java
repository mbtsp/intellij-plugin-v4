package github;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitHubDeviceAuthApis {
    private final String DEVICE_CODE_URL = "https://github.com/login/device/code";
    public static String token = "11AHTYGQY0EBe5ZXUiHLxq_XWqE76wibh0NnPByH1y9qxlF9ElwBDIrPHYgEEwX2TPLLJ5NZFCVtRsLolW";

    Map<String, String> heads = new HashMap<>();

    {
        heads.put("User-Agent", "issueAppRoot");
        heads.put("X-GitHub-Api-Version", "2022-11-28");
        heads.put("Authorization", String.format("Bearer github_pat_%s", token));
    }

    GitHubDeviceCode getDeviceCode(String clientId) throws Exception {
        HttpClient httpClient = new HttpClient();
        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        HttpClientResponse<GitHubDeviceCode> httpClientResponse = httpClient.post(DEVICE_CODE_URL, params, GitHubDeviceCode.class);
        if (httpClientResponse.isStatus()) {
            return httpClientResponse.getBody();
        }
        return null;
    }

    public IssueInfo.ItemsDTO findIssue(String id) throws Exception {
        String url = "https://api.github.com/search/issues";
        Map<String, String> params = new HashMap<>();
        params.put("q", "repo:mbtsp/Databae-Tool is:issue in:body " + id);
        params.put("page", "1");
        params.put("per_page", "per_page");
        HttpClient httpClient = new HttpClient(heads);
        HttpClientResponse<IssueInfo> httpClientResponse = httpClient.get(url, params, IssueInfo.class);
        if (httpClientResponse.isStatus()) {
            IssueInfo issueInfo = httpClientResponse.getBody();
            if (issueInfo != null && issueInfo.getTotalCount() != null && issueInfo.getTotalCount() != 0) {
                List<IssueInfo.ItemsDTO> itemsDTOS = issueInfo.getItems();
                if (itemsDTOS != null && !itemsDTOS.isEmpty()) {
                    return itemsDTOS.get(0);
                }
            }
        }
        return null;

    }

    public Issue issue(String title, String body) throws Exception {

        HttpClient httpClient = new HttpClient(heads);
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("body", body);
        List<String> labels = new ArrayList<>();
        labels.add("bug");
        map.put("labels", labels);
        HttpClientResponse<Issue> httpClientResponse = httpClient.post("https://api.github.com/repos/mbtsp/intellij-plugin-v4/issues", map, Issue.class);
        if (httpClientResponse.isStatus()) {
            return httpClientResponse.getBody();
        }
        return null;

    }


}
