package co.id.gmedia.yia.Utils;

public class ServerURL {
    public static final int PIX_REQUEST_CODE = 99;

    //private static final String baseURL = "http://192.168.20.36/gmedia/yia/api/";
    private static final String baseURL = "http://gmedia.bz/yia/v3/api/";

    public static final String login = baseURL + "Authentication/";
    public static final String updateAkun = baseURL + "Authentication/process_user/";
    public static final String getKota = baseURL + "Master/kota/";
    public static final String getKecamatan = baseURL + "Master/kecamatan/";
    public static final String getKelurahan = baseURL + "Master/kelurahan/";
    public static final String saveCalonDonatur = baseURL + "Donatur/add_calon_donatur/";
    public static final String getCalonDonatur = baseURL + "Donatur/calon_donatur/";

    public static final String getRencanaKerjaCollector = baseURL + "Collector/Rencana_kerja/index";
    public static final String getRencanaKerjaSurvey = baseURL + "Sales_checking/Rencana_kerja/rk_petugas_checking";
    public static final String saveSurvey = baseURL + "Sales_checking/Rencana_kerja/validasiDonatur";
    public static final String tambahDonaturLuarCollector = baseURL + "Collector/Rencana_kerja/data_luar";
}
