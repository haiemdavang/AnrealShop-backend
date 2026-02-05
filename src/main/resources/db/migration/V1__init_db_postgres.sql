-- ========================================================
-- NHÓM 1: CÁC BẢNG ĐỘC LẬP (CƠ SỞ)
-- ========================================================

-- 1. Bảng Vai trò (vai_tro)
-- Lưu trữ các quyền cơ bản trong hệ thống (USER, ADMIN)
CREATE TABLE public.vai_tro (
                                id_vai_tro varchar(36) NOT NULL,
                                mo_ta varchar(255) NULL,
                                ten_vai_tro varchar(10) NOT NULL,
                                CONSTRAINT idx_vaitro_ten UNIQUE (ten_vai_tro),
                                CONSTRAINT vai_tro_pkey PRIMARY KEY (id_vai_tro),
                                CONSTRAINT vai_tro_ten_vai_tro_check CHECK (((ten_vai_tro)::text = ANY ((ARRAY['USER'::character varying, 'ADMIN'::character varying])::text[])))
);
ALTER TABLE public.vai_tro OWNER TO postgres;
GRANT ALL ON TABLE public.vai_tro TO postgres;

-- 2. Bảng Tỉnh (tinh)
-- Danh mục Tỉnh/Thành phố
CREATE TABLE public.tinh (
                             id_tinh varchar(36) NOT NULL,
                             ten_tinh varchar(100) NOT NULL,
                             CONSTRAINT tinh_pkey PRIMARY KEY (id_tinh)
);
ALTER TABLE public.tinh OWNER TO postgres;
GRANT ALL ON TABLE public.tinh TO postgres;

-- 3. Bảng Thuộc tính (thuoc_tinh)
-- Cấu hình các loại thuộc tính sản phẩm (Màu sắc, Kích thước...)
CREATE TABLE public.thuoc_tinh (
                                   id_thuoc_tinh varchar(36) NOT NULL,
                                   ngay_tao timestamp(6) NULL,
                                   ten_hien_thi varchar(100) NOT NULL,
                                   thu_tu_hien_thi int4 NULL,
                                   mac_dinh bool DEFAULT false NULL,
                                   dung_chosku bool DEFAULT false NULL,
                                   chon_nhieu bool DEFAULT false NULL,
                                   ten_khoa varchar(50) NOT NULL,
                                   ngay_cap_nhat timestamp(6) NULL,
                                   CONSTRAINT idx_thuoctinh_tenkhoa UNIQUE (ten_khoa),
                                   CONSTRAINT thuoc_tinh_pkey PRIMARY KEY (id_thuoc_tinh)
);
ALTER TABLE public.thuoc_tinh OWNER TO postgres;
GRANT ALL ON TABLE public.thuoc_tinh TO postgres;

-- 4. Bảng Thanh toán (thanh_toan)
-- Quản lý giao dịch và trạng thái thanh toán
CREATE TABLE public.thanh_toan (
                                   id_thanh_toan varchar(36) NOT NULL,
                                   so_tien int8 NOT NULL,
                                   ngay_tao timestamp(6) NULL,
                                   het_han_luc timestamp(6) NULL,
                                   cong_thanh_toan varchar(255) NULL,
                                   trang_thai varchar(255) NOT NULL,
                                   loai_thanh_toan varchar(50) NULL,
                                   ngay_cap_nhat timestamp(6) NULL,
                                   CONSTRAINT thanh_toan_cong_thanh_toan_check CHECK (((cong_thanh_toan)::text = ANY ((ARRAY['VNPAY'::character varying, 'MOMO'::character varying, 'CASH_ON_DELIVERY'::character varying])::text[]))),
                                   CONSTRAINT thanh_toan_loai_thanh_toan_check CHECK (((loai_thanh_toan)::text = ANY ((ARRAY['COD'::character varying, 'BANK_TRANSFER'::character varying])::text[]))),
                                   CONSTRAINT thanh_toan_pkey PRIMARY KEY (id_thanh_toan),
                                   CONSTRAINT thanh_toan_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['PENDING'::character varying, 'COMPLETED'::character varying, 'CANCELED'::character varying, 'EXPIRED'::character varying, 'REFUNDED'::character varying, 'FAILED'::character varying])::text[])))
);
ALTER TABLE public.thanh_toan OWNER TO postgres;
GRANT ALL ON TABLE public.thanh_toan TO postgres;

-- 5. Bảng Phòng chat (phong_chat)
-- Khởi tạo định danh các phòng chat
CREATE TABLE public.phong_chat (
                                   id_phong_chat varchar(36) NOT NULL,
                                   hoat_dong_gan_nhat timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                   CONSTRAINT phong_chat_pkey PRIMARY KEY (id_phong_chat)
);
ALTER TABLE public.phong_chat OWNER TO postgres;
GRANT ALL ON TABLE public.phong_chat TO postgres;

-- 6. Bảng Banner (banner)
-- Thông tin quảng cáo và trình chiếu ảnh
CREATE TABLE public.banner (
                               id_banner varchar(36) NOT NULL,
                               ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                               mo_ta text NULL,
                               thu_tu_hien_thi int4 DEFAULT 0 NULL,
                               ngay_ket_thuc timestamp(6) NULL,
                               duong_dan_anh varchar(255) NOT NULL,
                               dang_hoat_dong bool DEFAULT true NULL,
                               duong_dan_chuyen_huong varchar(255) NULL,
                               ngay_bat_dau timestamp(6) NULL,
                               tieu_de varchar(100) NULL,
                               ngay_cap_nhat timestamp(6) NULL,
                               CONSTRAINT banner_pkey PRIMARY KEY (id_banner)
);
ALTER TABLE public.banner OWNER TO postgres;
GRANT ALL ON TABLE public.banner TO postgres;

-- ========================================================
-- NHÓM 2: CÁC BẢNG PHỤ THUỘC CẤP 1
-- ========================================================

-- 1. Bảng Danh mục (danh_muc)
-- Lưu ý: Có quan hệ đệ quy (parent_id) để quản lý cây danh mục
CREATE TABLE public.danh_muc (
                                 id_danh_muc varchar(36) NOT NULL,
                                 ngay_tao timestamp(6) NULL,
                                 mo_ta text NULL,
                                 co_con bool DEFAULT false NOT NULL,
                                 da_xoa bool DEFAULT false NOT NULL,
                                 hien_thi bool DEFAULT true NOT NULL,
                                 cap int4 NULL,
                                 ten_danh_muc varchar(100) NOT NULL,
                                 tong_san_pham int4 DEFAULT 0 NOT NULL,
                                 duong_dan_day_du text NULL,
                                 duong_dan varchar(100) NULL,
                                 id_danh_muc_cha varchar(36) NULL,
                                 CONSTRAINT danh_muc_pkey PRIMARY KEY (id_danh_muc),
    CONSTRAINT fkru5gr4onfkt5doeemc6y691ax FOREIGN KEY (id_danh_muc_cha) REFERENCES public.danh_muc(id_danh_muc)
    );
