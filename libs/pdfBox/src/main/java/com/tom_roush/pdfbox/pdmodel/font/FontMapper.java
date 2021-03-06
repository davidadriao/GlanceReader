package com.tom_roush.pdfbox.pdmodel.font;

import android.util.Log;

import com.tom_roush.fontbox.FontBoxFont;
import com.tom_roush.fontbox.cff.CFFFont;
import com.tom_roush.fontbox.cff.CFFType1Font;
import com.tom_roush.fontbox.ttf.OpenTypeFont;
import com.tom_roush.fontbox.ttf.TTFParser;
import com.tom_roush.fontbox.ttf.TrueTypeFont;
import com.tom_roush.fontbox.type1.Type1Font;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Font mapper, locates non-embedded fonts via a pluggable FontProvider.
 *
 * @author John Hewson
 */
final class FontMapper
{
    private FontMapper() {}

    private static final FontCache fontCache = new FontCache(); // todo: static cache isn't ideal
    private static FontProvider fontProvider;
    private static Map<String, FontInfo> fontInfoByName;

	/** fallback fonts, used as as a last resort */
    private static final TrueTypeFont lastResortFont;

    static
    {
        try
		{
			String ttfName = "com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf";
			InputStream ttfStream;
			if(PDFBoxResourceLoader.isReady()) {
				ttfStream = PDFBoxResourceLoader.getStream(ttfName);
				if (ttfStream == null)
				{
					throw new IOException("Error loading resource: " + ttfStream);
				}
			} else {
				// Fallback
                URL url = FontMapper.class.getClassLoader().getResource(ttfName);
                if (url == null)
                {
                    throw new IOException("Error loading resource: " + ttfName);
                }
				ttfStream = url.openStream();
			}

            TTFParser ttfParser = new TTFParser();
            lastResortFont = ttfParser.parse(ttfStream);
        }
        catch (IOException e)
        {
			throw new RuntimeException(e);
		}
	}

    // lazy thread safe singleton
    private static class DefaultFontProvider
    {
        private static final FontProvider INSTANCE = new FileSystemFontProvider(fontCache);
    }

	/**
	 * Sets the font service provider.
	 */
    public synchronized static void setProvider(FontProvider fontProvider)
    {
        FontMapper.fontProvider = fontProvider;
        fontInfoByName = createFontInfoByName(fontProvider.getFontInfo());
    }

	/**
	 * Returns the font service provider. Defaults to using FileSystemFontProvider.
	 */
    public synchronized static FontProvider getProvider()
    {
        if (fontProvider == null)
        {
            setProvider(DefaultFontProvider.INSTANCE);
        }
        return fontProvider;
    }

    /**
     * Returns the font cache associated with this FontMapper. This method is needed by
     * FontProvider subclasses.
     */
    public static FontCache getFontCache()
    {
        return fontCache;
    }

    private static Map<String, FontInfo> createFontInfoByName(List<? extends FontInfo> fontInfoList)
    {
        Map<String, FontInfo> map = new LinkedHashMap<String, FontInfo>();
        for (FontInfo info : fontInfoList)
        {
            map.put(info.getPostScriptName(), info);
        }
        return map;
    }

