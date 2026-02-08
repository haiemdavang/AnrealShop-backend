package com.haiemdavang.AnrealShop.service.chat;

import com.haiemdavang.AnrealShop.dto.chat.*;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.mapper.ChatMapper;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatMessage;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatRoom;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;
import com.haiemdavang.AnrealShop.repository.ShopRepository;
import com.haiemdavang.AnrealShop.repository.chat.ChatMessageRepository;
import com.haiemdavang.AnrealShop.repository.chat.ChatRoomRepository;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final SecurityUtils securityUtils;
    private final ChatMapper chatMapper;

    // ==================== REST API Methods ====================

    /**
     * GET /api/chat/rooms - Lấy danh sách phòng chat của người dùng hiện tại
     * User có thể là customer hoặc shop owner
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getCurrentUserRooms() {
        User currentUser = securityUtils.getCurrentUser();
        String currentUserId = currentUser.getId();

        // Lấy tất cả phòng chat mà user tham gia (là customer hoặc shop owner)
        List<ChatRoom> rooms = chatRoomRepository.findRoomsByUserId(currentUserId);

        return rooms.stream()
                .map(room -> {
                    // Xác định vai trò của user trong phòng chat
                    SenderRole myRole = isShopOwner(room, currentUserId) ? SenderRole.SHOP : SenderRole.USER;

                    // Lấy tin nhắn cuối cùng
                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);

                    // Đếm tin nhắn chưa đọc
                    int unreadCount = chatMessageRepository.countUnreadMessages(room.getId(), myRole);

                    // Tạo response dựa theo vai trò
                    if (myRole == SenderRole.SHOP) {
                        return chatMapper.toRoomResponseForShop(room, lastMessage, unreadCount);
                    } else {
                        return chatMapper.toRoomResponseForCustomer(room, lastMessage, unreadCount);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * GET /api/chat/rooms/{roomId}/messages - Lấy lịch sử tin nhắn với phân trang
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getRoomMessages(String roomId, int page, int size) {
        User currentUser = securityUtils.getCurrentUser();
        String currentUserId = currentUser.getId();

        // Kiểm tra user có quyền truy cập phòng chat này không
        ChatRoom room = validateAndGetRoomAccess(roomId, currentUserId);

        // Xác định vai trò của user
        SenderRole myRole = isShopOwner(room, currentUserId) ? SenderRole.SHOP : SenderRole.USER;

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);

        return messages.map(message -> chatMapper.toMessageResponse(message, myRole));
    }

    /**
     * POST /api/chat/rooms/init - Khởi tạo hoặc lấy phòng chat giữa customer và shop
     */
    @Transactional
    public InitRoomResponse initRoom(InitRoomRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        String currentUserId = currentUser.getId();
        String shopId = request.getShopId();

        // Kiểm tra shop tồn tại
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BadRequestException("SHOP_NOT_FOUND"));

        // Không thể chat với shop của chính mình
        if (shop.getUser().getId().equals(currentUserId)) {
            throw new BadRequestException("CANNOT_CHAT_WITH_YOUR_OWN_SHOP");
        }

        // Kiểm tra xem đã có phòng chat giữa customer và shop chưa
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByCustomerIdAndShopId(currentUserId, shopId);

        if (existingRoom.isPresent()) {
            // Đã có phòng chat
            return InitRoomResponse.builder()
                    .roomId(existingRoom.get().getId())
                    .isNew(false)
                    .shopId(shop.getId())
                    .shopName(shop.getName())
                    .shopAvatar(shop.getAvatarUrl())
                    .build();
        }

        // Tạo phòng chat mới
        ChatRoom newRoom = ChatRoom.builder()
                .customer(currentUser)
                .shop(shop)
                .lastActive(LocalDateTime.now())
                .build();
        newRoom = chatRoomRepository.save(newRoom);

        log.info("Created new chat room {} between customer {} and shop {}", newRoom.getId(), currentUserId, shopId);

        return InitRoomResponse.builder()
                .roomId(newRoom.getId())
                .isNew(true)
                .shopId(shop.getId())
                .shopName(shop.getName())
                .shopAvatar(shop.getAvatarUrl())
                .build();
    }

    // ==================== WebSocket Methods ====================

    /**
     * Kết quả lưu tin nhắn - chứa response cho cả sender và receiver
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SaveMessageResult {
        private ChatMessageResponse messageForSender;
        private ChatMessageResponse messageForReceiver;
        private String receiverEmail;
    }

    /**
     * Lưu tin nhắn từ WebSocket và trả về DTO cho cả sender và receiver
     */
    @Transactional
    public SaveMessageResult saveMessage(ChatMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BadRequestException("ROOM_NOT_FOUND"));

        // Kiểm tra user có trong phòng chat không và xác định vai trò
        SenderRole senderRole = determineSenderRole(room, sender.getId());

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .senderRole(senderRole)
                .type(request.getType())
                .content(request.getContent())
                .isRead(false)
                .build();

        message = chatMessageRepository.save(message);

        // Cập nhật lastActive của room
        chatRoomRepository.updateLastActive(room.getId(), LocalDateTime.now());

        log.debug("Saved message {} in room {} from {}", message.getId(), room.getId(), senderRole);

        // Tạo response cho sender (isMe = true)
        ChatMessageResponse messageForSender = chatMapper.toMessageResponse(message, senderRole);

        // Tạo response cho receiver (isMe = false)
        SenderRole receiverRole = senderRole == SenderRole.USER ? SenderRole.SHOP : SenderRole.USER;
        ChatMessageResponse messageForReceiver = chatMapper.toMessageResponse(message, receiverRole);

        // Lấy email của receiver
        String receiverEmail;
        if (senderRole == SenderRole.USER) {
            receiverEmail = room.getShop().getUser().getEmail();
        } else {
            receiverEmail = room.getCustomer().getEmail();
        }

        return new SaveMessageResult(messageForSender, messageForReceiver, receiverEmail);
    }

    /**
     * Lấy username của người nhận tin nhắn trong phòng chat
     */
    @Transactional(readOnly = true)
    public String getReceiverUsername(String roomId, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("ROOM_NOT_FOUND"));

        if (isShopOwner(room, sender.getId())) {
            return room.getCustomer().getEmail();
        } else {
            return room.getShop().getUser().getEmail();
        }
    }

    /**
     * Đánh dấu tất cả tin nhắn trong phòng là đã đọc
     */
    @Transactional
    public void markAsRead(String roomId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        ChatRoom room = validateAndGetRoomAccess(roomId, user.getId());

        // Xác định vai trò của user
        SenderRole myRole = isShopOwner(room, user.getId()) ? SenderRole.SHOP : SenderRole.USER;

        chatMessageRepository.markAllAsRead(roomId, myRole);
        log.debug("Marked all messages as read in room {} for {} ({})", roomId, user.getId(), myRole);
    }

    // ==================== Helper Methods ====================

    /**
     * Kiểm tra user có phải là shop owner trong phòng chat không
     */
    private boolean isShopOwner(ChatRoom room, String userId) {
        return room.getShop().getUser().getId().equals(userId);
    }

    /**
     * Kiểm tra user có quyền truy cập phòng chat không và trả về room
     */
    private ChatRoom validateAndGetRoomAccess(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("ROOM_NOT_FOUND"));

        boolean isCustomer = room.getCustomer().getId().equals(userId);
        boolean isShopOwner = room.getShop().getUser().getId().equals(userId);

        if (!isCustomer && !isShopOwner) {
            throw new ForbiddenException("ACCESS_DENIED_TO_CHAT_ROOM");
        }

        return room;
    }

    /**
     * Xác định vai trò của sender trong phòng chat
     */
    private SenderRole determineSenderRole(ChatRoom room, String userId) {
        if (room.getCustomer().getId().equals(userId)) {
            return SenderRole.USER;
        } else if (room.getShop().getUser().getId().equals(userId)) {
            return SenderRole.SHOP;
        } else {
            throw new ForbiddenException("ACCESS_DENIED_TO_CHAT_ROOM");
        }
    }
}