ALTER TABLE public.danh_muc OWNER TO postgres;
GRANT ALL ON TABLE public.danh_muc TO postgres;

-- 2. Bảng Giá trị thuộc tính (gia_tri_thuoc_tinh)
-- Phụ thuộc vào: thuoc_tinh
CREATE TABLE public.gia_tri_thuoc_tinh (
                                           id_gia_tri_thuoc_tinh varchar(36) NOT NULL,
                                           ngay_tao timestamp(6) NULL,
                                           thu_tu_hien_thi int4 DEFAULT 0 NULL,
                                           mac_dinh bool DEFAULT false NULL,
                                           du_lieu_mo_ta text NULL,
                                           ngay_cap_nhat timestamp(6) NULL,
                                           gia_tri varchar(255) NOT NULL,
                                           id_thuoc_tinh varchar(36) NOT NULL,
                                           CONSTRAINT gia_tri_thuoc_tinh_pkey PRIMARY KEY (id_gia_tri_thuoc_tinh),
                                           CONSTRAINT uk_giatrithuoctinh_khoa_giatri UNIQUE (id_thuoc_tinh, gia_tri),
    CONSTRAINT fk5o1gx280qdmg5lunj9iaspylu FOREIGN KEY (id_thuoc_tinh) REFERENCES public.thuoc_tinh(id_thuoc_tinh)
    );
ALTER TABLE public.gia_tri_thuoc_tinh OWNER TO postgres;
GRANT ALL ON TABLE public.gia_tri_thuoc_tinh TO postgres;

-- 3. Bảng Huyện (huyen)
-- Phụ thuộc vào: tinh
CREATE TABLE public.huyen (
                              id_huyen varchar(36) NOT NULL,
                              ten_huyen varchar(100) NOT NULL,
                              id_tinh varchar(36) NOT NULL,
                              CONSTRAINT huyen_pkey PRIMARY KEY (id_huyen),
    CONSTRAINT fkgeetcgvdsjub12jb5engn84d9 FOREIGN KEY (id_tinh) REFERENCES public.tinh(id_tinh)
    );
ALTER TABLE public.huyen OWNER TO postgres;
GRANT ALL ON TABLE public.huyen TO postgres;

-- 4. Bảng Người dùng (nguoi_dung)
-- Phụ thuộc vào: vai_tro
CREATE TABLE public.nguoi_dung (
                                   id_nguoi_dung varchar(36) NOT NULL,
                                   anh_dai_dien varchar(255) NULL,
                                   ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                   ly_do_xoa text NULL,
                                   da_xoa bool DEFAULT false NULL,
                                   ngay_sinh date NULL,
                                   email varchar(100) NOT NULL,
                                   tu_mangxh bool DEFAULT false NULL,
                                   ho_ten varchar(100) NULL,
                                   gioi_tinh varchar(255) NULL,
                                   mat_khau varchar(255) NULL,
                                   so_dien_thoai varchar(20) NULL,
                                   ngay_cap_nhat timestamp(6) NULL,
                                   ten_dang_nhap varchar(50) NOT NULL,
                                   da_xac_thuc bool DEFAULT false NULL,
                                   id_vai_tro varchar(36) NULL,
                                   CONSTRAINT idx_nguoidung_email UNIQUE (email),
                                   CONSTRAINT idx_nguoidung_tendangnhap UNIQUE (ten_dang_nhap),
                                   CONSTRAINT nguoi_dung_gioi_tinh_check CHECK (((gioi_tinh)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'OTHER'::character varying])::text[]))),
                                   CONSTRAINT nguoi_dung_pkey PRIMARY KEY (id_nguoi_dung),
    CONSTRAINT fksuvgf33tfdlh0i4vyo6edecvl FOREIGN KEY (id_vai_tro) REFERENCES public.vai_tro(id_vai_tro)
    );
ALTER TABLE public.nguoi_dung OWNER TO postgres;
GRANT ALL ON TABLE public.nguoi_dung TO postgres;

-- ========================================================
-- NHÓM 3: ĐỊA CHỈ VÀ CÁC BẢNG LIÊN QUAN ĐẾN NGƯỜI DÙNG
-- ========================================================

-- 1. Bảng Xã (xa)
-- Phụ thuộc vào: huyen
CREATE TABLE public.xa (
                           id_xa varchar(36) NOT NULL,
                           ten_xa varchar(100) NOT NULL,
                           id_huyen varchar(36) NOT NULL,
                           CONSTRAINT xa_pkey PRIMARY KEY (id_xa),
                           CONSTRAINT fkr10jf325f08v1xurksmwe7cir FOREIGN KEY (id_huyen) REFERENCES public.huyen(id_huyen)
);
ALTER TABLE public.xa OWNER TO postgres;
GRANT ALL ON TABLE public.xa TO postgres;

-- 2. Bảng Địa chỉ người dùng (dia_chi_nguoi_dung)
-- Phụ thuộc vào: tinh, huyen, xa, nguoi_dung
CREATE TABLE public.dia_chi_nguoi_dung (
                                           id_dia_chi_nguoi_dung varchar(36) NOT NULL,
                                           chi_tiet text NOT NULL,
                                           so_dien_thoai varchar(20) NOT NULL,
                                           dia_chi_mac_dinh bool DEFAULT false NULL,
                                           nguoi_nhan varchar(100) NOT NULL,
                                           id_huyen varchar(36) NOT NULL,
                                           id_tinh varchar(36) NOT NULL,
                                           id_nguoi_dung varchar(36) NOT NULL,
                                           id_xa varchar(36) NOT NULL,
                                           CONSTRAINT dia_chi_nguoi_dung_pkey PRIMARY KEY (id_dia_chi_nguoi_dung),
                                           CONSTRAINT fk19wch0pqjt4q6qsw4du2d27qm FOREIGN KEY (id_huyen) REFERENCES public.huyen(id_huyen),
                                           CONSTRAINT fk4ud7s0si11mnq7nkhc7bh4vyb FOREIGN KEY (id_xa) REFERENCES public.xa(id_xa),
                                           CONSTRAINT fkilird8t69twn8u8c8ukhso6ir FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                           CONSTRAINT fkqgmda9qbpnmqkbqq7ph629oxa FOREIGN KEY (id_tinh) REFERENCES public.tinh(id_tinh)
);
ALTER TABLE public.dia_chi_nguoi_dung OWNER TO postgres;
GRANT ALL ON TABLE public.dia_chi_nguoi_dung TO postgres;

