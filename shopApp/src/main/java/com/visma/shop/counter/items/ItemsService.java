package com.visma.shop.counter.items;

import com.item.ItemDTO;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@NoArgsConstructor
public class ItemsService {
    private RestTemplate restTemplate;
    @Value("${warehouse.url}")
    private String warehouseUrl;
    @Value("${authentication}")
    private String authentication;

    @PostConstruct
    public void init(){
        restTemplate = new RestTemplate();
    }

    public List<ItemDTO> getItems(){
        String url = warehouseUrl + "warehouseApp-0.0.1-SNAPSHOT/warehouse/items";
        ResponseEntity<List<ItemDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getAuthentication(),
                new ParameterizedTypeReference<>() {  } );

        List<ItemDTO> items = result.getBody();
        return items;
    }

    public String sellItem(int id, int amount){

        String url = warehouseUrl +
                "warehouseApp-0.0.1-SNAPSHOT/warehouse/items/"+ id +
                "/sell/" + amount;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getAuthentication(),
                String.class);

        String result = response.getBody();
        return result;
    }

    public HttpEntity<String> getAuthentication(){

        byte[] authToBytes = authentication.getBytes();
        byte[] base64Encode = Base64.encodeBase64(authToBytes);
        String authValue = new String(base64Encode);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(authValue);
        HttpEntity<String> requestHeaders = new HttpEntity<>(headers);

        return requestHeaders;
    }
}
