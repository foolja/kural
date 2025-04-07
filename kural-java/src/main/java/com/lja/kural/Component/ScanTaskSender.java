package com.lja.kural.Component;

import com.lja.kural.Vo.PortScanTaskRequestVo;
import com.lja.kural.Vo.WeakPasswordScanTaskRequestVo;
import com.lja.kural.Vo.WebVulnScanTaskRequestVo;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class ScanTaskSender {

    public void sendTaskToGin(PortScanTaskRequestVo portScanTaskRequestVo) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 添加安全认证（例如API Key）
        headers.set("X-API-Key", "a3VyYWw=");
        HttpEntity<PortScanTaskRequestVo> entity = new HttpEntity<>(portScanTaskRequestVo, headers);

        // 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                "http://127.0.0.1:9999/api/v2/task/portScanTask",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("触发扫描失败: " + response.getBody());
        }
    }
    public void sendTaskToGin(WeakPasswordScanTaskRequestVo weakPasswordScanTaskRequestVo){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 添加安全认证（例如API Key）
        headers.set("X-API-Key", "a3VyYWw=");
        HttpEntity<WeakPasswordScanTaskRequestVo> entity = new HttpEntity<>(weakPasswordScanTaskRequestVo, headers);
        // 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                "http://127.0.0.1:9999/api/v2/task/weakPasswordTask",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("触发扫描失败: " + response.getBody());
        }
    }
    public void sendTaskToGin(WebVulnScanTaskRequestVo webVulnScanTaskRequestVo){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 添加安全认证（例如API Key）
        headers.set("X-API-Key", "a3VyYWw=");
        HttpEntity<WebVulnScanTaskRequestVo> entity = new HttpEntity<>(webVulnScanTaskRequestVo, headers);
        // 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                "http://127.0.0.1:9999/api/v2/task/webVulnScanTask",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("触发扫描失败: " + response.getBody());
        }
    }

}
