package pro.dbro.glance.formats;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.provider.MediaStore;


import com.tom_roush.pdfbox.contentstream.operator.graphics.StrokePath;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pro.dbro.glance.FileUtils;

/**
 * Created by Al on 4/14/2017.
 */

public class PDF implements SpritzerMedia{

    private static String mContent;
    private static String mTitle = "Untitled";
    private static PDDocument pdfDoc = null;


    public static PDF fromUri(Context context, Uri uri) throws UnsupportedFormatException {
        final PDF page = new PDF();

        try{

            InputStream epubInputStream = context.getContentResolver().openInputStream(uri);
            String pdfPath = FileUtils.getPath(context, uri);

            PDFBoxResourceLoader.init(context);

            mContent = PDF.getPDFtext(pdfPath);
        }catch (IOException e){

        }

        return page;

    }


    public static String getPDFtext(String pdfUriString) throws IOException{
        File pdfFile = new File(pdfUriString);
        pdfDoc = PDDocument.load(pdfFile);
        String parsedText = "";

        PDFTextStripper pdfStripper = new PDFTextStripper();

        try {
            pdfStripper.setStartPage(0);
            //pdfStripper.setEndPage(1);
            parsedText = pdfStripper.getText(pdfDoc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pdfDoc != null) pdfDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return parsedText;
    }



    @Override
    public String getTitle() {
        PDDocumentInformation info = pdfDoc.getDocumentInformation();
        if (info.getTitle() != null) {
            mTitle = info.getTitle();
        }
        return mTitle;
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public String getChapterTitle(int ignored) {
        return "";
    }

    @Override
    public String loadChapter(int ignored) {
        return (mContent == null) ? "" : mContent;
    }

    @Override
    public int countChapters() {
        return 1;
    }

    public String getmContent() {return mContent;}
}
