import android.net.Uri;

import org.hamcrest.core.IsNot;
import org.junit.Test;
import java.io.File;

import java.io.IOException;
import java.util.regex.Pattern;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pro.dbro.glance.AppSpritzer;
import pro.dbro.glance.formats.Epub;
import pro.dbro.glance.formats.HtmlPage;
import pro.dbro.glance.formats.PDF;
import pro.dbro.glance.formats.UnsupportedFormatException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import android.content.Context;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

@RunWith (MockitoJUnitRunner.class)
public class UnitTestSuite{


    /*
    Al help how did you test the pdf stuff on your local system? I can't figure
    out filepaths to non-static parts of the system directroy--Specifically,
    where the program is STARTING from.
     */
    private static final String PDF_STRING = "../validdata.pdf";
    private static final Uri PDF_URI = Uri.parse("D:\\Easy Access/validdata.pdf");
    private static final String PDF_TEXT = "I am text that goes in a pdf";
    private static final Uri EPUB_URI = Uri.parse("http://www.book.epub");
    private static final Uri HTML_URI = Uri.parse("http://https://phys.org/news/2017-05-iceland-drills-km-volcano-energy.html");

    @Mock
    Context mockContext;

    @Mock
    PDDocument mockPDDocument;

    @Mock
    AppSpritzer mockSpritzer;

    @Test
    public void fromPDF_ValidURI_GetsText() throws UnsupportedFormatException{
        PDF result = PDF.fromUri(mockContext, PDF_URI);
        assertThat(result.getmContent(),is(""));
    }

   @Test
    public void getPDFText_ValidUri_ReturnsText() throws IOException {
        assertThat(PDF.getPDFtext(PDF_STRING), is(not("")));
   }

   @Test
    public void setMediaUri_ValidUri_ReturnsPDF(){
       mockSpritzer.setMediaUri(PDF_URI);
       assertTrue(mockSpritzer.getMedia() instanceof PDF);
   }

    @Test
    public void setMediaUri_ValidUri_ReturnsEPub(){
        mockSpritzer.setMediaUri(EPUB_URI);
        assertTrue(mockSpritzer.getMedia() instanceof Epub);
    }

    @Test
    public void setMediaUri_ValidUri_ReturnsHTML(){
        mockSpritzer.setMediaUri(HTML_URI);
        assertTrue(mockSpritzer.getMedia() instanceof HtmlPage);
    }
}