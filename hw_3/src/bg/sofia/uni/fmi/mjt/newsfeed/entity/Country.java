package bg.sofia.uni.fmi.mjt.newsfeed.entity;

public enum Country {

    //vvvvvvvvvvvvvvvvv AI generated code vvvvvvvvvvvvvvvvvvvvvvvv
    // In discord, it was said to ignore the constraint in the documentation for only possible value for country - US
    AE("United Arab Emirates"),
    AR("Argentina"),
    AT("Austria"),
    AU("Australia"),
    BE("Belgium"),
    BG("Bulgaria"),
    BR("Brazil"),
    CA("Canada"),
    CH("Switzerland"),
    CN("China"),
    CO("Colombia"),
    CU("Cuba"),
    CZ("Czech Republic"),
    DE("Germany"),
    EG("Egypt"),
    FR("France"),
    GB("United Kingdom"),
    GR("Greece"),
    HK("Hong Kong"),
    HU("Hungary"),
    ID("Indonesia"),
    IE("Ireland"),
    IL("Israel"),
    IN("India"),
    IT("Italy"),
    JP("Japan"),
    KR("South Korea"),
    LT("Lithuania"),
    LV("Latvia"),
    MA("Morocco"),
    MX("Mexico"),
    MY("Malaysia"),
    NG("Nigeria"),
    NL("Netherlands"),
    NO("Norway"),
    NZ("New Zealand"),
    PH("Philippines"),
    PL("Poland"),
    PT("Portugal"),
    RO("Romania"),
    RS("Serbia"),
    RU("Russia"),
    SA("Saudi Arabia"),
    SE("Sweden"),
    SG("Singapore"),
    SI("Slovenia"),
    SK("Slovakia"),
    TH("Thailand"),
    TR("Turkey"),
    TW("Taiwan"),
    UA("Ukraine"),
    US("USA"),
    VE("Venezuela"),
    ZA("South Africa");

// ^^^^^^^^^^^ AI generated code ^^^^^^^^^^^^^^^^^

    private final String countryName;

    Country(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static Country fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        for (Country country : Country.values()) {
            if (country.countryName.equalsIgnoreCase(name)) {
                return country;
            }
        }

        throw new IllegalArgumentException("Invalid country name: " + name);
    }
}