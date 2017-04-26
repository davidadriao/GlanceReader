package pro.dbro.glance.formats;

import android.graphics.pdf.PdfDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Al on 4/14/2017.
 */

public class PDF implements SpritzerMedia{


    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getChapterTitle(int chapterNumber) {
        return null;
    }

    @Override
    public String loadChapter(int chapterNumber) {
        return null;
    }

    @Override
    public int countChapters() {
        return 0;
    }
}
