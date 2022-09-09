package iiht.Company.repository;

import iiht.Company.pojo.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company,String> {

    Optional<Company> findByCompanyCode(String companyCode);
}
