package co.id.gmedia.yia.Utils;

public class ServerURL {

    public static final int PIX_REQUEST_CODE = 99;

    //private static final String baseURL = "http://192.168.20.36/gmedia/yia/api/";
    private static final String baseURL = "http://gmedia.bz/yia/v2/api/";

    public static final String login = baseURL + "Authentication/";
    public static final String updateAkun = baseURL + "Authentication/process_user/";
    public static final String getKota = baseURL + "Master/kota/";
    public static final String getKecamatan = baseURL + "Master/kecamatan/";
    public static final String getKelurahan = baseURL + "Master/kelurahan/";
    public static final String saveCalonDonatur = baseURL + "Donatur/add_calon_donatur/";
}