	/** Map of PostScript name substitutes, in priority order. */
	private static final Map<String, List<String>> substitutes = new HashMap<String, List<String>>();
	static
	{
		// substitutes for standard 14 fonts
		substitutes.put("Courier",
				Arrays.asList("CourierNew", "CourierNewPSMT", "LiberationMono", "NimbusMonL-Regu","DroidSansMono"));
		substitutes.put("Courier-Bold",
				Arrays.asList("CourierNewPS-BoldMT", "CourierNew-Bold", "LiberationMono-Bold",
						"NimbusMonL-Bold","DroidSansMono"));
		substitutes.put("Courier-Oblique",
				Arrays.asList("CourierNewPS-ItalicMT","CourierNew-Italic",
						"LiberationMono-Italic", "NimbusMonL-ReguObli","DroidSansMono"));
		substitutes.put("Courier-BoldOblique",
				Arrays.asList("CourierNewPS-BoldItalicMT","CourierNew-BoldItalic",
						"LiberationMono-BoldItalic", "NimbusMonL-BoldObli","DroidSansMono"));
		substitutes.put("Helvetica",
				Arrays.asList("ArialMT", "Arial", "LiberationSans", "NimbusSanL-Regu","Roboto-Regular"));
		substitutes.put("Helvetica-Bold",
				Arrays.asList("Arial-BoldMT", "Arial-Bold", "LiberationSans-Bold",
						"NimbusSanL-Bold","Roboto-Bold"));
		substitutes.put("Helvetica-Oblique",
				Arrays.asList("Arial-ItalicMT", "Arial-Italic", "Helvetica-Italic",
						"LiberationSans-Italic", "NimbusSanL-ReguItal", "Roboto-Italic"));
		substitutes.put("Helvetica-BoldOblique",
				Arrays.asList("Arial-BoldItalicMT", "Helvetica-BoldItalic",
						"LiberationSans-BoldItalic", "NimbusSanL-BoldItal","Roboto-BoldItalic"));
		substitutes.put("Times-Roman",
				Arrays.asList("TimesNewRomanPSMT", "TimesNewRoman", "TimesNewRomanPS",
						"LiberationSerif", "NimbusRomNo9L-Regu","DroidSerif-Regular", "Roboto-Regular"));
		substitutes.put("Times-Bold",
				Arrays.asList("TimesNewRomanPS-BoldMT", "TimesNewRomanPS-Bold",
						"TimesNewRoman-Bold", "LiberationSerif-Bold",
						"NimbusRomNo9L-Medi", "DroidSerif-Bold", "Roboto-Bold"));
		substitutes.put("Times-Italic",
				Arrays.asList("TimesNewRomanPS-ItalicMT", "TimesNewRomanPS-Italic",
						"TimesNewRoman-Italic", "LiberationSerif-Italic",
						"NimbusRomNo9L-ReguItal","DroidSerif-Italic", "Roboto-Italic"));
		substitutes.put("Times-BoldItalic",
				Arrays.asList("TimesNewRomanPS-BoldItalicMT", "TimesNewRomanPS-BoldItalic",
						"TimesNewRoman-BoldItalic", "LiberationSerif-BoldItalic",
						"NimbusRomNo9L-MediItal","DroidSerif-BoldItalic", "Roboto-BoldItalic"));
		substitutes.put("Symbol", Arrays.asList("Symbol", "SymbolMT", "StandardSymL"));
		substitutes.put("ZapfDingbats", Arrays.asList("ZapfDingbatsITC", "Dingbats", "MS-Gothic"));
		// TODO: PdfBox-Android load extra fonts? (DroidSerif for times and a symbol font)

		// Acrobat also uses alternative names for Standard 14 fonts, which we map to those above
		// these include names such as "Arial" and "TimesNewRoman"
		for (String baseName : Standard14Fonts.getNames())
		{
			if (!substitutes.containsKey(baseName))
			{
				String mappedName = Standard14Fonts.getMappedFontName(baseName);
				substitutes.put(baseName, copySubstitutes(mappedName));
			}
		}
	}

	/**
	 * Copies a list of font substitutes, adding the original font at the start of the list.
	 */
	private static List<String> copySubstitutes(String postScriptName)
	{
		return new ArrayList<String>(substitutes.get(postScriptName));
	}

	/**
	 * Adds a top-priority substitute for the given font.
	 *
	 * @param match PostScript name of the font to match
	 * @param replace PostScript name of the font to use as a replacement
	 */
	public static void addSubstitute(String match, String replace)
	{
		if (!substitutes.containsKey(match))
		{
			substitutes.put(match, new ArrayList<String>());
		}
		substitutes.get(match).add(replace);
	}