-- 3. Bảng Giỏ hàng (gio_hang)
-- Phụ thuộc vào: nguoi_dung
CREATE TABLE public.gio_hang (
                                 id_gio_hang varchar(36) NOT NULL,
                                 ngay_cap_nhat timestamp(6) NULL,
                                 id_nguoi_dung varchar(36) NOT NULL,
                                 CONSTRAINT gio_hang_pkey PRIMARY KEY (id_gio_hang),
                                 CONSTRAINT fk2u78fj8dii7flm08i96u70nbo FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung)
);
ALTER TABLE public.gio_hang OWNER TO postgres;
GRANT ALL ON TABLE public.gio_hang TO postgres;

-- 4. Bảng Thông báo người dùng (thong_bao_nguoi_dung)
-- Phụ thuộc vào: nguoi_dung
CREATE TABLE public.thong_bao_nguoi_dung (
                                             id_thong_bao_nguoi_dung varchar(36) NOT NULL,
                                             noi_dung text NOT NULL,
                                             ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                             da_doc bool DEFAULT false NULL,
                                             duong_dan_chuyen_huong varchar(255) NULL,
                                             anh_dai_dien varchar(255) NULL,
                                             ngay_cap_nhat timestamp(6) NULL,
                                             id_nguoi_dung varchar(36) NOT NULL,
                                             CONSTRAINT thong_bao_nguoi_dung_pkey PRIMARY KEY (id_thong_bao_nguoi_dung),
                                             CONSTRAINT fkr9uq3ve6x6ypcuga1mcld82jf FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung)
);
ALTER TABLE public.thong_bao_nguoi_dung OWNER TO postgres;
GRANT ALL ON TABLE public.thong_bao_nguoi_dung TO postgres;

-- 5. Bảng Lịch sử đăng nhập (lich_su_dang_nhap)
-- Phụ thuộc vào: nguoi_dung
CREATE TABLE public.lich_su_dang_nhap (
                                          id_lich_su_dang_nhap varchar(36) NOT NULL,
                                          thiet_bi varchar(100) NULL,
                                          dia_chiip varchar(45) NOT NULL,
                                          vi_tri varchar(100) NULL,
                                          thoi_gian_dang_nhap timestamp(6) NOT NULL,
                                          thoi_gian_dang_xuat timestamp(6) NULL,
                                          user_agent varchar(255) NULL,
                                          id_nguoi_dung varchar(36) NOT NULL,
                                          CONSTRAINT lich_su_dang_nhap_pkey PRIMARY KEY (id_lich_su_dang_nhap),
                                          CONSTRAINT fk46a274f1gtlwsekfe7cbfbvqc FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung)
);
CREATE INDEX idx_lichsudangnhap_nguoidung ON public.lich_su_dang_nhap USING btree (id_nguoi_dung);
CREATE INDEX idx_lichsudangnhap_thoigiandangnhap ON public.lich_su_dang_nhap USING btree (thoi_gian_dang_nhap);
ALTER TABLE public.lich_su_dang_nhap OWNER TO postgres;
GRANT ALL ON TABLE public.lich_su_dang_nhap TO postgres;

-- 6. Bảng Hiển thị danh mục (hien_thi_danh_muc)
-- Phụ thuộc vào: danh_muc
CREATE TABLE public.hien_thi_danh_muc (
                                          id_hien_thi_danh_muc varchar(36) NOT NULL,
                                          thu_tu_hien_thi int4 NOT NULL,
                                          loai_media varchar(255) NULL,
                                          vi_tri varchar(255) NULL,
                                          anh_dai_dien varchar(255) NULL,
                                          id_danh_muc varchar(36) NOT NULL,
                                          CONSTRAINT hien_thi_danh_muc_loai_media_check CHECK (((loai_media)::text = ANY ((ARRAY['IMAGE'::character varying, 'VIDEO'::character varying])::text[]))),
                                          CONSTRAINT hien_thi_danh_muc_pkey PRIMARY KEY (id_hien_thi_danh_muc),
                                          CONSTRAINT hien_thi_danh_muc_vi_tri_check CHECK (((vi_tri)::text = ANY ((ARRAY['HOMEPAGE'::character varying, 'SIDEBAR'::character varying])::text[]))),
                                          CONSTRAINT fkica36u84cpim4ke7cpaerfi62 FOREIGN KEY (id_danh_muc) REFERENCES public.danh_muc(id_danh_muc)
);
ALTER TABLE public.hien_thi_danh_muc OWNER TO postgres;
GRANT ALL ON TABLE public.hien_thi_danh_muc TO postgres;

-- 7. Bảng Thành viên phòng chat (phong_chat_thanh_vien)
-- Phụ thuộc vào: phong_chat, nguoi_dung
CREATE TABLE public.phong_chat_thanh_vien (
                                              id_phong_chat varchar(36) NOT NULL,
                                              id_nguoi_dung varchar(36) NOT NULL,
                                              CONSTRAINT phong_chat_thanh_vien_pkey PRIMARY KEY (id_phong_chat, id_nguoi_dung),
                                              CONSTRAINT fke3k9algfg7in512jd53wspktl FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                              CONSTRAINT fkk8m6g8i8xchw9j6b4ye1icw4u FOREIGN KEY (id_phong_chat) REFERENCES public.phong_chat(id_phong_chat)
);
ALTER TABLE public.phong_chat_thanh_vien OWNER TO postgres;
GRANT ALL ON TABLE public.phong_chat_thanh_vien TO postgres;

