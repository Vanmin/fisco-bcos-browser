package org.bcos.browser.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.bcos.browser.entity.dto.Contract;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractMapper {

    void add(Contract contract);

    int getContractByName(@Param(value = "groupId") int groupId, 
            @Param(value = "contractName") String contractName);

    int getContractByNameAndPath(
            @Param(value = "contractName") String contractName,
            @Param(value = "contratPath") String contractPath);

    
    int getContractCnts(@Param(value = "groupId") int groupId);
    
    List<Contract> getContractList(@Param(value = "groupId") int groupId,
            @Param(value = "start") int start,
            @Param(value = "pageSize") int pageSize);

    void updateContract(Contract contract);

    void deleteContract(@Param(value = "groupId") int groupId,
            @Param(value = "contractId") int contractId);
}
