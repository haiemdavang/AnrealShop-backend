-- Bảng lưu lịch sử chat với AI chatbot
CREATE TABLE IF NOT EXISTS lich_su_chatbot (
    id_lich_su VARCHAR(36) PRIMARY KEY,
    id_nguoi_dung VARCHAR(36) NOT NULL,
    cau_hoi TEXT NOT NULL,
    tra_loi TEXT,
    loai_response VARCHAR(50),
    loai_query VARCHAR(50),
    anh_dai_dien VARCHAR(500),
    link_san_pham VARCHAR(255),
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatbot_history_user FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
);

CREATE INDEX idx_chatbot_history_user ON lich_su_chatbot(id_nguoi_dung);
CREATE INDEX idx_chatbot_history_created ON lich_su_chatbot(ngay_tao DESC);
