package com.haiemdavang.AnrealShop.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.dto.shipping.*;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GHNServiceImp implements IGHNService {

    @Value("${ghn.shop_id}")
    private String shopId;
    @Value("${ghn.token}")
    private String token;

    private final String getServiceApi = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";
    private final String calculateFeeApi = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    private final String getExpectedDeliveryDateApi = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/leadtime";
//    private String getProvinceListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
//    private String getDistrictListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
//    private String getWardListApi = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public InfoShippingOrder getShippingOrderInfo(InfoShipment infoShipment) {
        List<ShipService> serviceIds = getServiceIdList(
                Integer.parseInt(infoShipment.from.getDistrictId()),
                Integer.parseInt(infoShipment.to.getDistrictId()));

        if (serviceIds.isEmpty() || (infoShipment.getWeight() > 20000 && serviceIds.size() < 2)) {
            return InfoShippingOrder.createFailedInfoShippingOrder();
        }
        int serviceId = infoShipment.getWeight() <= 20000 ? serviceIds.get(0).getService_id() : serviceIds.get(1).getService_type_id();
        int fee = getFee(serviceId,
                Integer.parseInt(infoShipment.from.getDistrictId()), Integer.parseInt(infoShipment.to.getDistrictId()),
                infoShipment.from.getWardId(), infoShipment.to.getWardId(),
                (int) infoShipment.getWeight());
        LocalDate expectedDeliveryDate = getExpectedDeliveryDate(serviceId,
                Integer.parseInt(infoShipment.from.getDistrictId()), Integer.parseInt(infoShipment.to.getDistrictId()),
                infoShipment.from.getWardId(), infoShipment.to.getWardId());

        return InfoShippingOrder.createSuccessInfoShippingOrder(fee, expectedDeliveryDate, serviceIds.get(0).getShort_name());
    }

    private HttpHeaders initHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        return headers;
    }

    private int getFee(int serviceId, int fromDistrictId, int toDistrictId,
                       String fromWardCode, String toWardCode, int weight) {
        Map<String, Object> body = new HashMap<>();
        body.put("service_id", serviceId);
        body.put("from_district_id", fromDistrictId);
        body.put("to_district_id", toDistrictId);
        body.put("from_ward_code", fromWardCode);
        body.put("to_ward_code", toWardCode);
        body.put("weight", weight);

        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, initHeader());
            ResponseEntity<String> response = restTemplate.postForEntity(calculateFeeApi,
                    requestEntity,
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                    GHNResponse<FeeData> data = objectMapper.readValue(
                            response.getBody(),
                            new TypeReference<GHNResponse<FeeData>>() {
                            });
                    return data.getData().getTotal();

            }
        } catch (Exception e) {
            throw new BadRequestException("ADDRESS_NOT_SUPPORT");
        }
        log.error("Can't fetch data api fee");
        return 0;
    }

    private LocalDate getExpectedDeliveryDate(int serviceId, int fromDistrictId, int toDistrictId, String fromWardCode, String toWardCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("service_id", serviceId);
        body.put("from_district_id", fromDistrictId);
        body.put("to_district_id", toDistrictId);
        body.put("from_ward_code", fromWardCode);
        body.put("to_ward_code", toWardCode);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, initHeader());
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(getExpectedDeliveryDateApi,
                    requestEntity,
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    GHNResponse<LeadTime> data = objectMapper.readValue(response.getBody(), new TypeReference<GHNResponse<LeadTime>>() {
                    });
                    return Instant.ofEpochSecond(data.getData().getLeadtime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } catch (JsonProcessingException e) {
                    log.error("Can't convert object from get lead time api ghn to dto");
                    return LocalDate.now();
                }
            }
        }catch (Exception e) {
            throw new AnrealShopException("GHN_NOT_RESPONSE");
        }

        log.error("Can't fetch data api lead time");
        return LocalDate.now();
    }

    private List<ShipService> getServiceIdList(int fromDistrict, int toDistrict) {
        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", Integer.parseInt(shopId));
        body.put("from_district", fromDistrict);
        body.put("to_district", toDistrict);

        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, initHeader());
            ResponseEntity<String> response = restTemplate.postForEntity(getServiceApi,
                    requestEntity,
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    GHNResponse<List<ShipService>> data = objectMapper.readValue(response.getBody(), new TypeReference<GHNResponse<List<ShipService>>>() {
                    });
                    return data.getData();
                } catch (JsonProcessingException e) {
                    log.error("Can't convert object from get list service ghn to dto");
                    return new ArrayList<>();
                }
            }
        }catch (Exception e) {
            throw new AnrealShopException("GHN_NOT_RESPONSE");
        }
        log.error("Can't fetch data get list service");
        return new ArrayList<>();
    }



}
