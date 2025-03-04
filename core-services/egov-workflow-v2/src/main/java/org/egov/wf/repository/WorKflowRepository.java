package org.egov.wf.repository;


import lombok.extern.slf4j.Slf4j;
import org.egov.wf.repository.querybuilder.WorkflowQueryBuilder;
import org.egov.wf.repository.rowmapper.WorkflowRowMapper;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
@Slf4j
public class WorKflowRepository {

    private WorkflowQueryBuilder queryBuilder;

    private JdbcTemplate jdbcTemplate;

    private WorkflowRowMapper rowMapper;


    @Autowired
    public WorKflowRepository(WorkflowQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate, WorkflowRowMapper rowMapper) {
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    /**
     * Executes the search criteria on the db
     * @param criteria The object containing the params to search on
     * @return The parsed response from the search query
     */
    public List<ProcessInstance> getProcessInstances(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        List<String> ids = getProcessInstanceIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);

        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }



    /**
     *
     * @param criteria
     * @return
     */
    public List<ProcessInstance> getProcessInstancesForUserInbox(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getStatus()))
            return new LinkedList<>();

        List<String> ids = getInboxSearchIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }


    /**
     * Returns the count based on the search criteria
     * @param criteria
     * @return
     */
    public Integer getInboxCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getInboxCount(criteria, preparedStmtList);

        log.info("getInboxCount Query: "+ query );
        log.info("getInboxCount preparedStmtList.toString(): "+ preparedStmtList.toString() );
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }

    public Integer getProcessInstancesCount(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceCount(criteria, preparedStmtList);
        log.info("getProcessInstancesCount Query: "+ query );
        log.info("getProcessInstancesCount preparedStmtList.toString(): "+ preparedStmtList.toString() );
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }



    private List<String> getInboxSearchIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getInboxIdQuery(criteria,preparedStmtList,Boolean.FALSE);
        log.info(" getInboxSearchIds query: "+query+" params: "+preparedStmtList.toString());
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }

    private List<String> getProcessInstanceIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceIds(criteria,preparedStmtList);
        log.info(" getProcessInstanceIds query: "+query+" params: "+preparedStmtList.toString());
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }


}