-- 8. Bảng Tin nhắn (tin_nhan)
-- Phụ thuộc vào: phong_chat, nguoi_dung
CREATE TABLE public.tin_nhan (
                                 id_tin_nhan varchar(36) NOT NULL,
                                 noi_dung text NULL,
                                 ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                 da_doc bool DEFAULT false NULL,
                                 loai_tin_nhan varchar(255) NOT NULL,
                                 id_phong_chat varchar(36) NOT NULL,
                                 id_nguoi_dung varchar(36) NOT NULL,
                                 CONSTRAINT tin_nhan_loai_tin_nhan_check CHECK (((loai_tin_nhan)::text = ANY ((ARRAY['TEXT'::character varying, 'MEDIA'::character varying])::text[]))),
                                 CONSTRAINT tin_nhan_pkey PRIMARY KEY (id_tin_nhan),
                                 CONSTRAINT fk6qots5hs8qprp58ev9snwjfy9 FOREIGN KEY (id_phong_chat) REFERENCES public.phong_chat(id_phong_chat),
                                 CONSTRAINT fkhll7rh7oo8rh0rq0ok2pvre42 FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung)
);
ALTER TABLE public.tin_nhan OWNER TO postgres;
GRANT ALL ON TABLE public.tin_nhan TO postgres;


-- ========================================================
-- NHÓM 4: CỬA HÀNG VÀ SẢN PHẨM
-- ========================================================

-- 1. Bảng Cửa hàng (cua_hang)
-- Phụ thuộc vào: nguoi_dung
CREATE TABLE public.cua_hang (
                                 id_cua_hang varchar(36) NOT NULL,
                                 anh_dai_dien varchar(255) NULL,
                                 diem_danh_giatb float8 DEFAULT 0 NULL,
                                 ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                 da_xoa bool DEFAULT false NULL,
                                 mo_ta text NULL,
                                 luot_theo_doi int4 DEFAULT 0 NULL,
                                 ten_cua_hang varchar(100) NOT NULL,
                                 tongsp int4 DEFAULT 0 NULL,
                                 doanh_thu int8 DEFAULT 0 NULL,
                                 tong_danh_gia int4 DEFAULT 0 NULL,
                                 ngay_cap_nhat timestamp(6) NULL,
                                 duong_dan text NULL,
                                 id_nguoi_dung varchar(36) NOT NULL,
                                 CONSTRAINT cua_hang_pkey PRIMARY KEY (id_cua_hang),
                                 CONSTRAINT fk1yiasf2ahk01uijc1hncvduao FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung)
);
ALTER TABLE public.cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.cua_hang TO postgres;

-- 2. Bảng Địa chỉ Shop
-- (dia_chi_shop)
-- Phụ thuộc vào: cua_hang, tinh, huyen, xa
CREATE TABLE public.dia_chi_cua_hang (
                                     id_dia_chi_cua_hang varchar(36) NOT NULL,
                                     chi_tiet text NOT NULL,
                                     so_dien_thoai varchar(20) NOT NULL,
                                     dia_chi_mac_dinh bool DEFAULT false NULL,
                                     nguoi_gui varchar(100) NOT NULL,
                                     id_huyen varchar(36) NOT NULL,
                                     id_tinh varchar(36) NOT NULL,
                                     id_cua_hang varchar(36) NOT NULL,
                                     id_xa varchar(36) NOT NULL,
                                     CONSTRAINT dia_chi_cua_hang_pkey PRIMARY KEY (id_dia_chi_cua_hang),
                                     CONSTRAINT fkd5198ypdlad1klax27p3vqteb FOREIGN KEY (id_tinh) REFERENCES public.tinh(id_tinh),
                                     CONSTRAINT fke5mf6u4ifkc9ajy14r0ssi8po FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang),
                                     CONSTRAINT fknrxf96p5pmkyix7tynyf71hx2 FOREIGN KEY (id_huyen) REFERENCES public.huyen(id_huyen),
                                     CONSTRAINT fkr2xys7vsml8is6b0jonqdgeub FOREIGN KEY (id_xa) REFERENCES public.xa(id_xa)
);
ALTER TABLE public.dia_chi_cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.dia_chi_cua_hang TO postgres;

-- 3. Bảng Theo dõi (theo_doi)
-- Phụ thuộc vào: nguoi_dung (id_user), cua_hang
CREATE TABLE public.theo_doi (
                                 id_theo_doi varchar(36) NOT NULL,
                                 thoi_gian_theo_doi timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                 id_cua_hang varchar(36) NOT NULL,
                                 id_nguoi_dung varchar(36) NOT NULL,
                                 CONSTRAINT theo_doi_pkey PRIMARY KEY (id_theo_doi),
                                 CONSTRAINT uk_user_shop_follow UNIQUE (id_nguoi_dung, id_cua_hang),
                                 CONSTRAINT fk_theodoi_nguoidung FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                 CONSTRAINT fk_theodoi_cuahang FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang)
);
ALTER TABLE public.theo_doi OWNER TO postgres;
GRANT ALL ON TABLE public.theo_doi TO postgres;

-- 4. Bảng Thông báo cửa hàng (thong_bao_cua_hang)
-- Phụ thuộc vào: cua_hang
CREATE TABLE public.thong_bao_cua_hang (
                                           id_thong_bao_cua_hang varchar(36) NOT NULL,
                                           noi_dung text NOT NULL,
                                           ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                           da_doc bool DEFAULT false NULL,
                                           duong_dan_chuyen_huong varchar(255) NULL,
                                           anh_dai_dien varchar(255) NULL,
                                           ngay_cap_nhat timestamp(6) NULL,
                                           id_cua_hang varchar(36) NOT NULL,
                                           CONSTRAINT thong_bao_cua_hang_pkey PRIMARY KEY (id_thong_bao_cua_hang),
                                           CONSTRAINT fk89ookkhc84nsf4awhb2maln78 FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang)
);
ALTER TABLE public.thong_bao_cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.thong_bao_cua_hang TO postgres;

-- 5. Bảng Danh mục cửa hàng (danh_muc_cua_hang)
-- Phụ thuộc vào: cua_hang
CREATE TABLE public.danh_muc_cua_hang (
                                          id_danh_muc_cua_hang varchar(36) NOT NULL,
                                          id_cua_hang varchar(36) NOT NULL,
                                          CONSTRAINT danh_muc_cua_hang_pkey PRIMARY KEY (id_danh_muc_cua_hang),
                                          CONSTRAINT fkg96om833nnrr6qiyv9ex2b6tc FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang)
);
ALTER TABLE public.danh_muc_cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.danh_muc_cua_hang TO postgres;

