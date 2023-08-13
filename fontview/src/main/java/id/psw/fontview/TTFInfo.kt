package id.psw.fontview

class TTFInfo {
    companion object {
        const val NK_COPYRIGHT_NOTICE           = 0
        const val NK_FONT_FAMILY_NAME           = 1
        const val NK_FONT_SUBFAMILY_NAME        = 2
        const val NK_UUID                       = 3
        const val NK_FULL_FONT_NAME             = 4
        const val NK_VERSION                    = 5
        const val NK_POST_SCRIPT_NAME           = 6
        const val NK_TRADEMARK                  = 7
        const val NK_MANUFACTURER_NAME          = 8
        const val NK_DESIGNER_NAME              = 9
        const val NK_DESCRIPTION                = 10
        const val NK_URL_VENDOR                 = 11
        const val NK_URL_DESIGNER               = 12
        const val NK_LICENSE_DESCRIPTION        = 13
        const val NK_URL_LICENSE_INFO           = 14
        const val NK_TYPO_FAMILY_NAME           = 16
        const val NK_TYPO_SUBFAMILY_NAME        = 17
        const val NK_MAC_COMPAT                 = 18
        const val NK_SAMPLE_TEXT                = 19
        const val NK_POSTSCRIPT_CID_NAME        = 20
        const val NK_WWS_FAMILY_NAME            = 21
        const val NK_WWS_SUBFAMILY_NAME         = 22
        const val NK_LIGHT_PALETTE              = 23
        const val NK_DARK_PALETTE               = 24
        const val NK_VARIATION_POSTSCRIPT_NAME  = 25
    }

    var copyright = ""
    var fontFamilyName = ""
    var fontSubfamilyName = ""
    var uuid = ""
    var fullFontName = ""
    var postScriptName = ""
    var version = ""
    var trademark = ""
    var manufacturerName = ""
    var designerName = ""
    var description = ""
    var urlVendor = ""
    var urlDesigner = ""
    var licenseDescription = ""
    var urlLicenseInfo = ""
    var typoFamilyName = ""
    var typoSubfamilyName = ""
    var macCompat = ""
    var sampleText = ""
    var postScriptCidName = ""
    var wwsFamilyName = ""
    var wwsSubfamilyName = ""
    var lightPalette = ""
    var darkPalette = ""
    var variationPostScriptName = ""

    operator fun get(index:Int) : String? {
        return when(index){
            NK_COPYRIGHT_NOTICE          -> copyright
            NK_FONT_FAMILY_NAME          -> fontFamilyName
            NK_FONT_SUBFAMILY_NAME       -> fontSubfamilyName
            NK_UUID                      -> uuid
            NK_FULL_FONT_NAME            -> fullFontName
            NK_VERSION                   -> version
            NK_POST_SCRIPT_NAME          -> postScriptName
            NK_TRADEMARK                 -> trademark
            NK_MANUFACTURER_NAME         -> manufacturerName
            NK_DESIGNER_NAME             -> designerName
            NK_DESCRIPTION               -> description
            NK_URL_VENDOR                -> urlVendor
            NK_URL_DESIGNER              -> urlDesigner
            NK_LICENSE_DESCRIPTION       -> licenseDescription
            NK_URL_LICENSE_INFO          -> urlLicenseInfo
            NK_TYPO_FAMILY_NAME          -> typoFamilyName
            NK_TYPO_SUBFAMILY_NAME       -> typoSubfamilyName
            NK_MAC_COMPAT                -> macCompat
            NK_SAMPLE_TEXT               -> sampleText
            NK_POSTSCRIPT_CID_NAME       -> postScriptCidName
            NK_WWS_FAMILY_NAME           -> wwsFamilyName
            NK_WWS_SUBFAMILY_NAME        -> wwsSubfamilyName
            NK_LIGHT_PALETTE             -> lightPalette
            NK_DARK_PALETTE              -> darkPalette
            NK_VARIATION_POSTSCRIPT_NAME -> variationPostScriptName
            else -> null
        }
    }

    operator fun set(index:Int, str:String){
        when(index){
            NK_COPYRIGHT_NOTICE          -> copyright = str
            NK_FONT_FAMILY_NAME          -> fontFamilyName = str
            NK_FONT_SUBFAMILY_NAME       -> fontSubfamilyName = str
            NK_UUID                      -> uuid = str
            NK_FULL_FONT_NAME            -> fullFontName = str
            NK_VERSION                   -> version = str
            NK_POST_SCRIPT_NAME          -> postScriptName = str
            NK_TRADEMARK                 -> trademark = str
            NK_MANUFACTURER_NAME         -> manufacturerName = str
            NK_DESIGNER_NAME             -> designerName = str
            NK_DESCRIPTION               -> description = str
            NK_URL_VENDOR                -> urlVendor = str
            NK_URL_DESIGNER              -> urlDesigner = str
            NK_LICENSE_DESCRIPTION       -> licenseDescription = str
            NK_URL_LICENSE_INFO          -> urlLicenseInfo = str
            NK_TYPO_FAMILY_NAME          -> typoFamilyName = str
            NK_TYPO_SUBFAMILY_NAME       -> typoSubfamilyName = str
            NK_MAC_COMPAT                -> macCompat = str
            NK_SAMPLE_TEXT               -> sampleText = str
            NK_POSTSCRIPT_CID_NAME       -> postScriptCidName = str
            NK_WWS_FAMILY_NAME           -> wwsFamilyName = str
            NK_WWS_SUBFAMILY_NAME        -> wwsSubfamilyName = str
            NK_LIGHT_PALETTE             -> lightPalette = str
            NK_DARK_PALETTE              -> darkPalette = str
            NK_VARIATION_POSTSCRIPT_NAME -> variationPostScriptName = str
        }
    }
}