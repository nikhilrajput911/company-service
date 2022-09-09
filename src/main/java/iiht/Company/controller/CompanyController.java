package iiht.Company.controller;

import iiht.Company.pojo.AddCompanyResponse;
import iiht.Company.pojo.BaseResponse;
import iiht.Company.pojo.Company;
import iiht.Company.pojo.CompanyLookupResponse;
import iiht.Company.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1.0/market/company")
@Api(value = "REST APIs to Add/Get/Delete company")
public class CompanyController {

    private final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    @ApiOperation(value = "${api.add-company.description}", notes = "${api.add-company.notes}")
    public ResponseEntity<BaseResponse> addCompany(@RequestBody Company company){
        String companyCode = company.getCompanyCode();
        logger.info("Company Code: " + companyCode);
        String id = UUID.randomUUID().toString();
        company.setId(id);
        try {
            if (companyService.isCompanyAlreadyPresent(companyCode)) {
                String safeErrorMessage = MessageFormat.format("Company already registered with company code - {0}", companyCode);
                logger.warn(safeErrorMessage);
                return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.BAD_REQUEST);
            }
            companyService.save(company);
            return new ResponseEntity<>(new AddCompanyResponse("Company registration request completed successfully!", id), HttpStatus.CREATED);
        } catch(IllegalStateException e) {
            logger.warn(MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            String safeErrorMessage = MessageFormat.format("Error while processing request to register company for id - {0}.", id);
            logger.error(safeErrorMessage, e);
            return new ResponseEntity<>(new AddCompanyResponse(safeErrorMessage, id), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getall")
    @ApiOperation(value = "${api.get-all-company.description}", notes = "${api.get-all-company.notes}")
    public ResponseEntity<CompanyLookupResponse> listOfCompany() {
        try {
            List<Company> companyList = (List<Company>) companyService.findAll();
            if (companyList == null || companyList.size() == 0) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            CompanyLookupResponse response = CompanyLookupResponse.builder()
                    .numberOfCompany(companyList.size())
                    .companies(companyList)
                    .message(MessageFormat.format("Successfully returned {0} company(s)!", companyList.size()))
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            String safeErrorMessage = "Failed to get company(s)!";
            logger.error(safeErrorMessage, e);
            return new ResponseEntity<>(new CompanyLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info/{companyCode}")
    @ApiOperation(value = "${api.get-company-by-company-code.description}", notes = "${api.get-company-by-company-code.notes}")
    public ResponseEntity<Company> viewCompany(@PathVariable("companyCode") String companyCode) {
        try {
            Optional<Company> companyOptional = companyService.findByCompanyCode(companyCode);
            if (companyOptional.isPresent()) {
                return new ResponseEntity<>(companyOptional.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String safeErrorMessage = "Failed to get company!";
            logger.error(safeErrorMessage, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/isCompanyPresent/{companyCode}")
    @ApiOperation(value = "${api.is-company-present.description}", notes = "${api.is-company-present.notes}")
    public ResponseEntity<Boolean> isCompanyPresent(@PathVariable ("companyCode") String companyCode){
        try {
            Optional<Company> companyOptional = companyService.findByCompanyCode(companyCode);
            if (companyOptional.isPresent()) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
            }
        } catch (Exception e) {
            String safeErrorMessage = "Failed to get company!";
            logger.error(safeErrorMessage, e);
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "${api.delete-company.description}", notes = "${api.delete-company.notes}")
    public ResponseEntity<BaseResponse> deleteCompany(@PathVariable String id) {
        try {
            String message = companyService.deleteCompany(id);
            logger.info(message);
            return new ResponseEntity<>(new BaseResponse(message), HttpStatus.OK);
        } catch (Exception e) {
            String safeErrorMessage = MessageFormat.format("Error while processing request to delete company with id - {0}.", id);
            logger.error(safeErrorMessage, e);
            return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