-- 6. Bảng Sản phẩm (san_pham)
-- Phụ thuộc vào: danh_muc, cua_hang
CREATE TABLE public.san_pham (
                                 id_san_pham varchar(36) NOT NULL,
                                 diem_danh_giatb float4 NOT NULL,
                                 ngay_tao timestamp(6) NULL,
                                 da_xoa bool NOT NULL,
                                 mo_ta text NULL,
                                 gia_giam int8 NOT NULL,
                                 chieu_cao int8 NOT NULL,
                                 chieu_dai int8 NOT NULL,
                                 ten_san_pham varchar(255) NOT NULL,
                                 gia_goc int8 NOT NULL,
                                 so_luong int4 NOT NULL,
                                 trang_thai_han_che varchar(255) NOT NULL,
                                 bi_han_che bool NOT NULL,
                                 ly_do_han_che text NULL,
                                 doanh_thu int8 NOT NULL,
                                 da_ban int4 NOT NULL,
                                 mo_ta_ngan text NULL,
                                 anh_dai_dien varchar(255) NULL,
                                 tong_danh_gia int4 NOT NULL,
                                 ngay_cap_nhat timestamp(6) NULL,
                                 duong_dan text NULL,
                                 hien_thi bool NOT NULL,
                                 khoi_luong int8 NOT NULL,
                                 chieu_rong int8 NOT NULL,
                                 id_danh_muc varchar(36) NULL,
                                 id_cua_hang varchar(36) NOT NULL,
                                 CONSTRAINT san_pham_pkey PRIMARY KEY (id_san_pham),
                                 CONSTRAINT san_pham_trang_thai_han_che_check CHECK (((trang_thai_han_che)::text = ANY ((ARRAY['ALL'::character varying, 'ACTIVE'::character varying, 'VIOLATION'::character varying, 'PENDING'::character varying, 'HIDDEN'::character varying])::text[]))),
                                 CONSTRAINT fkd58u2abkk8djfe7kfatndp012 FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang),
                                 CONSTRAINT fkk8b4wwituxbxbcudtvqie796j FOREIGN KEY (id_danh_muc) REFERENCES public.danh_muc(id_danh_muc)
);
ALTER TABLE public.san_pham OWNER TO postgres;
GRANT ALL ON TABLE public.san_pham TO postgres;

-- 7. Bảng Sản phẩm SKU (san_phamsku)
-- Phụ thuộc vào: san_pham
CREATE TABLE public.san_phamsku (
                                    id_san_phamsku varchar(36) NOT NULL,
                                    ngay_tao timestamp(6) NULL,
                                    gia int8 NOT NULL,
                                    so_luong int4 DEFAULT 0 NULL,
                                    masku varchar(50) NOT NULL,
                                    da_ban int4 NOT NULL,
                                    anh_dai_dien varchar(255) NULL,
                                    ngay_cap_nhat timestamp(6) NULL,
                                    id_san_pham varchar(36) NOT NULL,
                                    CONSTRAINT san_phamsku_pkey PRIMARY KEY (id_san_phamsku),
                                    CONSTRAINT fkbi5llaiyrmsktmk4f7qdjtauy FOREIGN KEY (id_san_pham) REFERENCES public.san_pham(id_san_pham)
);
ALTER TABLE public.san_phamsku OWNER TO postgres;
GRANT ALL ON TABLE public.san_phamsku TO postgres;

-- 8. Bảng Media sản phẩm (media_san_pham)
-- Phụ thuộc vào: san_pham
CREATE TABLE public.media_san_pham (
                                       id_media_san_pham varchar(36) NOT NULL,
                                       anh_dai_dien varchar(255) NOT NULL,
                                       loai_media varchar(255) NOT NULL,
                                       duong_dan varchar(255) NOT NULL,
                                       id_san_pham varchar(36) NOT NULL,
                                       CONSTRAINT media_san_pham_loai_media_check CHECK (((loai_media)::text = ANY ((ARRAY['IMAGE'::character varying, 'VIDEO'::character varying])::text[]))),
                                       CONSTRAINT media_san_pham_pkey PRIMARY KEY (id_media_san_pham),
                                       CONSTRAINT fkknpiqd3d7hhgg5gadjrn9b9ie FOREIGN KEY (id_san_pham) REFERENCES public.san_pham(id_san_pham)
);
ALTER TABLE public.media_san_pham OWNER TO postgres;
GRANT ALL ON TABLE public.media_san_pham TO postgres;


-- ========================================================
-- NHÓM 5: ĐƠN HÀNG VÀ VẬN CHUYỂN
-- ========================================================

-- 1. Bảng Đơn hàng (don_hang)
-- Phụ thuộc vào: thanh_toan, dia_chi_nguoi_dung, nguoi_dung
CREATE TABLE public.don_hang (
                                 id_don_hang varchar(36) NOT NULL,
                                 ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                 tong_tien int8 NOT NULL,
                                 trang_thai varchar(255) NOT NULL,
                                 tam_tinh int8 NULL,
                                 tong_phi_van_chuyen int8 NULL,
                                 ngay_cap_nhat timestamp(6) NULL,
                                 id_thanh_toan varchar(36) NULL,
                                 id_dia_chi_nguoi_dung varchar(36) NOT NULL,
                                 id_nguoi_dung varchar(36) NOT NULL,
                                 CONSTRAINT don_hang_pkey PRIMARY KEY (id_don_hang),
                                 CONSTRAINT don_hang_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['PROCESSING'::character varying, 'SUCCESS'::character varying, 'CANCELED'::character varying])::text[]))),
                                 CONSTRAINT ukqjrnvbm2sdw3qi8871yfr0ivp UNIQUE (id_thanh_toan),
                                 CONSTRAINT fkc5tkognwtgw8fw3dnpylmoad0 FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                 CONSTRAINT fkn0hu63ofdu8ym4a4knv3rimw5 FOREIGN KEY (id_dia_chi_nguoi_dung) REFERENCES public.dia_chi_nguoi_dung(id_dia_chi_nguoi_dung),
                                 CONSTRAINT fkrspgexpthb7jujuqrnxpnr17w FOREIGN KEY (id_thanh_toan) REFERENCES public.thanh_toan(id_thanh_toan)
);
ALTER TABLE public.don_hang OWNER TO postgres;
GRANT ALL ON TABLE public.don_hang TO postgres;

