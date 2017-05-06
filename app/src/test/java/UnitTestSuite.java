import android.net.Uri;
import org.junit.Test;
import java.util.regex.Pattern;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pro.dbro.glance.formats.PDF;
import pro.dbro.glance.formats.UnsupportedFormatException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import android.content.Context;

@RunWith (MockitoJUnitRunner.class)
public class UnitTestSuite{

    /*
    PDF Stuff
     */

    private static final Uri PDF_URI = Uri.parse("validdata.psd");

    @Mock
    Context mockContext;

   /* @Test
    public void fromPDF_ValidURI_ReturnsPDFObj() throws UnsupportedFormatException{
        PDF result = PDF.fromUri(mockContext, PDF_URI);
        assertFalse();
    }*/

   @Test
    public void 
}