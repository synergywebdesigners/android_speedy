package br.liveo.utils;

public class Locations {
	
	public static double SOURCE_LAT ;
	public static double SOURCE_LOGI ;
	public static double DESTINATION_LAT ;
	public static double DESTINATION_LOGI ;
	public static double CURRENT_LAT ;
	public static double CURRENT_LOGI ;
	public static double SEARCHED_LAT ;
	public static double SEARCHED_LOGI ;
	
	public static void clearAllLocation() {
		
		SOURCE_LAT = 0;
		SOURCE_LOGI=0;
		DESTINATION_LAT=0;
		DESTINATION_LOGI=0;
		SEARCHED_LAT=CURRENT_LAT;
		SEARCHED_LOGI=CURRENT_LOGI;
		
	}

}