-- 2. Bảng Đơn hàng của cửa hàng (don_hang_cua_hang)
-- Phụ thuộc vào: don_hang, dia_chi_shop, cua_hang, nguoi_dung
CREATE TABLE public.don_hang_cua_hang (
                                          id_don_hang_cua_hang varchar(36) NOT NULL,
                                          ngay_tao timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                          phi_giao_hang int8 NOT NULL,
                                          trang_thai varchar(255) NULL,
                                          tong_tien int8 NOT NULL,
                                          tong_khoi_luong int8 NOT NULL,
                                          ngay_cap_nhat timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                          id_don_hang varchar(36) NOT NULL,
                                          id_dia_chi_cua_hang varchar(36) NOT NULL,
                                          id_cua_hang varchar(36) NOT NULL,
                                          id_nguoi_dung varchar(36) NOT NULL,
                                          CONSTRAINT don_hang_cua_hang_pkey PRIMARY KEY (id_don_hang_cua_hang),
                                          CONSTRAINT don_hang_cua_hang_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['INIT_PROCESSING'::character varying, 'PENDING_CONFIRMATION'::character varying, 'CONFIRMED'::character varying, 'PREPARING'::character varying, 'SHIPPING'::character varying, 'DELIVERED'::character varying, 'CLOSED'::character varying])::text[]))),
                                          CONSTRAINT fk16d92q883uq4akhgqhwnd42ug FOREIGN KEY (id_don_hang) REFERENCES public.don_hang(id_don_hang),
                                          CONSTRAINT fk3l9jm8vqdls9yu6v8jhppdiud FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang),
                                          CONSTRAINT fkl5i3s4wvk8v2o06tgw6rri4va FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                          CONSTRAINT fkos6kbas8wnu7pbblfo7r4c7ws FOREIGN KEY (id_dia_chi_cua_hang) REFERENCES public.dia_chi_cua_hang(id_dia_chi_cua_hang)
);
ALTER TABLE public.don_hang_cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.don_hang_cua_hang TO postgres;

-- 3. Bảng Item đơn hàng (item_don_hang)
-- Phụ thuộc vào: don_hang, san_phamsku, don_hang_cua_hang
CREATE TABLE public.item_don_hang (
                                      id_item_don_hang varchar(36) NOT NULL,
                                      ly_do_huy text NULL,
                                      huy_boi varchar(255) NULL,
                                      ngay_tao timestamp(6) NULL,
                                      gia int8 NOT NULL,
                                      so_luong int4 NOT NULL,
                                      trang_thai varchar(255) NULL,
                                      thanh_cong bool DEFAULT false NULL,
                                      ngay_cap_nhat timestamp(6) NULL,
                                      id_don_hang varchar(36) NOT NULL,
                                      id_san_phamsku varchar(36) NOT NULL,
                                      id_don_hang_cua_hang varchar(36) NOT NULL,
                                      CONSTRAINT item_don_hang_huy_boi_check CHECK (((huy_boi)::text = ANY ((ARRAY['CUSTOMER'::character varying, 'SHOP'::character varying, 'ADMIN'::character varying])::text[]))),
                                      CONSTRAINT item_don_hang_pkey PRIMARY KEY (id_item_don_hang),
                                      CONSTRAINT item_don_hang_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['PROCESSING'::character varying, 'PENDING_CONFIRMATION'::character varying, 'PREPARING'::character varying, 'WAIT_SHIPMENT'::character varying, 'SHIPPING'::character varying, 'DELIVERED'::character varying, 'REFUND'::character varying, 'CANCELED'::character varying])::text[]))),
                                      CONSTRAINT fk6bdb8al23ds9ek2cx8c249mhl FOREIGN KEY (id_don_hang) REFERENCES public.don_hang(id_don_hang),
                                      CONSTRAINT fklw37476jcp7efhdydpvstv8y8 FOREIGN KEY (id_san_phamsku) REFERENCES public.san_phamsku(id_san_phamsku),
                                      CONSTRAINT fkpd6wbrjxa9sribjku1n9aigf7 FOREIGN KEY (id_don_hang_cua_hang) REFERENCES public.don_hang_cua_hang(id_don_hang_cua_hang)
);
ALTER TABLE public.item_don_hang OWNER TO postgres;
GRANT ALL ON TABLE public.item_don_hang TO postgres;

-- 4. Bảng Vận chuyển (van_chuyen)
-- Phụ thuộc vào: dia_chi_shop, dia_chi_nguoi_dung, don_hang_cua_hang
CREATE TABLE public.van_chuyen (
                                   id_van_chuyen varchar(36) NOT NULL,
                                   ly_do_huy varchar(500) NULL,
                                   ngay_tao timestamp(6) NULL,
                                   ngay_lay_hang date NOT NULL,
                                   phi_van_chuyen int8 NOT NULL,
                                   da_in bool NOT NULL,
                                   ghi_chu varchar(255) NULL,
                                   ten_don_vi_giao varchar(100) NOT NULL,
                                   sdtdon_vi_giao varchar(20) NOT NULL,
                                   trang_thai varchar(255) NOT NULL,
                                   tong_khoi_luong int8 NOT NULL,
                                   id_dia_chi_cua_hang varchar(36) NOT NULL,
                                   id_dia_chi_nguoi_dung varchar(36) NOT NULL,
                                   id_don_hang_cua_hang varchar(36) NOT NULL,
                                   CONSTRAINT ukqs411ruu1p3p1tpakix7b2qve UNIQUE (id_don_hang_cua_hang),
                                   CONSTRAINT van_chuyen_pkey PRIMARY KEY (id_van_chuyen),
                                   CONSTRAINT van_chuyen_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['ORDER_CREATED'::character varying, 'WAITING_FOR_PICKUP'::character varying, 'PICKED_UP'::character varying, 'IN_TRANSIT'::character varying, 'OUT_FOR_DELIVERY'::character varying, 'DELIVERED'::character varying, 'DELIVERY_FAILED'::character varying, 'RETURNED'::character varying])::text[]))),
                                   CONSTRAINT fk66xdq4du4cis71yuakk98p28n FOREIGN KEY (id_dia_chi_nguoi_dung) REFERENCES public.dia_chi_nguoi_dung(id_dia_chi_nguoi_dung),
                                   CONSTRAINT fkho2dfwfg1fe51tbhpgb1evf9b FOREIGN KEY (id_don_hang_cua_hang) REFERENCES public.don_hang_cua_hang(id_don_hang_cua_hang),
                                   CONSTRAINT fkmxlqlfmijr5ly0c65shq8uvsp FOREIGN KEY (id_dia_chi_cua_hang) REFERENCES public.dia_chi_cua_hang(id_dia_chi_cua_hang)
);
ALTER TABLE public.van_chuyen OWNER TO postgres;
GRANT ALL ON TABLE public.van_chuyen TO postgres;


