/*
Copyright 2019 The Alcor Authors.

Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.futurewei.alcor.securitygroup.controller;

import com.futurewei.alcor.securitygroup.service.SecurityGroupRuleService;
import com.futurewei.alcor.web.entity.securitygroup.SecurityGroupRule;
import com.futurewei.alcor.web.entity.securitygroup.SecurityGroupRuleBulkJson;
import com.futurewei.alcor.web.entity.securitygroup.SecurityGroupRuleJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.futurewei.alcor.securitygroup.utils.RestParameterValidator.*;

@RestController
public class SecurityGroupRuleController {
    @Autowired
    private SecurityGroupRuleService securityGroupRuleService;

    @PostMapping({"/project/{project_id}/security-group-rules", "v4/{project_id}/security-group-rules"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityGroupRuleJson createSecurityGroupRule(@PathVariable("project_id") String projectId,
                                                     @RequestBody SecurityGroupRuleJson securityGroupRuleJson) throws Exception {
        checkProjectId(projectId);
        checkSecurityGroupRule(securityGroupRuleJson);
        SecurityGroupRule securityGroupRule = securityGroupRuleJson.getSecurityGroupRule();
        checkSecurityGroupRule(securityGroupRule);
        securityGroupRule.setProjectId(projectId);

        return securityGroupRuleService.createSecurityGroupRule(securityGroupRuleJson);
    }

    @PostMapping({"/project/{project_id}/security-group-rules/bulk", "v4/{project_id}/security-group-rules/bulk"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityGroupRuleBulkJson createSecurityGroupRuleBulk(@PathVariable("project_id") String projectId,
                                                         @RequestBody SecurityGroupRuleBulkJson securityGroupRuleBulkJson) throws Exception {
        checkProjectId(projectId);
        checkSecurityGroupRules(securityGroupRuleBulkJson);

        for (SecurityGroupRule securityGroupRule: securityGroupRuleBulkJson.getSecurityGroupRules()) {
            checkSecurityGroupRule(securityGroupRule);
            securityGroupRule.setProjectId(projectId);
        }

        return securityGroupRuleService.createSecurityGroupRuleBulk(securityGroupRuleBulkJson);
    }

    /*
    @PutMapping({"/project/{project_id}/security-group-rules/{security_group_rule_id}", "v4/{project_id}/security-group-rules/{security_group_rule_id}"})
    public SecurityGroupRuleJson updateSecurityGroupRule(@PathVariable("project_id") String projectId,
                                                 @PathVariable("security_group_rule_id") String securityGroupRuleId,
                                                 @RequestBody SecurityGroupRuleJson securityGroupRuleJson) throws Exception {
        RestPreconditionsUtil.verifyParameterNotNullorEmpty(projectId);
        RestPreconditionsUtil.verifyParameterNotNullorEmpty(securityGroupRuleJson);
        RestPreconditionsUtil.verifyParameterNotNullorEmpty(securityGroupRuleId);
        SecurityGroupRule securityGroupRule = securityGroupRuleJson.getSecurityGroupRule();
        RestPreconditionsUtil.verifyParameterNotNullorEmpty(securityGroupRule);

        return securityGroupRuleService.updateSecurityGroupRule(projectId, securityGroupRuleId, securityGroupRuleJson);
    }*/

    @DeleteMapping({"/project/{project_id}/security-group-rules/{security_group_rule_id}", "v4/{project_id}/security-group-rules/{security_group_rule_id}"})
    public void deleteSecurityGroupRule(@PathVariable("project_id") String projectId,
                                    @PathVariable("security_group_rule_id") String securityGroupRuleId) throws Exception {
        checkProjectId(projectId);
        checkSecurityGroupRuleId(securityGroupRuleId);

        securityGroupRuleService.deleteSecurityGroupRule(securityGroupRuleId);
    }

    @GetMapping({"/project/{project_id}/security-group-rules/{security_group_rule_id}", "v4/{project_id}/security-group-rules/{security_group_rule_id}"})
    public SecurityGroupRuleJson getSecurityGroupRule(@PathVariable("project_id") String projectId,
                                              @PathVariable("security_group_rule_id") String securityGroupRuleId) throws Exception {
        checkProjectId(projectId);
        checkSecurityGroupRuleId(securityGroupRuleId);

        return securityGroupRuleService.getSecurityGroupRule(securityGroupRuleId);
    }

    @GetMapping({"/project/{project_id}/security-group-rules", "v4/{project_id}/security-group-rules"})
    public List<SecurityGroupRuleJson> listSecurityGroupRule(@PathVariable("project_id") String projectId) throws Exception {
        checkProjectId(projectId);

        return securityGroupRuleService.listSecurityGroupRule();
    }
}
