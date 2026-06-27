package com.haiemdavang.AnrealShop.schedule;

import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import com.haiemdavang.AnrealShop.service.serviceInter.IShipmentService;
import com.haiemdavang.AnrealShop.tech.kafka.dto.EmailMessageDto;
import com.haiemdavang.AnrealShop.tech.kafka.dto.notice.NoticeTemplate;
import com.haiemdavang.AnrealShop.tech.kafka.producer.EmailKafkaProducer;
import com.haiemdavang.AnrealShop.tech.kafka.producer.NoticeKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSchedule {
    private final IShopOrderService orderService;
    private final IShipmentService shipmentService;
    private final EmailKafkaProducer emailKafkaProducer;
    private final NoticeKafkaProducer noticeKafkaProducer;


    @Scheduled(cron = "0 0/1 * * * ?")
    public void shipperGiveOrder() {
        log.info("Running shipperGiveOrder schedule task");
        List<String> shopOrderIds = orderService.confirmOrders(ShopOrderStatus.PREPARING, ShopOrderStatus.SHIPPING);
        if (shopOrderIds != null && !shopOrderIds.isEmpty()) {
            shipmentService.updateShipmentStatus(shopOrderIds, ShippingStatus.PICKED_UP, ShippingTemplateStringNote.PICKED_UP_NOTES);
            emailKafkaProducer.sendEmailSyncMessage(EmailMessageDto.buildMailOrderPickedUp(new HashSet<>(shopOrderIds)));
            shopOrderIds.stream().map(NoticeTemplate::buildNoticeShopOrderPickedUp).forEach(noticeKafkaProducer::sendNoticeSyncMessage);
        }

    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void shipperUpdateStatusShip() {
        log.info("Running shipperUpdateStatusShip schedule task");
        List<Shipping> shippings = shipmentService.getListShippingByShopOrderStatus(ShopOrderStatus.SHIPPING);
        if (shippings.isEmpty()) {
            return;
        }
        for (Shipping shipping : shippings) {
            String id = shipping.getId();
            switch (shipping.getStatus()) {
                case PICKED_UP:
                    String note = ShippingTemplateStringNote.IN_TRANSIT_NOTES.get(0);
//                    shippingStatusKafkaProducer.sendSyncMessage(ShippingSyncMessage.from(id, ShippingStatus.IN_TRANSIT, note));
                    shipmentService.processShippingStatusSync(id, ShippingStatus.IN_TRANSIT, note);
                    break;
                case IN_TRANSIT:
                    for (int i = 1; i < ShippingTemplateStringNote.IN_TRANSIT_NOTES.size(); i++) {
                        String transitNote = ShippingTemplateStringNote.IN_TRANSIT_NOTES.get(i);
//                        shippingStatusKafkaProducer.sendSyncMessage(ShippingSyncMessage.from(id, ShippingStatus.IN_TRANSIT, transitNote));
                        shipmentService.processShippingStatusSync(id, ShippingStatus.IN_TRANSIT, transitNote);
                        if (i == ShippingTemplateStringNote.IN_TRANSIT_NOTES.size() - 1) {
                            String outForDeliveryNote = ShippingTemplateStringNote.OUT_FOR_DELIVERY_NOTES.get(0);
//                            shippingStatusKafkaProducer.sendSyncMessage(ShippingSyncMessage.from(id, ShippingStatus.OUT_FOR_DELIVERY, outForDeliveryNote));
                            shipmentService.processShippingStatusSync(id, ShippingStatus.OUT_FOR_DELIVERY, outForDeliveryNote);
                            String shopOrderId = shipping.getShopOrder().getId();
                            emailKafkaProducer.sendEmailSyncMessage(EmailMessageDto.buildMailOrderOutForDelivery(shopOrderId));
                            noticeKafkaProducer.sendNoticeSyncMessage(NoticeTemplate.buildNoticeShopOrderOutForDelivery(shopOrderId));
                        }
                    }
                    break;
                case OUT_FOR_DELIVERY:
                    for (int i = 1; i < ShippingTemplateStringNote.OUT_FOR_DELIVERY_NOTES.size(); i++) {
                        String deliveryNote = ShippingTemplateStringNote.OUT_FOR_DELIVERY_NOTES.get(i);
//                        shippingStatusKafkaProducer.sendSyncMessage(ShippingSyncMessage.from(id, ShippingStatus.OUT_FOR_DELIVERY, deliveryNote));
                        shipmentService.processShippingStatusSync(id, ShippingStatus.OUT_FOR_DELIVERY, deliveryNote);
                        if (i == ShippingTemplateStringNote.OUT_FOR_DELIVERY_NOTES.size() - 1) {
                            String deliveredNote = ShippingTemplateStringNote.DELIVERED_NOTES;
//                            shippingStatusKafkaProducer.sendSyncMessage(ShippingSyncMessage.from(id, ShippingStatus.DELIVERED, deliveredNote));
                            shipmentService.processShippingStatusSync(id, ShippingStatus.DELIVERED, deliveredNote);
                        }
                    }
                    break;
            }
        }
    }
}
