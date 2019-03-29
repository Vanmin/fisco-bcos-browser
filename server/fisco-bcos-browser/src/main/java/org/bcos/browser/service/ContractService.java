package org.bcos.browser.service;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bcos.browser.base.ConstantCode;
import org.bcos.browser.base.exception.BaseException;
import org.bcos.browser.entity.base.BasePageResponse;
import org.bcos.browser.entity.base.BaseResponse;
import org.bcos.browser.entity.dto.Contract;
import org.bcos.browser.entity.req.ReqContracts;
import org.bcos.browser.mapper.ContractMapper;
import org.bcos.browser.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import static org.bcos.browser.util.CommonUtils.readZipFile;

@Slf4j
@Service
public class ContractService {
    @Autowired
    ContractMapper contractMapper;

    /**
     * addContract.
     * 
     * @param contracts info
     * @return
     */
    public BaseResponse addContract(ReqContracts contracts) throws BaseException {
        log.info("addContract contracts:{}", contracts);
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        for (Contract loop : contracts.getData()) {
            loop.setGroupId(contracts.getGroupId());
            // TODO: 2019-03-23 去掉查重
            int count = contractMapper.getContractByName(contracts.getGroupId(), 
                    loop.getContractName());
            if (count > 0) {
                loop.setContractName(loop.getContractName() + "_" + CommonUtils.getDateDescStr());;
            }
            contractMapper.add(loop);
        }
        return response;
    }

    /**
     * addZipContracts.
     *
     * @param
     * @return
     */
    public BaseResponse addBatchContracts(File zipFile, Contract contract) throws BaseException, IOException {
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration entries = zf.entries(); entries.hasMoreElements(); ){
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            String zipEntryName = zipEntry.getName();
            String contractPath = null;
            String contractName = null;
            if (zipEntryName.contains("/")){
                contractPath = "/" + zipEntryName.substring(0,zipEntryName.lastIndexOf("/")+1);
                contractName = zipEntryName.substring(zipEntryName.lastIndexOf("/")+1, zipEntryName.length());
            }else {
                contractName = zipEntryName;
                contractPath = "/";
            }
            String contractSource = readZipFile(zipEntry, zf);
            contract.setContractName(contractName);
            contract.setContractPath(contractPath);
            contract.setContractSource(contractSource);
            contractMapper.add(contract);
        }
        return response;
    }


    /**
     * getContractList.
     * 
     * @return
     */
    public BasePageResponse getContractList(int groupId, int pageNumber, int pageSize) {
        BasePageResponse response = new BasePageResponse(ConstantCode.SUCCESS);
        int start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
        int total = contractMapper.getContractCnts(groupId);
        List<Contract> list = contractMapper.getContractList(groupId, start, pageSize);
        response.setTotalCount(total);
        response.setData(list);
        return response;
    }

    /**
     * updateContractList.
     * 
     * @param contracts info
     * @return
     */
    public BaseResponse updateContract(ReqContracts contracts) {
        log.info("updateContract contracts:{}", contracts);
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        for (Contract loop : contracts.getData()) {
            loop.setGroupId(contracts.getGroupId());
            if (StringUtils.isBlank(loop.getContractAbi())) {
                loop.setContractStatus(2);
            } else {
                loop.setContractStatus(1);
            }
            contractMapper.updateContract(loop);
        }
        return response;
    }

    /**
     * deleteContract.
     * 
     * @param groupId groupId
     * @param contractId contractId
     * @return
     */
    public BaseResponse deleteContract(int groupId, int contractId) {
        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS);
        contractMapper.deleteContract(groupId, contractId);
        return response;
    }
}
