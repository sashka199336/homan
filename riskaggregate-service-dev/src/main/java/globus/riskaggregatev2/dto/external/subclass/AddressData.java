package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AddressData {
    @JsonProperty("postal_code")
    private String postalCode;
    private String country;
    @JsonProperty("country_iso_code")
    private String countryIsoCode;
    @JsonProperty("federal_district")
    private String federalDistrict;
    private List<Metro> metro;
}