-- ========================================================
-- NHÓM 6: CÁC BẢNG LIÊN KẾT, ĐÁNH GIÁ VÀ THEO DÕI
-- ========================================================

-- 1. Bảng Item giỏ hàng (gio_hang_item)
-- Phụ thuộc vào: gio_hang, san_phamsku
CREATE TABLE public.gio_hang_item (
                                      id_gio_hang_item varchar(36) NOT NULL,
                                      gia int8 NOT NULL,
                                      so_luong int4 DEFAULT 1 NULL,
                                      duoc_chon bool DEFAULT true NULL,
                                      id_gio_hang varchar(36) NOT NULL,
                                      id_san_phamsku varchar(36) NOT NULL,
                                      CONSTRAINT gio_hang_item_pkey PRIMARY KEY (id_gio_hang_item),
                                      CONSTRAINT fk3tlitj5uoounj67vfs1jddlax FOREIGN KEY (id_gio_hang) REFERENCES public.gio_hang(id_gio_hang),
                                      CONSTRAINT fkltqol4xlx4djkx4sb4kg7w15x FOREIGN KEY (id_san_phamsku) REFERENCES public.san_phamsku(id_san_phamsku)
);
ALTER TABLE public.gio_hang_item OWNER TO postgres;
GRANT ALL ON TABLE public.gio_hang_item TO postgres; 

-- 2. Bảng Liên kết SKU và Thuộc tính (sku_thuoc_tinh)
-- Phụ thuộc vào: san_phamsku, gia_tri_thuoc_tinh
CREATE TABLE public.sku_thuoc_tinh (
                                       id_san_phamsku varchar(36) NOT NULL,
                                       id_gia_tri_thuoc_tinh varchar(36) NOT NULL,
                                       CONSTRAINT sku_thuoc_tinh_pkey PRIMARY KEY (id_san_phamsku, id_gia_tri_thuoc_tinh),
                                       CONSTRAINT fk4conbbhyvu0in0tv6pleq0wgi FOREIGN KEY (id_san_phamsku) REFERENCES public.san_phamsku(id_san_phamsku),
                                       CONSTRAINT fk4kfxuq71qy6um3i6jl9xn0v15 FOREIGN KEY (id_gia_tri_thuoc_tinh) REFERENCES public.gia_tri_thuoc_tinh(id_gia_tri_thuoc_tinh)
);
ALTER TABLE public.sku_thuoc_tinh OWNER TO postgres;
GRANT ALL ON TABLE public.sku_thuoc_tinh TO postgres; 

CREATE TABLE public.san_pham_thuoc_tinh_chung (
                                                  id_gia_tri_thuoc_tinh varchar(36) NOT NULL,
                                                  id_san_pham varchar(36) NOT NULL,
                                                  CONSTRAINT san_pham_thuoc_tinh_chung_pkey PRIMARY KEY (id_gia_tri_thuoc_tinh, id_san_pham),
                                                  CONSTRAINT fk5uo5qqddb55s4wcawfhj7wjy5 FOREIGN KEY (id_gia_tri_thuoc_tinh) REFERENCES public.gia_tri_thuoc_tinh(id_gia_tri_thuoc_tinh),
                                                  CONSTRAINT fk6wx5bih63mx7pxilrxen690u8 FOREIGN KEY (id_san_pham) REFERENCES public.san_pham(id_san_pham)
);
ALTER TABLE public.san_pham_thuoc_tinh_chung OWNER TO postgres;
GRANT ALL ON TABLE public.san_pham_thuoc_tinh_chung TO postgres; 

-- 4. Bảng Đánh giá sản phẩm (danh_gia_san_pham)
-- Phụ thuộc vào: nguoi_dung, san_pham, item_don_hang
CREATE TABLE public.danh_gia_san_pham (
                                          id_danh_gia_san_pham varchar(36) NOT NULL,
                                          binh_luan text NULL,
                                          ngay_tao timestamp(6) NULL,
                                          diem_danh_gia int4 NOT NULL,
                                          ngay_cap_nhat timestamp(6) NULL,
                                          id_item_don_hang varchar(36) NULL,
                                          id_san_pham varchar(36) NOT NULL,
                                          id_nguoi_dung varchar(36) NOT NULL,
                                          CONSTRAINT danh_gia_san_pham_pkey PRIMARY KEY (id_danh_gia_san_pham),
                                          CONSTRAINT uknmyq31tvfvseo2yr8cy0gfflp UNIQUE (id_item_don_hang),
                                          CONSTRAINT fk17nf4ylmnmmuqidlsl5vmot3v FOREIGN KEY (id_nguoi_dung) REFERENCES public.nguoi_dung(id_nguoi_dung),
                                          CONSTRAINT fkbi9oecnm517cffsvogwr2cqnq FOREIGN KEY (id_san_pham) REFERENCES public.san_pham(id_san_pham),
                                          CONSTRAINT fko903uwi8dkxh9hais3hih6pe8 FOREIGN KEY (id_item_don_hang) REFERENCES public.item_don_hang(id_item_don_hang)
);
CREATE INDEX idx_danhgiasanpham_user_product ON public.danh_gia_san_pham USING btree (id_nguoi_dung, id_san_pham);
ALTER TABLE public.danh_gia_san_pham OWNER TO postgres;
GRANT ALL ON TABLE public.danh_gia_san_pham TO postgres; 

-- 5. Bảng Media đánh giá (danh_gia_san_pham_media)
-- Phụ thuộc vào: danh_gia_san_pham
CREATE TABLE public.danh_gia_san_pham_media (
                                                id_media_danh_gia_san_pham varchar(36) NOT NULL,
                                                ngay_tao timestamp(6) NULL,
                                                loai_media varchar(10) NOT NULL,
                                                duong_dan_media varchar(255) NOT NULL,
                                                id_danh_gia_san_pham varchar(36) NOT NULL,
                                                CONSTRAINT danh_gia_san_pham_media_loai_media_check CHECK (((loai_media)::text = ANY ((ARRAY['IMAGE'::character varying, 'VIDEO'::character varying])::text[]))),
                                                CONSTRAINT danh_gia_san_pham_media_pkey PRIMARY KEY (id_media_danh_gia_san_pham),
                                                CONSTRAINT fklxxg1rftf9qs3vht0isgeh1f9 FOREIGN KEY (id_danh_gia_san_pham) REFERENCES public.danh_gia_san_pham(id_danh_gia_san_pham)
);
ALTER TABLE public.danh_gia_san_pham_media OWNER TO postgres;
GRANT ALL ON TABLE public.danh_gia_san_pham_media TO postgres; 

