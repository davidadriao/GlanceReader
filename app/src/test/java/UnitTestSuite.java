import android.net.Uri;

import org.hamcrest.core.IsNot;
import org.junit.Test;
import java.io.File;

import java.io.IOException;
import java.util.regex.Pattern;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pro.dbro.glance.AppSpritzer;
import pro.dbro.glance.activities.MainActivity;
import pro.dbro.glance.formats.Epub;
import pro.dbro.glance.formats.HtmlPage;
import pro.dbro.glance.formats.PDF;
import pro.dbro.glance.formats.UnsupportedFormatException;
import pro.dbro.glance.fragments.SpritzFragment;
import timber.log.Timber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import android.content.Context;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

@RunWith (MockitoJUnitRunner.class)
public class UnitTestSuite{


    private static final String PDF_STRING = "../validdata.pdf";
    private static final Uri PDF_URI = Uri.parse("validdata.pdf");
    private static final String PDF_TEXT = "I am text that goes in a pdf";
    private static final Uri EPUB_URI = Uri.parse("http://www.book.epub");
    private static final Uri HTML_URI = Uri.parse("http://https://phys.org/news/2017-05-iceland-drills-km-volcano-energy.html");

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

   @Mock
   Bus mockBus;
    TextView mockTarget;
    Context mockContext;
    Context mockContext2;
    Timber mockTimber;

   @Test
    public void setMediaUri_ValidUri_ReturnsPDF(){
       //mockSpritzer.setMediaUri(PDF_URI);
       /*AppSpritzer spritzer = new AppSpritzer(mockBus, mockTarget, PDF_URI);
       when(mockTarget.getContext().getApplicationContext()).thenReturn(mockContext2);
       when(mockTarget.getContext()).thenReturn(mockContext);*/
       SpritzFragment frag = new SpritzFragment();
       frag.feedMediaUriToSpritzer(PDF_URI);
       assertTrue(frag.getSpritzer().getMedia() instanceof PDF);
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