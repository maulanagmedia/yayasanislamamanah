package co.id.gmedia.yia.Utils;

public class ServerURL {
    public static final int PIX_REQUEST_CODE = 99;

    //private static final String baseURL = "http://192.168.20.36/gmedia/yia/api/";
    private static final String baseURL = "http://gmedia.bz/yia/v3/api/";

    public static final String login = baseURL + "Authentication/";
    public static final String changePassword = baseURL + "Authentication/update_password/";
    public static final String updateAkun = baseURL + "Authentication/process_user/";
    public static final String getKota = baseURL + "Master/kota/";
    public static final String getKecamatan = baseURL + "Master/kecamatan/";
    public static final String getKelurahan = baseURL + "Master/kelurahan/";
    public static final String saveCalonDonatur = baseURL + "Donatur/add_calon_donatur/";
    public static final String getCalonDonatur = baseURL + "Donatur/calon_donatur/";

    public static final String getRencanaKerjaSosial = baseURL + "Sales_sosial/Rencana_kerja/rk_petugas_sosial";
    public static final String saveSosial = baseURL + "Sales_sosial/Rencana_kerja/ActionTempDonatur";
    public static final String getRencanaKerjaCollector = baseURL + "Collector/Rencana_kerja/index";
    public static final String getRencanaKerjaSurvey = baseURL + "Sales_checking/Rencana_kerja/rk_petugas_checking";
    public static final String saveSurvey = baseURL + "Sales_checking/Rencana_kerja/validasiDonatur";
    public static final String saveCollector = baseURL + "Collector/Rencana_kerja/jemput_infaq/";
    public static final String tambahDonaturLuarCollector = baseURL + "Collector/Rencana_kerja/data_luar";
    public static final String berhentiDonasi = baseURL + "Collector/Rencana_kerja/donatur_out";
    public static final String getRekapSetoran = baseURL + "Collector/Rencana_kerja/hasil_setoran_collector/";
    public static final String getRekapSetoranByOut = baseURL + "Collector/Rencana_kerja/view_donatur_out/";
    public static final String getRekapSetoranByJenis = baseURL + "Collector/Rencana_kerja/donatur_rekap_setoran/";
    public static final String updateDonatur = baseURL + "Collector/Rencana_kerja/Edit_donatur/";
}
