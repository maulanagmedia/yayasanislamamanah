package co.id.gmedia.yia.Utils;

public class ServerURL {
    public static final int PIX_REQUEST_CODE = 99;

//    private static final String baseURL = "http://192.168.20.16/gmedia/yia/v2/api/";
    //private static final String baseURL = "http://gmedia.bz/yia/v3/api/";
    private static final String baseURL = "http://yayasanislamamanah.com/api/";

    public static final String login = baseURL + "Authentication/";
    public static final String updateFCM = baseURL + "Authentication/update_fcm";
    public static final String changePassword = baseURL + "Authentication/update_password/";
    public static final String updateAkun = baseURL + "Authentication/process_user/";
    public static final String sendRequest = baseURL + "Donatur/request/";
    public static final String getKota = baseURL + "Master/kota/";
    public static final String getKecamatan = baseURL + "Master/kecamatan/";
    public static final String getKelurahan = baseURL + "Master/kelurahan/";
    public static final String saveCalonDonatur = baseURL + "Donatur/add_calon_donatur/";
    public static final String getCalonDonatur = baseURL + "Donatur/calon_donatur/";
    public static final String editCalonDonatur = baseURL + "Donatur/edit_calon_donatur/";

    public static final String getJadwalSosial = baseURL + "Sales_sosial/Rencana_kerja/jadwal_rk_sales_sosial";
    public static final String getRencanaKerjaSosial = baseURL + "Sales_sosial/Rencana_kerja/rk_petugas_sosial";
    public static final String saveSosial = baseURL + "Sales_sosial/Rencana_kerja/ActionTempDonatur";
    public static final String editDonaturSosial = baseURL + "Sales_sosial/Rencana_kerja/editDonaturSosial";

    public static final String getRencanaKerjaCollector = baseURL + "Collector/Rencana_kerja/history_collector";
    public static final String getTotalHistoryCollector = baseURL + "Collector/Rencana_kerja/total_history_collector";
    public static final String getJadwalCollector = baseURL + "Collector/Rencana_kerja/jadwal_collector";
    public static final String getJadwalCollector2 = baseURL + "Collector/Rencana_kerja/jadwal_kunjungan_collector";
    public static final String getTotalKunjungan = baseURL + "Collector/Rencana_kerja/total_kunjungan_collector";
    public static final String getRencanaKerjaSurvey = baseURL + "Sales_checking/Rencana_kerja/history_kunjungan_collector";
    public static final String getJadwalKerjaSurvey = baseURL + "Sales_checking/Rencana_kerja/jadwal_kunjungan_checking";
    public static final String saveSurvey = baseURL + "Sales_checking/Rencana_kerja/validasiDonatur";
    public static final String saveCollector = baseURL + "Collector/Rencana_kerja/jemput_infaq/";
    public static final String tambahDonaturLuarCollector = baseURL + "Collector/Rencana_kerja/data_luar";
    public static final String berhentiDonasi = baseURL + "Collector/Rencana_kerja/donatur_out";
    public static final String getRekapSetoran = baseURL + "Collector/Rencana_kerja/hasil_setoran_collector/";
    public static final String getRekapSetoranByOut = baseURL + "Collector/Rencana_kerja/view_donatur_out/";
    public static final String getRekapSetoranByJenis = baseURL + "Collector/Rencana_kerja/donatur_rekap_setoran/";
    public static final String updateDonatur = baseURL + "Collector/Rencana_kerja/Edit_donatur/";
    public static final String saveDataLuarSosial = baseURL + "Sales_sosial/Rencana_kerja/add_data_luar/";
    public static final String getNotification = baseURL + "Collector/Rencana_kerja/notif_sales/";
    public static final String getBackgroundDashboard = baseURL + "/Authentication/bg_sampul/";
    public static final String getApplicationVersion = baseURL + "Master/application_version/";
}
