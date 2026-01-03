-- --------------------------------------------------------
-- CẤU HÌNH MÔI TRƯỜNG
-- --------------------------------------------------------
SET client_encoding = 'UTF8';

-- --------------------------------------------------------
-- XÓA BẢNG CŨ (NẾU CÓ) ĐỂ TRÁNH LỖI KHI CHẠY LẠI
-- --------------------------------------------------------
DROP TABLE IF EXISTS user_notifications CASCADE;
DROP TABLE IF EXISTS sku_attributes CASCADE;
DROP TABLE IF EXISTS shop_order_tracks CASCADE;
DROP TABLE IF EXISTS shop_order_shipping_fees CASCADE;
DROP TABLE IF EXISTS shop_notifications CASCADE;
DROP TABLE IF EXISTS shop_category_items CASCADE;
DROP TABLE IF EXISTS shop_categories CASCADE;
DROP TABLE IF EXISTS shop_attribute_keys CASCADE;
DROP TABLE IF EXISTS shipping_tracks CASCADE;
DROP TABLE IF EXISTS shippings CASCADE;
DROP TABLE IF EXISTS product_review_media CASCADE;
DROP TABLE IF EXISTS product_reviews CASCADE;
DROP TABLE IF EXISTS product_media CASCADE;
DROP TABLE IF EXISTS product_general_attributes CASCADE;
DROP TABLE IF EXISTS order_item_tracks CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS shop_orders CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS history_login CASCADE;
DROP TABLE IF EXISTS follows CASCADE;
DROP TABLE IF EXISTS chat_room_participants CASCADE;
DROP TABLE IF EXISTS chat_messages CASCADE;
DROP TABLE IF EXISTS chat_rooms CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS product_skus CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS shop_addresses CASCADE;
DROP TABLE IF EXISTS shops CASCADE;
DROP TABLE IF EXISTS user_addresses CASCADE;
DROP TABLE IF EXISTS wards CASCADE;
DROP TABLE IF EXISTS districts CASCADE;
DROP TABLE IF EXISTS provinces CASCADE;
DROP TABLE IF EXISTS display_categories CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS banners CASCADE;
DROP TABLE IF EXISTS attribute_values CASCADE;
DROP TABLE IF EXISTS attribute_keys CASCADE;

-- --------------------------------------------------------
-- TẠO CẤU TRÚC BẢNG (SCHEMA)
-- --------------------------------------------------------

-- 1. Bảng: attribute_keys
CREATE TABLE attribute_keys (
                                id varchar(36) NOT NULL,
                                created_at timestamp DEFAULT NULL,
                                display_name varchar(100) NOT NULL,
                                is_default boolean DEFAULT FALSE,
                                key_name varchar(50) NOT NULL,
                                updated_at timestamp DEFAULT NULL,
                                display_order int DEFAULT 0,
                                is_multi_selected boolean DEFAULT FALSE,
                                is_for_sku boolean DEFAULT FALSE,
                                PRIMARY KEY (id),
                                CONSTRAINT uk_attribute_keys_key_name UNIQUE (key_name)
);

-- 2. Bảng: attribute_values
CREATE TABLE attribute_values (
                                  id varchar(36) NOT NULL,
                                  created_at timestamp DEFAULT NULL,
                                  display_order int DEFAULT 0,
                                  is_default boolean DEFAULT FALSE,
                                  metadata text,
                                  updated_at timestamp DEFAULT NULL,
                                  value varchar(255) NOT NULL,
                                  attribute_key_id varchar(36) NOT NULL,
                                  PRIMARY KEY (id),
                                  CONSTRAINT fk_attribute_values_key FOREIGN KEY (attribute_key_id) REFERENCES attribute_keys (id)
);

