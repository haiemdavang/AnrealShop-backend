ALTER TABLE public.don_hang_cua_hang
    DROP CONSTRAINT don_hang_cua_hang_trang_thai_check;

-- 2. Add ràng buộc CHECK mới với danh sách đầy đủ (thêm 'SUCCESS')
ALTER TABLE public.don_hang_cua_hang
    ADD CONSTRAINT don_hang_cua_hang_trang_thai_check
        CHECK (
            (trang_thai)::text = ANY (
                ARRAY[
                    'INIT_PROCESSING',
                    'PENDING_CONFIRMATION',
                    'CONFIRMED',
                    'PREPARING',
                    'SHIPPING',
                    'DELIVERED',
                    'CLOSED',
                    'SUCCESS'  -- <-- Thêm trạng thái mới ở đây
                    ]::text[]
                )
            );