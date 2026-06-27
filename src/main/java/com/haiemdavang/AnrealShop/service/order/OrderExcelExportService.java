package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.OrderItemDto;
import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderExcelExportService {

    private static final String[] HEADERS = {
            "STT",
            "Mã đơn hàng",
            "Mã order item",
            "Khách hàng",
            "Mã vận đơn",
            "Trạng thái đơn",
            "Mã sản phẩm",
            "Mã SKU",
            "Tên sản phẩm",
            "Phân loại",
            "Số lượng",
            "Đơn giá",
            "Thành tiền",
            "Trạng thái sản phẩm",
            "Ngày xác nhận"
    };

    public byte[] export(List<OrderItemDto> orderItems) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách đơn hàng");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);
            createHeader(sheet, headerStyle);

            int rowIndex = 1;
            int sequence = 1;
            for (OrderItemDto order : orderItems) {
                List<ProductOrderItemDto> productItems = order.getProductOrderItemDtoSet()
                        .stream()
                        .sorted(Comparator.comparing(ProductOrderItemDto::getOrderItemId))
                        .toList();
                for (ProductOrderItemDto item : productItems) {
                    Row row = sheet.createRow(rowIndex++);
                    int column = 0;
                    setCell(row, column++, sequence++);
                    setCell(row, column++, order.getShopOrderId());
                    setCell(row, column++, item.getOrderItemId());
                    setCell(row, column++, order.getCustomerName());
                    setCell(row, column++, order.getShippingId());
                    setCell(row, column++, order.getOrderStatus());
                    setCell(row, column++, item.getProductId());
                    setCell(row, column++, item.getProductSkuId());
                    setCell(row, column++, item.getProductName());
                    setCell(row, column++, item.getVariant());
                    setCell(row, column++, item.getQuantity());
                    setMoneyCell(row, column++, item.getPrice(), moneyStyle);
                    long total = item.getPrice() == null
                            ? 0L
                            : item.getPrice() * item.getQuantity();
                    setMoneyCell(row, column++, total, moneyStyle);
                    setCell(row, column++, item.getOrderStatus());
                    setCell(row, column, item.getSubmitConfirmDate());
                }
            }

            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new CellRangeAddress(0, Math.max(0, rowIndex - 1), 0, HEADERS.length - 1));
            setColumnWidths(sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AnrealShopException("EXPORT_ORDER_EXCEL_FAILED");
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private void createHeader(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void setColumnWidths(Sheet sheet) {
        int[] widths = {8, 38, 38, 24, 38, 20, 38, 38, 36, 28, 12, 18, 18, 24, 24};
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256);
        }
    }

    private void setCell(Row row, int column, String value) {
        row.createCell(column).setCellValue(value == null ? "" : value);
    }

    private void setCell(Row row, int column, int value) {
        row.createCell(column).setCellValue(value);
    }

    private void setMoneyCell(Row row, int column, Long value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? 0 : value);
        cell.setCellStyle(style);
    }
}
