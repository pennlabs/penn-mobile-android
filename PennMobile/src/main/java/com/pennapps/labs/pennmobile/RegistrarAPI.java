package com.pennapps.labs.pennmobile;

public class RegistrarAPI extends API {
    private final String REGISTRAR_ID       = "UPENN_OD_empF_1000401";
    private final String REGISTRAR_PASSWORD = "3qle5rfgns5d466o5tq5qnqndo";

    protected RegistrarAPI() {
        super();
        ID = REGISTRAR_ID;
        PASSWORD = REGISTRAR_PASSWORD;
        urlPath = "course_section_search?course_id=";
    }

}
