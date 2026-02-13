-- Bảng lưu danh sách sản phẩm yêu thích của người dùng
CREATE TABLE IF NOT EXISTS yeu_thich (
    id_yeu_thich VARCHAR(36) PRIMARY KEY,
    id_nguoi_dung VARCHAR(36) NOT NULL,
    id_san_pham VARCHAR(36) NOT NULL,
    thoi_gian_yeu_thich TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_user FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
    CONSTRAINT fk_favorite_product FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),
    CONSTRAINT uk_user_product_favorite UNIQUE (id_nguoi_dung, id_san_pham)
);

CREATE INDEX idx_favorite_user ON yeu_thich(id_nguoi_dung);
CREATE INDEX idx_favorite_product ON yeu_thich(id_san_pham);
CREATE INDEX idx_favorite_created ON yeu_thich(thoi_gian_yeu_thich DESC);
