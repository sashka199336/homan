package globus.riskaggregatev2.dto.external.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

// Основные данные компании
@Data
public class CompanyData {
    private String kpp;
    private Capital capital;
    private Management management;
    private List<Founder> founders;
    private List<Manager> managers;
    @JsonProperty("branch_type")
    private String branchType;
    @JsonProperty("branch_count")
    private int branchCount;
    private String source;
    private Integer qc;
    private String hid;
    private String type;
    private CompanyState state;
    private Opf opf;
    private CompanyName name;
    private String inn;
    private String ogrn;
    private String okpo;
    private String okato;
    private String oktmo;
    private String okogu;
    private String okfs;
    private String okved;
    private List<Okved> okveds;
    private Authorities authorities;
    private CompanyDocuments documents;
    private Finance finance;
    private CompanyAddress address;
    private List<Phone> phones;
    private List<Email> emails;
    @JsonProperty("ogrn_date")
    private Long ogrnDate;
    @JsonProperty("okved_type")
    private String okvedType;
    @JsonProperty("employee_count")
    private Integer employeeCount;
}
