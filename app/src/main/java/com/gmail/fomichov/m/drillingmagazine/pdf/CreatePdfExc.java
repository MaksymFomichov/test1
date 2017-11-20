package com.gmail.fomichov.m.drillingmagazine.pdf;

import android.content.Context;
import android.text.TextUtils;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class CreatePdfExc {
    public static Font font;
    private static Font fontHeader;
    private static Context context;
    private static Excavation excavation;
    private File patch;

    public CreatePdfExc(Context context, Excavation excavation, File patch) {
        this.context = context;
        this.excavation = excavation;
        this.patch = patch;
    }

    public void startCreatePDF() throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont("assets/fonts/FreeSans.ttf", "Cp1251", BaseFont.EMBEDDED);
        font = new Font(baseFont);
        fontHeader = new Font(baseFont, 20, Font.BOLD);
        OutputStream output = new FileOutputStream(patch);
        Document document = new Document(PageSize.A4, 20, 5, 45, 45); // левое, правое, верхнее и нижнее поля
        PdfWriter writer = PdfWriter.getInstance(document, output);
        TableHeader header = new TableHeader();
        header.setHeader(excavation.getObject());
        writer.setPageEvent(header);
        document.open();

        PdfPTable table = new PdfPTable(2); // создаем таблицу, указываем количество столбцов
        table.setTotalWidth(new float[]{310, 260}); // задаем ширину столбцов
        table.setLockedWidth(true); // этот параметр отвечает за реальные размеры
        // заголовок
        PdfPCell cellHeader = new PdfPCell(new Phrase("Выработка " + excavation.getTypeExcavation() + "-" + excavation.getNameExcavation(), fontHeader));
        cellHeader.setColspan(2); // обьединяем две колонки в одну
        cellHeader.setFixedHeight(25); // высота ячейки
        cellHeader.setBorder(Rectangle.NO_BORDER); // убираем видимость границы
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER); // выравниваем по центру по горизонтали
        cellHeader.setVerticalAlignment(Element.ALIGN_CENTER); // выравниваем по центру по вертикали
        cellHeader.setUseDescender(true); // одинаковые отступы сверху и снизу
        table.addCell(cellHeader);

        PdfPCell cellEmpty = new PdfPCell(new Phrase(""));
        cellEmpty.setBorder(Rectangle.NO_BORDER); // убираем видимость границы
        table.addCell(cellEmpty);

        PdfPCell cellDate = new PdfPCell(new Phrase(
                "абсолютная отметка: " + String.format(Locale.ENGLISH, "%(.2f", excavation.getAbsoluteElevation()) + "\n" +
                        "начало: " + excavation.getDateStart() + " окончание: " + excavation.getDateEnd(), font));
        cellDate.setVerticalAlignment(Element.ALIGN_BOTTOM); // выравниваем по центру по вертикали
        cellDate.setUseDescender(true); // одинаковые отступы сверху и снизу
        cellDate.setBorder(Rectangle.NO_BORDER); // убираем видимость границы
        table.addCell(cellDate);

        // данные по выработке
        document.add(table);
        document.add(createTable());
        document.close();
    }

    // генерируем таблицу
    private static PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(7); // создаем таблицу, указываем количество столбцов
        table.setTotalWidth(new float[]{30, 40, 70, 50, 90, 60, 200}); // задаем ширину столбцов
        table.setLockedWidth(true); // этот параметр отвечает за реальные размеры
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(context.getString(R.string.column_number), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_power), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_depth), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_benchmark_layer), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_water), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_probe), font));
        myAlignColumnTitle(cell);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(context.getString(R.string.column_description), font));
        cell.setFixedHeight(100);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // выравниваем по центру по вертикали
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // выравниваем по центру по горизонтали
        cell.setUseDescender(true);
        table.addCell(cell);

        int sizeListLayer = excavation.getLayers().size();
        int sizeListProbe = excavation.getProbes().size();
        double startDepthLayer = 0;
        for (int i = 0; i < sizeListLayer; i++) {
            double startLayer = excavation.getLayers().get(i).getStartDepth();
            double endLayer = excavation.getLayers().get(i).getEndDepth();
            double depthWaterShow = excavation.getWaterShow();
            double depthWaterStay = excavation.getWaterStay();

            table.getDefaultCell().setUseDescender(true);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(String.valueOf((i + 1))); // номер слоя
            table.addCell(String.format(Locale.ENGLISH, "%(.2f", (excavation.getLayers().get(i).getLayerPower()))); // мощность слоя
            table.addCell(String.valueOf(startDepthLayer + "-" + excavation.getLayers().get(i).getEndDepth())); // глубина подошвы слоя
            startDepthLayer = excavation.getLayers().get(i).getEndDepth();
            table.addCell(String.format(Locale.ENGLISH, "%(.2f", (excavation.getAbsoluteElevation() - excavation.getLayers().get(i).getEndDepth()))); // отметка подошвы слоя

            cell = new PdfPCell(new Phrase(getWaterData(startLayer, endLayer, sizeListProbe, depthWaterShow, depthWaterStay), font)); // вода
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(getProbe(startLayer, endLayer, sizeListProbe), font)); // пробы
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(excavation.getLayers().get(i).getDescription(), font)); // описание слоя
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // выравниваем по центру по вертикали
            table.addCell(cell);
        }

        return table;
    }

    // настройка для ячеек заглавного ряда
    private static void myAlignColumnTitle(PdfPCell cell) {
        cell.setFixedHeight(105); // высота ячейки
        cell.setRotation(90); // поворот текста в ячейке
        cell.setUseDescender(true); // одинаковые отступы сверху и снизу
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // выравниваем по центру по вертикали
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // выравниваем по центру по горизонтали
    }

    // получаем данные о воде в диапозоне глубин слоя
    private static String getWaterData(double startLayer, double endLayer, int sizeListProbe, double depthWaterShow, double depthWaterStay) {
        String result = "";
        if (depthWaterShow > startLayer && depthWaterShow <= endLayer) {
            result += "п " + depthWaterShow + " (" + String.format(Locale.ENGLISH, "%(.2f", (excavation.getAbsoluteElevation() - depthWaterShow)) + ")" + "\n" + excavation.getDateStart() + "\n";
        }
        if (depthWaterStay > startLayer && depthWaterStay <= endLayer) {
            result += "у " + depthWaterStay + " (" + String.format(Locale.ENGLISH, "%(.2f", (excavation.getAbsoluteElevation() - depthWaterStay)) + ")" + "\n" + excavation.getDateWaterStay();
        }
        return result;
    }

    // получаем пробы в диапозоне глубин слоя
    private static String getProbe(double startLayer, double endLayer, int sizeListProbe) {
        String result = "";
        ArrayList<Double> listWaterProbe = new ArrayList<>();
        ArrayList<Double> listFullProbe = new ArrayList<>();
        ArrayList<Double> listDestroyProbe = new ArrayList<>();
        for (int i = 0; i < sizeListProbe; i++) {
            double depthProbe = excavation.getProbes().get(i).getDepth();
            if (depthProbe > startLayer && depthProbe <= endLayer) {
                if (excavation.getProbes().get(i).getType().equals("вода")) {
                    listWaterProbe.add(depthProbe);
                } else if (excavation.getProbes().get(i).getType().equals("монолит")) {
                    listFullProbe.add(depthProbe);
                } else if (excavation.getProbes().get(i).getType().equals("нарушенная")) {
                    listDestroyProbe.add(depthProbe);
                }
            }
        }

        if (!listWaterProbe.isEmpty()) {
            result = "в " + TextUtils.join(", ", listWaterProbe) + "\n";
            MyLog.showLog(startLayer + "-" + endLayer + " в " + TextUtils.join(", ", listWaterProbe));
        }
        if (!listFullProbe.isEmpty()) {
            result += "м " + TextUtils.join(", ", listFullProbe) + "\n";
            MyLog.showLog(startLayer + "-" + endLayer + " м " + TextUtils.join(", ", listFullProbe));
        }
        if (!listDestroyProbe.isEmpty()) {
            result += "н " + TextUtils.join(", ", listDestroyProbe);
            MyLog.showLog(startLayer + "-" + endLayer + " н " + TextUtils.join(", ", listDestroyProbe));
        }

        return result;
    }
}