-- 6. Các bảng Theo dõi trạng thái (Tracking)
-- Phụ thuộc vào các bảng đơn hàng và vận chuyển tương ứng

-- Theo dõi đơn hàng shop
CREATE TABLE public.theo_doi_don_hang_cua_hang (
                                                   cap_nhat_luc timestamp(6) NOT NULL,
                                                   trang_thai varchar(255) NOT NULL,
                                                   id_donhangcuahang varchar(255) NOT NULL,
                                                   id_don_hang_cua_hang varchar(36) NOT NULL,
                                                   CONSTRAINT theo_doi_don_hang_cua_hang_pkey PRIMARY KEY (id_donhangcuahang, cap_nhat_luc),
                                                   CONSTRAINT theo_doi_don_hang_cua_hang_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['INIT_PROCESSING'::character varying, 'PENDING_CONFIRMATION'::character varying, 'CONFIRMED'::character varying, 'PREPARING'::character varying, 'SHIPPING'::character varying, 'DELIVERED'::character varying, 'CLOSED'::character varying])::text[]))),
                                                   CONSTRAINT fkc9f6gbyyposndh5q6xy7gx7n4 FOREIGN KEY (id_don_hang_cua_hang) REFERENCES public.don_hang_cua_hang(id_don_hang_cua_hang)
);
ALTER TABLE public.theo_doi_don_hang_cua_hang OWNER TO postgres;
GRANT ALL ON TABLE public.theo_doi_don_hang_cua_hang TO postgres; 

-- Theo dõi chi tiết item đơn hàng
CREATE TABLE public.theo_doi_item_don_hang (
                                               cap_nhat_luc timestamp(6) NOT NULL,
                                               trang_thai varchar(255) NOT NULL,
                                               id_item_don_hang varchar(36) NOT NULL,
                                               CONSTRAINT theo_doi_item_don_hang_pkey PRIMARY KEY (id_item_don_hang, cap_nhat_luc),
                                               CONSTRAINT theo_doi_item_don_hang_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['PROCESSING'::character varying, 'PENDING_CONFIRMATION'::character varying, 'PREPARING'::character varying, 'WAIT_SHIPMENT'::character varying, 'SHIPPING'::character varying, 'DELIVERED'::character varying, 'REFUND'::character varying, 'CANCELED'::character varying])::text[]))),
                                               CONSTRAINT fkhrxvsngitik0cm5pidw2dj0je FOREIGN KEY (id_item_don_hang) REFERENCES public.item_don_hang(id_item_don_hang)
);
ALTER TABLE public.theo_doi_item_don_hang OWNER TO postgres;
GRANT ALL ON TABLE public.theo_doi_item_don_hang TO postgres; 

-- Theo dõi vận chuyển
CREATE TABLE public.theo_doi_van_chuyen (
                                            cap_nhat_luc timestamp(6) NOT NULL,
                                            ghi_chu varchar(255) NULL,
                                            trang_thai varchar(255) NOT NULL,
                                            id_vanchuyen varchar(255) NOT NULL,
                                            id_van_chuyen varchar(36) NOT NULL,
                                            CONSTRAINT theo_doi_van_chuyen_pkey PRIMARY KEY (id_vanchuyen, cap_nhat_luc),
                                            CONSTRAINT theo_doi_van_chuyen_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['ORDER_CREATED'::character varying, 'WAITING_FOR_PICKUP'::character varying, 'PICKED_UP'::character varying, 'IN_TRANSIT'::character varying, 'OUT_FOR_DELIVERY'::character varying, 'DELIVERED'::character varying, 'DELIVERY_FAILED'::character varying, 'RETURNED'::character varying])::text[]))),
                                            CONSTRAINT fkqw0bbhlixvbkhmo8r0gax5rrb FOREIGN KEY (id_van_chuyen) REFERENCES public.van_chuyen(id_van_chuyen)
);
ALTER TABLE public.theo_doi_van_chuyen OWNER TO postgres;
GRANT ALL ON TABLE public.theo_doi_van_chuyen TO postgres; 

-- 7. Các bảng liên kết bổ sung (Shop attributes, Category items)
CREATE TABLE public.cua_hang_thuoc_tinh (
                                        id_thuoc_tinh varchar(36) NOT NULL,
                                        id_cua_hang varchar(36) NOT NULL,
                                        CONSTRAINT shop_thuoc_tinh_pkey PRIMARY KEY (id_thuoc_tinh, id_cua_hang),
                                        CONSTRAINT fk2p3l4c6d8m7svk0c9nb9o9t9b FOREIGN KEY (id_cua_hang) REFERENCES public.cua_hang(id_cua_hang),
                                        CONSTRAINT fkl5p5mqpu88lsjlsdy942ygbyw FOREIGN KEY (id_thuoc_tinh) REFERENCES public.thuoc_tinh(id_thuoc_tinh)
                                            ); 

CREATE TABLE public.danh_muc_cua_hang_item (
                                               id_danhmuc varchar(255) NOT NULL,
                                               id_danh_muc varchar(36) NOT NULL,
                                               id_danhmuccuahang varchar(255) NOT NULL,
                                               id_danh_muc_cua_hang varchar(36) NOT NULL,
                                               CONSTRAINT danh_muc_cua_hang_item_pkey PRIMARY KEY (id_danhmuc, id_danhmuccuahang),
                                               CONSTRAINT fkay6lnm7n51qyg64hwykv4v1dk FOREIGN KEY (id_danh_muc_cua_hang) REFERENCES public.danh_muc_cua_hang(id_danh_muc_cua_hang),
                                               CONSTRAINT fks4w163lvkq5kf79fv476nejsq FOREIGN KEY (id_danh_muc) REFERENCES public.danh_muc(id_danh_muc)
                                                   ); 

CREATE TABLE public.phi_van_chuyen_don_hang_cua_hang (
                                                         id_don_hang_cua_hang varchar(36) NOT NULL,
                                                         so_tien int8 NULL,
                                                         CONSTRAINT phi_van_chuyen_don_hang_cua_hang_pkey PRIMARY KEY (id_don_hang_cua_hang),
                                                         CONSTRAINT fk79xrl47iwar5up5i0d0sk55t3 FOREIGN KEY (id_don_hang_cua_hang) REFERENCES public.don_hang_cua_hang(id_don_hang_cua_hang)
                                                             ); 


                                                                            