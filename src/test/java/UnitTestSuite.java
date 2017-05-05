import org.junit.Test;
import java.util.regex.Pattern;
import GlanceReader.app.src.main.java.pro.dbro.glance.formats.PDF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith (MockitoJUnitRunner.class)
public class UnitTestSuite{

    /*
    PDF Stuff
     */

    private static final String PDF_URI = "validdata.pdf";

    @Mock
    Context mockContext;

    @Test
    public void fromPDF_ValidURI_ReturnsPDFObj(){
        result = PDF.getFromURI(mockContext, PDF_URI);
        assertThat(result,is(PDF));
    }
}