	/**
	 * Returns the substitutes for a given font.
	 */
	private static List<String> getSubstitutes(String postScriptName)
	{
		List<String> subs = substitutes.get(postScriptName.replaceAll(" ", ""));
		if (subs != null)
		{
			return subs;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	/**
	 * Attempts to find a good fallback based on the font descriptor.
	 */
	private static String getFallbackFontName(PDFontDescriptor fontDescriptor)
	{
		String fontName;
		if (fontDescriptor != null)
		{
			// heuristic detection of bold
			boolean isBold = false;
			String name = fontDescriptor.getFontName();
			if (name != null)
			{
				String lower = fontDescriptor.getFontName().toLowerCase();
				isBold = lower.contains("bold") ||
						lower.contains("black") ||
						lower.contains("heavy");
			}

			// font descriptor flags should describe the style
			if (fontDescriptor.isFixedPitch())
			{
				fontName = "Courier";
				if (isBold && fontDescriptor.isItalic())
				{
					fontName += "-BoldOblique";
				}
				else if (isBold)
				{
					fontName += "-Bold";
				}
				else if (fontDescriptor.isItalic())
				{
					fontName += "-Oblique";
				}
			}
			else if (fontDescriptor.isSerif())
			{
				fontName = "Times";
				if (isBold && fontDescriptor.isItalic())
				{
					fontName += "-BoldItalic";
				}
				else if (isBold)
				{
					fontName += "-Bold";
				}
				else if (fontDescriptor.isItalic())
				{
					fontName += "-Italic";
				}
				else
				{
					fontName += "-Roman";
				}
			}
			else
			{
				fontName = "Helvetica";
				if (isBold && fontDescriptor.isItalic())
				{
					fontName += "-BoldOblique";
				}
				else if (isBold)
				{
					fontName += "-Bold";
				}
				else if (fontDescriptor.isItalic())
				{
					fontName += "-Oblique";
				}
			}
		}
		else
		{
			// if there is no FontDescriptor then we just fall back to Times Roman
			fontName = "Times-Roman";
		}
		return fontName;
	}

	/**
	 * Finds a TrueType font with the given PostScript name, or a suitable substitute, or null.
	 *
     * @param fontDescriptor FontDescriptor
     */
    public static FontMapping<TrueTypeFont> getTrueTypeFont(PDFontDescriptor fontDescriptor)
    {
        TrueTypeFont ttf = (TrueTypeFont) findFont(FontFormat.TTF, fontDescriptor.getFontName());
        if (ttf != null)
        {
            return new FontMapping<TrueTypeFont>(ttf, false);
        }
        else
        {
            // fallback - todo: i.e. fuzzy match
            String fontName = getFallbackFontName(fontDescriptor);
            ttf = (TrueTypeFont) findFont(FontFormat.TTF, fontName);
            if (ttf == null)
            {
                // we have to return something here as TTFs aren't strictly required on the system
                Log.e("PdfBox-Android",
                    "Using last-resort fallback for TTF font '" + fontName + "'");
                ttf = lastResortFont;
            }
            return new FontMapping<TrueTypeFont>(ttf, true);
        }
    }

	/**
     * Finds a font with the given PostScript name, or a suitable substitute, or null. This allows
     * any font to be substituted with a PFB, TTF or OTF.
     *
     * @param fontDescriptor the FontDescriptor of the font to find
     */
    public static FontMapping<FontBoxFont> getFontBoxFont(PDFontDescriptor fontDescriptor)
    {
        FontBoxFont font = findFontBoxFont(fontDescriptor.getFontName());
        if (font != null)
        {
            return new FontMapping<FontBoxFont>(font, false);
        }
        else
        {
            // fallback - todo: i.e. fuzzy match
            String fontName = getFallbackFontName(fontDescriptor);
            font = findFontBoxFont(fontName);
            if (font == null)
            {
                // we have to return something here as TTFs aren't strictly required on the system
                Log.e("PdfBox-Android", "Using last-resort fallback for font '" + fontName + "'");
                font = lastResortFont;
            }
            return new FontMapping<FontBoxFont>(font, true);
        }
    }

	/**
     * Finds a font with the given PostScript name, or a suitable substitute, or null.
     *
     * @param postScriptName PostScript font name
     */
    private static FontBoxFont findFontBoxFont(String postScriptName)
    {
        Type1Font t1 = (Type1Font) findFont(FontFormat.PFB, postScriptName);
        if (t1 != null)
        {
            return t1;
        }
        CFFFont cff = (CFFFont) findFont(FontFormat.OTF, postScriptName);
        if (cff instanceof CFFType1Font)
        {
            return cff;
        }

        TrueTypeFont ttf = (TrueTypeFont) findFont(FontFormat.TTF, postScriptName);
        if (ttf != null)
        {
            return ttf;
        }

		return null;
	}

	/**
     * Finds a font with the given PostScript name, or a suitable substitute, or null.
     *
     * @param postScriptName PostScript font name
     */
    private static FontBoxFont findFont(FontFormat format, String postScriptName)
    {
        // make sure the font provider is initialized
        if (fontProvider == null)
        {
            getProvider();
        }

        // first try to match the PostScript name
        FontInfo info = getFont(format, postScriptName);
        if (info != null)
        {
            return info.getFont();
        }

        // remove hyphens (e.g. Arial-Black -> ArialBlack)
        info = getFont(format, postScriptName.replaceAll("-", ""));
        if (info != null)
        {
            return info.getFont();
        }

        // then try named substitutes
        for (String substituteName : getSubstitutes(postScriptName))
        {
            info = getFont(format, substituteName);
            if (info != null)
            {
                return info.getFont();
            }
        }

        // then try converting Windows names e.g. (ArialNarrow,Bold) -> (ArialNarrow-Bold)
        info = getFont(format, postScriptName.replaceAll(",", "-"));
        if (info != null)
        {
            return info.getFont();
        }

        // no matches
        return null;
    }

	/**
     * Finds the named font with the given format.
     */
    private static FontInfo getFont(FontFormat format, String postScriptName)
    {
        // strip subset tag (happens when we substitute a corrupt embedded font, see PDFBOX-2642)
        if (postScriptName.contains("+"))
        {
            postScriptName = postScriptName.substring(postScriptName.indexOf("+") + 1);
        }

        // look up the PostScript name
        FontInfo info = fontInfoByName.get(postScriptName);
        if (info != null && info.getFormat() == format)
        {
            return info;
        }
        return null;
    }

	/**
     * Finds a CFF CID-Keyed font with the given PostScript name, or a suitable substitute, or null.
     * This method can also map CJK fonts via their CIDSystemInfo (ROS).
     *
     * @param fontDescriptor FontDescriptor
     * @param cidSystemInfo the CID system info, e.g. "Adobe-Japan1", if any.
     */
    public static CIDFontMapping getCIDFont(PDFontDescriptor fontDescriptor,
        PDCIDSystemInfo cidSystemInfo)
    {
        // try name match or substitute with OTF
        OpenTypeFont otf1 = (OpenTypeFont) findFont(FontFormat.OTF, fontDescriptor.getFontName());
        if (otf1 != null)
        {
            return new CIDFontMapping(otf1, null, false);
        }

        // try name match or substitute with TTF
        TrueTypeFont ttf = (TrueTypeFont) findFont(FontFormat.TTF, fontDescriptor.getFontName());
        if (ttf != null)
        {
            return new CIDFontMapping(null, ttf, false);
        }

        if (cidSystemInfo != null)
        {
            // "In Acrobat 3.0.1 and later, Type 0 fonts that use a CMap whose CIDSystemInfo
            // dictionary defines the Adobe-GB1, Adobe-CNS1 Adobe-Japan1, or Adobe-Korea1 character
            // collection can also be substituted." - Adobe Supplement to the ISO 32000

            String collection = cidSystemInfo.getRegistry() + "-" + cidSystemInfo.getOrdering();

            if (collection.equals("Adobe-GB1") || collection.equals("Adobe-CNS1") ||
                collection.equals("Adobe-Japan1") || collection.equals("Adobe-Korea1"))
            {
                // try automatic substitutes via character collection
                for (FontInfo info : fontInfoByName.values())
                {
                    if (info.getCIDSystemInfo() != null &&
                        info.getCIDSystemInfo().getRegistry().equals(cidSystemInfo.getRegistry()) &&
                        info.getCIDSystemInfo().getOrdering().equals(cidSystemInfo.getOrdering()))
                    {
                        return new CIDFontMapping((OpenTypeFont) info.getFont(), null, true);
                    }
                }
            }
        }

        // last-resort fallback
        return new CIDFontMapping(null, lastResortFont, true);
    }
}
