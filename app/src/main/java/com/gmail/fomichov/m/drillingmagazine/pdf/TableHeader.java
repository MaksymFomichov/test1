package com.gmail.fomichov.m.drillingmagazine.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class TableHeader extends PdfPageEventHelper {
    String header;
    PdfTemplate total;

    public void setHeader(String header) {
        this.header = header;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16); // размер
    }

    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable tableTop = new PdfPTable(3);
        PdfPTable tableDown = new PdfPTable(1);
        try {
            tableTop.setWidths(new int[]{24, 24, 2});
            tableTop.setTotalWidth(550);
            tableTop.setLockedWidth(true);

            // добавляем верхний колонтитул
            tableTop.getDefaultCell().setFixedHeight(20); // высота ряда 20
            tableTop.getDefaultCell().setBorder(Rectangle.BOTTOM); // показываем нижнюю границу
            // добавляем название обьекта в левый угол (угол по умолчанию) первой ячейки
            tableTop.addCell(new Phrase(header, CreatePdfExc.font));
            // для текста "Страница ** из" делаем выравнивание по горизонтали в право в второй ячейке
            tableTop.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTop.addCell(new Phrase(String.format("Страница %d из", writer.getPageNumber()), CreatePdfExc.font)); // получаем номер текущей страницы
            // добавляем в третью ячейку количество страниц в документе
            PdfPCell cell = new PdfPCell(Image.getInstance(total));
            cell.setBorder(Rectangle.BOTTOM); // показываем нижнюю границу
            tableTop.addCell(cell);

            // добавляем нижний колонтитул
            tableDown.setTotalWidth(550);
            tableDown.setLockedWidth(true);
            PdfPCell cellDown = new PdfPCell(new Phrase("Сгенерировано в мобильном приложении \"Буровой журнал\" http://play.google.com/store/apps/details?id=com.gmail.fomichov.m.drillingmagazine", CreatePdfExc.font));
            cellDown.setBorder(Rectangle.TOP);
            cellDown.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableDown.addCell(cellDown);
            tableTop.writeSelectedRows(0, -1, 30, 825, writer.getDirectContent());
            tableDown.writeSelectedRows(0, -1, 30, 40, writer.getDirectContent());
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    // добавляем в третью ячейку количество страниц (в параметрах выравнивание и координаты вставки текста)
    public void onCloseDocument(PdfWriter writer, Document document) {
        Phrase allPageNumber = new Phrase(String.valueOf(writer.getPageNumber()));
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT, allPageNumber, 2, 2, 0);
    }
}