-- 3. Bảng: banners
CREATE TABLE banners (
                         id varchar(36) NOT NULL,
                         created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                         description text,
                         display_order int DEFAULT 0,
                         end_date timestamp DEFAULT NULL,
                         image_url varchar(255) NOT NULL,
                         is_active boolean DEFAULT TRUE,
                         redirect_url varchar(255) DEFAULT NULL,
                         start_date timestamp DEFAULT NULL,
                         title varchar(100) DEFAULT NULL,
                         updated_at timestamp DEFAULT NULL,
                         PRIMARY KEY (id)
);

-- 4. Bảng: roles
CREATE TABLE roles (
                       id varchar(36) NOT NULL,
                       description varchar(255) DEFAULT NULL,
                       name varchar(20) NOT NULL CHECK (name IN ('USER','ADMIN')),
                       PRIMARY KEY (id),
                       CONSTRAINT uk_roles_name UNIQUE (name)
);

-- 5. Bảng: users
CREATE TABLE users (
                       id varchar(36) NOT NULL,
                       avatar_url varchar(255) DEFAULT NULL,
                       created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                       delete_reason text,
                       deleted boolean DEFAULT FALSE,
                       dob date DEFAULT NULL,
                       email varchar(100) NOT NULL,
                       from_social boolean DEFAULT FALSE,
                       full_name varchar(100) DEFAULT NULL,
                       gender varchar(10) CHECK (gender IN ('MALE','FEMALE','OTHER')),
                       password varchar(60) DEFAULT NULL,
                       verify boolean NOT NULL DEFAULT FALSE,
                       phone_number varchar(20) DEFAULT NULL,
                       updated_at timestamp DEFAULT NULL,
                       username varchar(50) NOT NULL,
                       role_id varchar(36) DEFAULT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- 6. Bảng: carts
CREATE TABLE carts (
                       id varchar(36) NOT NULL,
                       updated_at timestamp DEFAULT NULL,
                       user_id varchar(36) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 7. Bảng: categories
CREATE TABLE categories (
                            id varchar(36) NOT NULL,
                            created_at timestamp DEFAULT NULL,
                            description text,
                            has_children boolean NOT NULL DEFAULT FALSE,
                            name varchar(100) NOT NULL,
                            product_count int NOT NULL DEFAULT 0,
                            url_path text,
                            url_slug varchar(100) DEFAULT NULL,
                            parent_id varchar(36) DEFAULT NULL,
                            level int NOT NULL DEFAULT 0,
                            is_deleted boolean NOT NULL DEFAULT FALSE,
                            is_visible boolean NOT NULL DEFAULT TRUE,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
);

-- 8. Bảng: display_categories
CREATE TABLE display_categories (
                                    id varchar(36) NOT NULL,
                                    thumbnail_url varchar(255) DEFAULT NULL,
                                    category_id varchar(36) NOT NULL,
                                    media_type varchar(10) NOT NULL CHECK (media_type IN ('IMAGE','VIDEO')),
                                    display_order int NOT NULL DEFAULT 0,
                                    position varchar(20) NOT NULL CHECK (position IN ('HOMEPAGE','SIDEBAR')),
                                    PRIMARY KEY (id),
                                    CONSTRAINT fk_display_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- 9. Bảng: provinces
CREATE TABLE provinces (
                           id varchar(36) NOT NULL,
                           name varchar(100) NOT NULL,
                           PRIMARY KEY (id)
);

-- 10. Bảng: districts
CREATE TABLE districts (
                           id varchar(36) NOT NULL,
                           name varchar(100) NOT NULL,
                           province_id varchar(36) NOT NULL,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_districts_province FOREIGN KEY (province_id) REFERENCES provinces (id)
);

-- 11. Bảng: wards
CREATE TABLE wards (
                       id varchar(36) NOT NULL,
                       name varchar(100) NOT NULL,
                       district_id varchar(36) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_wards_district FOREIGN KEY (district_id) REFERENCES districts (id)
);

-- 12. Bảng: user_addresses
CREATE TABLE user_addresses (
                                id varchar(36) NOT NULL,
                                detail text NOT NULL,
                                phone_number varchar(20) NOT NULL,
                                primary_address boolean DEFAULT FALSE,
                                receiver_name varchar(100) NOT NULL,
                                district_id varchar(36) NOT NULL,
                                province_id varchar(36) NOT NULL,
                                user_id varchar(36) NOT NULL,
                                ward_id varchar(36) NOT NULL,
                                PRIMARY KEY (id),
                                CONSTRAINT fk_user_addresses_district FOREIGN KEY (district_id) REFERENCES districts (id),
                                CONSTRAINT fk_user_addresses_province FOREIGN KEY (province_id) REFERENCES provinces (id),
                                CONSTRAINT fk_user_addresses_user FOREIGN KEY (user_id) REFERENCES users (id),
                                CONSTRAINT fk_user_addresses_ward FOREIGN KEY (ward_id) REFERENCES wards (id)
);

-- 13. Bảng: shops
CREATE TABLE shops (
                       id varchar(36) NOT NULL,
                       avatar_url varchar(255) DEFAULT NULL,
                       average_rating float DEFAULT 0,
                       created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                       deleted boolean DEFAULT FALSE,
                       description text,
                       follower_count int DEFAULT 0,
                       name varchar(100) NOT NULL,
                       product_count int DEFAULT 0,
                       revenue bigint DEFAULT 0,
                       total_reviews int DEFAULT 0,
                       updated_at timestamp DEFAULT NULL,
                       url_slug text,
                       user_id varchar(36) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_shops_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 14. Bảng: shop_addresses
CREATE TABLE shop_addresses (
                                id varchar(36) NOT NULL,
                                detail text NOT NULL,
                                phone_number varchar(20) NOT NULL,
                                primary_address boolean DEFAULT FALSE,
                                sender_name varchar(100) NOT NULL,
                                district_id varchar(36) NOT NULL,
                                province_id varchar(36) NOT NULL,
                                shop_id varchar(36) NOT NULL,
                                ward_id varchar(36) NOT NULL,
                                PRIMARY KEY (id),
                                CONSTRAINT fk_shop_addresses_district FOREIGN KEY (district_id) REFERENCES districts (id),
                                CONSTRAINT fk_shop_addresses_province FOREIGN KEY (province_id) REFERENCES provinces (id),
                                CONSTRAINT fk_shop_addresses_shop FOREIGN KEY (shop_id) REFERENCES shops (id),
                                CONSTRAINT fk_shop_addresses_ward FOREIGN KEY (ward_id) REFERENCES wards (id)
);

-- 15. Bảng: products
CREATE TABLE products (
                          id varchar(36) NOT NULL,
                          average_rating float NOT NULL,
                          created_at timestamp DEFAULT NULL,
                          deleted boolean NOT NULL,
                          description text,
                          discount_price bigint NOT NULL,
                          name varchar(255) NOT NULL,
                          price bigint NOT NULL,
                          quantity int NOT NULL,
                          restrict_status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK (restrict_status IN ('ALL','ACTIVE','VIOLATION','PENDING','HIDDEN')),
                          restricted boolean NOT NULL,
                          restricted_reason text,
                          revenue bigint NOT NULL,
                          sold int NOT NULL,
                          sort_description text,
                          thumbnail_url varchar(255) DEFAULT NULL,
                          total_reviews int NOT NULL,
                          updated_at timestamp DEFAULT NULL,
                          url_slug text,
                          visible boolean NOT NULL,
                          weight decimal(10,2) NOT NULL DEFAULT 0.00,
                          category_id varchar(36) DEFAULT NULL,
                          shop_id varchar(36) NOT NULL,
                          height decimal(10,2) NOT NULL DEFAULT 0.00,
                          length decimal(10,2) NOT NULL DEFAULT 0.00,
                          width decimal(10,2) NOT NULL DEFAULT 0.00,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
                          CONSTRAINT fk_products_shop FOREIGN KEY (shop_id) REFERENCES shops (id)
);

-- 16. Bảng: product_skus
CREATE TABLE product_skus (
                              id varchar(36) NOT NULL,
                              created_at timestamp DEFAULT NULL,
                              price bigint NOT NULL,
                              quantity int DEFAULT 0,
                              sku varchar(50) NOT NULL,
                              image_urls varchar(255) DEFAULT NULL,
                              updated_at timestamp DEFAULT NULL,
                              product_id varchar(36) NOT NULL,
                              sold int DEFAULT 0,
                              PRIMARY KEY (id),
                              CONSTRAINT uk_product_skus_sku UNIQUE (sku),
                              CONSTRAINT fk_product_skus_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- 17. Bảng: cart_items
CREATE TABLE cart_items (
                            id varchar(36) NOT NULL,
                            price bigint NOT NULL,
                            quantity int DEFAULT 1,
                            selected boolean DEFAULT TRUE,
                            cart_id varchar(36) NOT NULL,
                            product_sku_id varchar(36) NOT NULL,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id),
                            CONSTRAINT fk_cart_items_product_sku FOREIGN KEY (product_sku_id) REFERENCES product_skus (id)
);

-- 18. Bảng: chat_rooms
CREATE TABLE chat_rooms (
                            id varchar(36) NOT NULL,
                            last_active timestamp DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (id)
);

-- 19. Bảng: chat_messages
CREATE TABLE chat_messages (
                               id varchar(36) NOT NULL,
                               content text,
                               created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                               is_read boolean DEFAULT FALSE,
                               type varchar(10) NOT NULL CHECK (type IN ('TEXT','MEDIA')),
                               room_id varchar(36) NOT NULL,
                               sender_id varchar(36) NOT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_chat_messages_room FOREIGN KEY (room_id) REFERENCES chat_rooms (id),
                               CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

-- 20. Bảng: chat_room_participants
CREATE TABLE chat_room_participants (
                                        chat_room_id varchar(255) NOT NULL,
                                        user_id varchar(255) NOT NULL,
                                        PRIMARY KEY (chat_room_id,user_id),
                                        CONSTRAINT fk_chat_room_participants_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id),
                                        CONSTRAINT fk_chat_room_participants_user FOREIGN KEY (user_id) REFERENCES users (id)
);


-- 22. Bảng: follows
CREATE TABLE follows (
                         id varchar(36) NOT NULL,
                         followed_at timestamp DEFAULT CURRENT_TIMESTAMP,
                         shop_id varchar(36) NOT NULL,
                         user_id varchar(36) NOT NULL,
                         PRIMARY KEY (id),
                         CONSTRAINT uk_follows_user_shop UNIQUE (user_id,shop_id),
                         CONSTRAINT fk_follows_shop FOREIGN KEY (shop_id) REFERENCES shops (id),
                         CONSTRAINT fk_follows_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 23. Bảng: history_login
CREATE TABLE history_login (
                               id varchar(36) NOT NULL,
                               user_id varchar(36) NOT NULL,
                               login_at timestamp NOT NULL,
                               logout_at timestamp DEFAULT NULL,
                               ip_address varchar(45) NOT NULL,
                               user_agent varchar(255) DEFAULT NULL,
                               device varchar(100) DEFAULT NULL,
                               location varchar(100) DEFAULT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_history_login_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 24. Bảng: payments
CREATE TABLE payments (
                          id varchar(36) NOT NULL,
                          amount bigint NOT NULL,
                          created_at timestamp DEFAULT NULL,
                          expire_at timestamp DEFAULT NULL,
                          gateway varchar(20) CHECK (gateway IN ('VNPAY','CASH_ON_DELIVERY','MOMO')),
                          status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','COMPLETED','CANCELED','EXPIRED','REFUNDED','FAILED')),
                          type varchar(20) CHECK (type IN ('BANK_TRANSFER','COD')),
                          updated_at timestamp DEFAULT NULL,
                          PRIMARY KEY (id)
);

-- 25. Bảng: orders
CREATE TABLE orders (
                        id varchar(36) NOT NULL,
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                        grand_total_amount bigint NOT NULL,
                        total_shipping_fee bigint DEFAULT NULL,
                        status varchar(20) NOT NULL CHECK (status IN ('PROCESSING','SUCCESS','CANCELED')),
                        sub_total_amount bigint DEFAULT NULL,
                        updated_at timestamp DEFAULT NULL,
                        payment_id varchar(36) DEFAULT NULL,
                        address_id varchar(36) NOT NULL,
                        user_id varchar(36) NOT NULL,
                        PRIMARY KEY (id),
                        CONSTRAINT uk_orders_payment UNIQUE (payment_id),
                        CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES user_addresses (id),
                        CONSTRAINT fk_orders_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 26. Bảng: shop_orders
CREATE TABLE shop_orders (
                             id varchar(36) NOT NULL,
                             created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                             shipping_fee int DEFAULT 0,
                             status varchar(30) NOT NULL CHECK (status IN ('INIT_PROCESSING','PENDING_CONFIRMATION','CONFIRMED','PREPARING','SHIPPING','DELIVERED','CLOSED')),
                             total_amount bigint NOT NULL,
                             order_id varchar(36) NOT NULL,
                             address_id varchar(36) NOT NULL,
                             shop_id varchar(36) NOT NULL,
                             user_id varchar(36) NOT NULL,
                             updated_at timestamp DEFAULT NULL,
                             total_weight decimal(10,2) DEFAULT 0.00,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_shop_orders_order FOREIGN KEY (order_id) REFERENCES orders (id),
                             CONSTRAINT fk_shop_orders_address FOREIGN KEY (address_id) REFERENCES shop_addresses (id),
                             CONSTRAINT fk_shop_orders_shop FOREIGN KEY (shop_id) REFERENCES shops (id),
                             CONSTRAINT fk_shop_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 27. Bảng: order_items
CREATE TABLE order_items (
                             id varchar(36) NOT NULL,
                             created_at timestamp DEFAULT NULL,
                             price bigint NOT NULL,
                             quantity int NOT NULL,
                             success boolean DEFAULT FALSE,
                             updated_at timestamp DEFAULT NULL,
                             order_id varchar(36) NOT NULL,
                             product_sku_id varchar(36) NOT NULL,
                             shop_order_id varchar(36) DEFAULT NULL,
                             status varchar(30) NOT NULL CHECK (status IN ('PROCESSING','PENDING_CONFIRMATION','PREPARING','WAIT_SHIPMENT','SHIPPING','DELIVERED','REFUND','CANCELED')),
                             cancel_reason varchar(255) DEFAULT NULL,
                             canceled_by varchar(20) CHECK (canceled_by IN ('CUSTOMER','SHOP')),
                             PRIMARY KEY (id),
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
                             CONSTRAINT fk_order_items_sku FOREIGN KEY (product_sku_id) REFERENCES product_skus (id),
                             CONSTRAINT fk_order_items_shop_order FOREIGN KEY (shop_order_id) REFERENCES shop_orders (id)
);

-- 28. Bảng: order_item_tracks
CREATE TABLE order_item_tracks (
                                   status varchar(30) NOT NULL CHECK (status IN ('PROCESSING','PENDING_CONFIRMATION','PREPARING','WAIT_SHIPMENT','SHIPPING','DELIVERED','REFUND','CANCELED')),
                                   updated_at timestamp NOT NULL,
                                   order_item_id varchar(36) NOT NULL,
                                   PRIMARY KEY (order_item_id,updated_at),
                                   CONSTRAINT fk_order_item_tracks_item FOREIGN KEY (order_item_id) REFERENCES order_items (id)
);

-- 29. Bảng: product_general_attributes
CREATE TABLE product_general_attributes (
                                            product_id varchar(36) NOT NULL,
                                            attribute_value_id varchar(36) NOT NULL,
                                            PRIMARY KEY (product_id,attribute_value_id),
                                            CONSTRAINT fk_pga_attribute FOREIGN KEY (attribute_value_id) REFERENCES attribute_values (id),
                                            CONSTRAINT fk_pga_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- 30. Bảng: product_media
CREATE TABLE product_media (
                               id varchar(36) NOT NULL,
                               type varchar(10) NOT NULL CHECK (type IN ('IMAGE','VIDEO')),
                               url varchar(255) NOT NULL,
                               product_id varchar(36) NOT NULL,
                               thumbnail_url varchar(255) DEFAULT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_product_media_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- 31. Bảng: product_reviews
CREATE TABLE product_reviews (
                                 id varchar(36) NOT NULL,
                                 comment text,
                                 created_at timestamp DEFAULT NULL,
                                 rating int NOT NULL,
                                 updated_at timestamp DEFAULT NULL,
                                 order_item_id varchar(36) DEFAULT NULL,
                                 product_id varchar(36) NOT NULL,
                                 user_id varchar(36) NOT NULL,
                                 PRIMARY KEY (id),
                                 CONSTRAINT uk_product_reviews_order_item UNIQUE (order_item_id),
                                 CONSTRAINT fk_product_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items (id),
                                 CONSTRAINT fk_product_reviews_product FOREIGN KEY (product_id) REFERENCES products (id),
                                 CONSTRAINT fk_product_reviews_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 32. Bảng: product_review_media
CREATE TABLE product_review_media (
                                      id varchar(36) NOT NULL,
                                      created_at timestamp DEFAULT NULL,
                                      media_type varchar(10) DEFAULT 'IMAGE' CHECK (media_type IN ('IMAGE','VIDEO')),
                                      media_url varchar(255) NOT NULL,
                                      review_id varchar(36) NOT NULL,
                                      PRIMARY KEY (id),
                                      CONSTRAINT fk_product_review_media_review FOREIGN KEY (review_id) REFERENCES product_reviews (id)
);

-- 33. Bảng: shippings
CREATE TABLE shippings (
                           id varchar(36) NOT NULL,
                           shop_order_id varchar(36) NOT NULL,
                           address_from_id varchar(36) NOT NULL,
                           address_to_id varchar(36) NOT NULL,
                           shipper_name varchar(100) NOT NULL,
                           shipper_phone varchar(20) NOT NULL,
                           fee bigint NOT NULL,
                           created_at timestamp DEFAULT NULL,
                           status varchar(30) NOT NULL CHECK (status IN ('ORDER_CREATED','WAITING_FOR_PICKUP','PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','DELIVERY_FAILED','RETURNED')),
                           total_weight bigint DEFAULT 0,
                           note text,
                           is_printed boolean NOT NULL DEFAULT FALSE,
                           day_pickup date DEFAULT NULL,
                           cancel_reason text,
                           PRIMARY KEY (id),
                           CONSTRAINT uk_shippings_shop_order UNIQUE (shop_order_id),
                           CONSTRAINT fk_shippings_address_from FOREIGN KEY (address_from_id) REFERENCES shop_addresses (id),
                           CONSTRAINT fk_shippings_address_to FOREIGN KEY (address_to_id) REFERENCES user_addresses (id),
                           CONSTRAINT fk_shippings_shop_order FOREIGN KEY (shop_order_id) REFERENCES shop_orders (id)
);

-- 34. Bảng: shipping_tracks
CREATE TABLE shipping_tracks (
                                 shipping_id varchar(36) NOT NULL,
                                 status varchar(30) NOT NULL CHECK (status IN ('ORDER_CREATED','WAITING_FOR_PICKUP','PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','DELIVERY_FAILED','RETURNED')),
                                 note varchar(255) DEFAULT NULL,
                                 updated_at timestamp NOT NULL,
                                 PRIMARY KEY (shipping_id,updated_at),
                                 CONSTRAINT fk_shipping_tracks_shipping FOREIGN KEY (shipping_id) REFERENCES shippings (id)
);

-- 35. Bảng: shop_attribute_keys
CREATE TABLE shop_attribute_keys (
                                     attribute_key_id varchar(36) NOT NULL,
                                     shop_id varchar(36) NOT NULL,
                                     PRIMARY KEY (attribute_key_id,shop_id),
                                     CONSTRAINT fk_sak_attribute_key FOREIGN KEY (attribute_key_id) REFERENCES attribute_keys (id),
                                     CONSTRAINT fk_sak_shop FOREIGN KEY (shop_id) REFERENCES shops (id)
);

-- 36. Bảng: shop_categories
CREATE TABLE shop_categories (
                                 id varchar(36) NOT NULL,
                                 shop_id varchar(36) NOT NULL,
                                 PRIMARY KEY (id),
                                 CONSTRAINT fk_shop_categories_shop FOREIGN KEY (shop_id) REFERENCES shops (id)
);

-- 37. Bảng: shop_category_items
CREATE TABLE shop_category_items (
                                     category_id varchar(255) NOT NULL,
                                     shop_categories_id varchar(255) NOT NULL,
                                     PRIMARY KEY (category_id,shop_categories_id),
                                     CONSTRAINT fk_sci_category FOREIGN KEY (category_id) REFERENCES categories (id),
                                     CONSTRAINT fk_sci_shop_category FOREIGN KEY (shop_categories_id) REFERENCES shop_categories (id)
);

-- 38. Bảng: shop_notifications
CREATE TABLE shop_notifications (
                                    id varchar(36) NOT NULL,
                                    content text NOT NULL,
                                    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                                    is_read boolean DEFAULT FALSE,
                                    redirect_url varchar(255) DEFAULT NULL,
                                    shop_id varchar(36) NOT NULL,
                                    updated_at timestamp DEFAULT NULL,
                                    thumbnail_url varchar(255) DEFAULT 'https://res.cloudinary.com/dlcjc36ow/image/upload/v1747916255/ImagError_jsv7hr.png',
                                    PRIMARY KEY (id),
                                    CONSTRAINT fk_shop_notifications_shop FOREIGN KEY (shop_id) REFERENCES shops (id)
);

-- 39. Bảng: shop_order_shipping_fees
CREATE TABLE shop_order_shipping_fees (
                                          shop_order_id varchar(36) NOT NULL,
                                          amount bigint NOT NULL,
                                          PRIMARY KEY (shop_order_id),
                                          CONSTRAINT fk_sosf_shop_order FOREIGN KEY (shop_order_id) REFERENCES shop_orders (id)
);

-- 40. Bảng: shop_order_tracks
CREATE TABLE shop_order_tracks (
                                   updated_at timestamp NOT NULL,
                                   status varchar(30) NOT NULL CHECK (status IN ('INIT_PROCESSING','PENDING_CONFIRMATION','CONFIRMED','PREPARING','SHIPPING','DELIVERED','CLOSED')),
                                   shop_order_id varchar(255) NOT NULL,
                                   PRIMARY KEY (shop_order_id,updated_at),
                                   CONSTRAINT fk_sot_shop_order FOREIGN KEY (shop_order_id) REFERENCES shop_orders (id)
);

-- 41. Bảng: sku_attributes
CREATE TABLE sku_attributes (
                                sku_id varchar(36) NOT NULL,
                                attribute_value_id varchar(36) NOT NULL,
                                PRIMARY KEY (sku_id,attribute_value_id),
                                CONSTRAINT fk_sku_attributes_value FOREIGN KEY (attribute_value_id) REFERENCES attribute_values (id),
                                CONSTRAINT fk_sku_attributes_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id)
);

-- 42. Bảng: user_notifications
CREATE TABLE user_notifications (
                                    id varchar(36) NOT NULL,
                                    content text NOT NULL,
                                    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                                    is_read boolean DEFAULT FALSE,
                                    redirect_url varchar(255) DEFAULT NULL,
                                    thumbnail_url varchar(255) DEFAULT NULL,
                                    user_id varchar(36) NOT NULL,
                                    updated_at timestamp DEFAULT NULL,
                                    PRIMARY KEY (id),
                                    CONSTRAINT fk_user_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
);